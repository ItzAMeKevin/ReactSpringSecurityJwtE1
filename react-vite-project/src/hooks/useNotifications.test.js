import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { renderHook, waitFor, act } from "@testing-library/react";
import useNotifications from "./useNotifications.js";
import fetcher from "../utils/fetcher.js";

vi.mock("../utils/fetcher.js", () => ({
    default: vi.fn(),
}));


const okResponse = (data) => ({ ok: true, json: () => Promise.resolve(data) });
const errorResponse = () => ({ ok: false });

// Format renvoyé par GET /notifications
const acceptedNotification = {
    id: 1,
    notificationType: "CV",
    referenceId: 10,
    message: "notification.types.CV_ACCEPTED.message",
    status: "UNREAD",
    creationDate: "2026-10-15T14:30:00Z",
};

const rejectedNotification = {
    id: 2,
    notificationType: "CV",
    referenceId: 55,
    message: "notification.types.CV_DECLINED.message",
    status: "UNREAD",
    creationDate: "2026-10-16T09:00:00Z",
};

const readNotification = {
    id: 3,
    notificationType: "CV",
    referenceId: 11,
    message: "notification.types.CV_ACCEPTED.message",
    status: "READ",
    creationDate: "2026-10-14T08:00:00Z",
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

    it("should fetch the notifications of the logged-in user", async () => {
        await renderReadyHook([acceptedNotification]);

        expect(fetcher).toHaveBeenCalledWith("/notifications");
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

describe("useNotifications - normalisation", () => {
    it("should map the backend fields to the frontend shape", async () => {
        const { result } = await renderReadyHook([acceptedNotification, readNotification]);

        expect(result.current.notifications[0]).toEqual({
            id: 1,
            category: "CV",
            event: "CV_ACCEPTED",
            referenceId: 10,
            message: null,
            isRead: false,
            date: "2026-10-15T14:30:00Z",
            target: { path: "/etudiant", state: { highlight: { kind: "cv", id: 10 } } },
        });
        expect(result.current.notifications[1].isRead).toBe(true);
    });

    it("should keep a plain message that is not an event key", async () => {
        const { result } = await renderReadyHook([{ ...acceptedNotification, message: "Texte libre" }]);

        expect(result.current.notifications[0].event).toBeNull();
        expect(result.current.notifications[0].message).toBe("Texte libre");
    });

    it("should have no event when the message is missing", async () => {
        const { result } = await renderReadyHook([{ ...acceptedNotification, message: null }]);

        expect(result.current.notifications[0].event).toBeNull();
    });
});


describe("useNotifications - target", () => {
    it.each([
        ["CV_UPLOADED", "CV", "/gestionnaire", "cv"],
        ["JOB_OFFER_CREATED", "JOB_OFFER", "/gestionnaire", "offer"],
        ["CV_ACCEPTED", "CV", "/etudiant", "cv"],
        ["CV_DECLINED", "CV", "/etudiant", "cv"],
        ["JOB_OFFER_ACCEPTED", "JOB_OFFER", "/employeur", "offer"],
        ["JOB_OFFER_DECLINED", "JOB_OFFER", "/employeur", "offer"],
    ])("should lead %s to the highlighted item on %s's page", async (event, category, path, kind) => {
        const { result } = await renderReadyHook([{
            ...acceptedNotification,
            notificationType: category,
            referenceId: 7,
            message: `notification.types.${event}.message`,
        }]);

        expect(result.current.notifications[0].target).toEqual({
            path,
            state: { highlight: { kind, id: 7 } },
        });
    });

    it("should have no target for an unknown event", async () => {
        const { result } = await renderReadyHook([{
            ...acceptedNotification,
            message: "notification.types.INCONNU.message",
        }]);

        expect(result.current.notifications[0].target).toBeNull();
    });

    it("should have no target when there is no event", async () => {
        const { result } = await renderReadyHook([{ ...acceptedNotification, message: "Texte libre" }]);

        expect(result.current.notifications[0].target).toBeNull();
    });
});

describe("useNotifications - markAsRead", () => {
    it("should call the read endpoint with PATCH", async () => {
        const { result } = await renderReadyHook([acceptedNotification]);
        fetcher.mockResolvedValueOnce({ ok: true });

        await act(() => result.current.markAsRead(1));

        expect(fetcher).toHaveBeenCalledWith("/notifications?notificationId=1", { method: "PATCH" });
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
