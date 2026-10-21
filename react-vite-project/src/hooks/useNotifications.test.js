import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { renderHook, waitFor, act } from "@testing-library/react";
import useNotifications from "./useNotifications.js";
import fetcher from "../utils/fetcher.js";

vi.mock("../utils/fetcher.js", () => ({
    default: vi.fn(),
}));


const okResponse = (data) => ({ ok: true, json: () => Promise.resolve(data) });
const errorResponse = () => ({ ok: false });

const acceptedNotification = {
    id: 1,
    type: "CV_ACCEPTED",
    isRead: false,
    studentCv: { id: 10 },
};

const rejectedNotification = {
    id: 2,
    type: "CV_REJECTED",
    isRead: false,
    studentCv: { id: 55 },
};

const readNotification = {
    id: 3,
    type: "CV_ACCEPTED",
    isRead: true,
    studentCv: { id: 11 },
};

const renderReadyHook = async (data) => {
    fetcher.mockResolvedValueOnce(okResponse(data));
    const hook = renderHook(() => useNotifications());
    await waitFor(() => expect(hook.result.current.status).not.toBe("loading"));
    return hook;
};

beforeEach(() => {
    vi.clearAllMocks();
});

afterEach(() => {
    vi.useRealTimers();
});


describe("useNotifications - chargement", () => {
    it("should start in the loading state", () => {
        fetcher.mockReturnValue(new Promise(() => {}));

        const { result } = renderHook(() => useNotifications());

        expect(result.current.status).toBe("loading");
        expect(result.current.notifications).toEqual([]);
    });

    it("should fetch the student notifications", async () => {
        await renderReadyHook([acceptedNotification]);

        expect(fetcher).toHaveBeenCalledWith("/etudiant/notifications");
    });

    it("should be ready with the notifications and the unread count", async () => {
        const { result } = await renderReadyHook([acceptedNotification, rejectedNotification, readNotification]);

        expect(result.current.status).toBe("ready");
        expect(result.current.notifications).toHaveLength(3);
        expect(result.current.unreadCount).toBe(2);
    });

    it("should be empty when the student has no notification", async () => {
        const { result } = await renderReadyHook([]);

        expect(result.current.status).toBe("empty");
        expect(result.current.unreadCount).toBe(0);
    });

    it("should be in error when the response is not ok", async () => {
        fetcher.mockResolvedValueOnce(errorResponse());

        const { result } = renderHook(() => useNotifications());

        await waitFor(() => expect(result.current.status).toBe("error"));
    });

    it("should be in error when the request throws", async () => {
        const consoleError = vi.spyOn(console, "error").mockImplementation(() => {});
        fetcher.mockRejectedValueOnce(new Error("network error"));

        const { result } = renderHook(() => useNotifications());

        await waitFor(() => expect(result.current.status).toBe("error"));
        expect(consoleError).toHaveBeenCalled();

        consoleError.mockRestore();
    });
});

describe("useNotifications - feedbackCvId", () => {
    it("should set feedbackCvId to the CV id for a rejected CV", async () => {
        const { result } = await renderReadyHook([rejectedNotification]);

        expect(result.current.notifications[0].feedbackCvId).toBe(55);
    });

    it("should set feedbackCvId to null for an accepted CV", async () => {
        const { result } = await renderReadyHook([acceptedNotification]);

        expect(result.current.notifications[0].feedbackCvId).toBeNull();
    });

    it("should set feedbackCvId to null for a rejected CV without studentCv", async () => {
        const { result } = await renderReadyHook([{ ...rejectedNotification, studentCv: null }]);

        expect(result.current.notifications[0].feedbackCvId).toBeNull();
    });
});

describe("useNotifications - markAsRead", () => {
    it("should call the read endpoint with PUT", async () => {
        const { result } = await renderReadyHook([acceptedNotification]);
        fetcher.mockResolvedValueOnce({ ok: true });

        await act(() => result.current.markAsRead(1));

        expect(fetcher).toHaveBeenCalledWith("/etudiant/notifications/1/read", { method: "PUT" });
    });

    it("should mark the notification as read and decrement the unread count", async () => {
        const { result } = await renderReadyHook([acceptedNotification, rejectedNotification]);
        fetcher.mockResolvedValueOnce({ ok: true });

        await act(() => result.current.markAsRead(2));

        expect(result.current.notifications.find((n) => n.id === 2).isRead).toBe(true);
        expect(result.current.notifications.find((n) => n.id === 1).isRead).toBe(false);
        expect(result.current.unreadCount).toBe(1);
    });

    it("should change nothing when the response is not ok", async () => {
        const consoleError = vi.spyOn(console, "error").mockImplementation(() => {});
        const { result } = await renderReadyHook([acceptedNotification]);
        fetcher.mockResolvedValueOnce(errorResponse());

        await act(() => result.current.markAsRead(1));

        expect(result.current.notifications[0].isRead).toBe(false);
        expect(result.current.unreadCount).toBe(1);
        expect(consoleError).toHaveBeenCalled();

        consoleError.mockRestore();
    });

    it("should change nothing when the request throws", async () => {
        const consoleError = vi.spyOn(console, "error").mockImplementation(() => {});
        const { result } = await renderReadyHook([acceptedNotification]);
        fetcher.mockRejectedValueOnce(new Error("network error"));

        await act(() => result.current.markAsRead(1));

        expect(result.current.notifications[0].isRead).toBe(false);
        expect(result.current.unreadCount).toBe(1);
        expect(consoleError).toHaveBeenCalled();

        consoleError.mockRestore();
    });
});

describe("useNotifications - rafraîchissement automatique", () => {
    it("should fetch the notifications again every 30 seconds", async () => {
        vi.useFakeTimers();
        fetcher.mockResolvedValue(okResponse([acceptedNotification]));

        renderHook(() => useNotifications());
        await act(() => vi.advanceTimersByTimeAsync(0));
        expect(fetcher).toHaveBeenCalledTimes(1);

        await act(() => vi.advanceTimersByTimeAsync(30000));
        expect(fetcher).toHaveBeenCalledTimes(2);
    });

    it("should stop polling when unmounted", async () => {
        vi.useFakeTimers();
        fetcher.mockResolvedValue(okResponse([]));

        const { unmount } = renderHook(() => useNotifications());
        await act(() => vi.advanceTimersByTimeAsync(0));
        unmount();

        await act(() => vi.advanceTimersByTimeAsync(60000));
        expect(fetcher).toHaveBeenCalledTimes(1);
    });

    it("should show a decision received after the first load", async () => {
        vi.useFakeTimers();
        fetcher
            .mockResolvedValueOnce(okResponse([]))
            .mockResolvedValueOnce(okResponse([rejectedNotification]));

        const { result } = renderHook(() => useNotifications());
        await act(() => vi.advanceTimersByTimeAsync(0));
        expect(result.current.status).toBe("empty");

        await act(() => vi.advanceTimersByTimeAsync(30000));
        expect(result.current.status).toBe("ready");
        expect(result.current.unreadCount).toBe(1);
    });
});
