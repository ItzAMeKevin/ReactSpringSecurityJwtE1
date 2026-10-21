import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { render, screen, fireEvent, cleanup } from "@testing-library/react";
import "@testing-library/jest-dom/vitest";
import NotificationBell from "./NotificationBell";
import useNotifications from "../hooks/useNotifications.js";


const mockNavigate = vi.hoisted(() => vi.fn());


const translations = vi.hoisted(() => ({
    "notifications.title": "Notifications",
    "notifications.ariaLabel": "Notifications",
    "notifications.loading": "Chargement...",
    "notifications.error": "Erreur lors du chargement des notifications.",
    "notifications.empty": "Aucune notification.",
    "notifications.voirFeedback": "Voir le feedback",
    "notifications.types.CV_ACCEPTED.title": "Votre CV a été accepté",
    "notifications.types.CV_ACCEPTED.message": "Votre CV a été validé par un gestionnaire.",
    "notifications.types.CV_REJECTED.title": "Votre CV a été refusé",
    "notifications.types.CV_REJECTED.message": "Veuillez consulter le commentaire et téléverser un nouveau CV.",
}));

vi.mock("react-i18next", () => ({
    useTranslation: () => ({
        t: (key) => translations[key] ?? key,
        i18n: { language: "fr" },
    }),
}));

vi.mock("react-router-dom", () => ({
    useNavigate: () => mockNavigate,
}));

vi.mock("../hooks/useNotifications.js", () => ({
    default: vi.fn(),
}));


const acceptedNotification = {
    id: 1,
    type: "CV_ACCEPTED",
    title: "Votre CV a ete accepte",
    message: "brut",
    isRead: false,
    createdAt: "2026-10-15T14:30:00Z",
    feedbackCvId: null,
};

const rejectedNotification = {
    id: 2,
    type: "CV_REJECTED",
    title: "Votre CV a ete refuse",
    message: "brut",
    isRead: false,
    createdAt: "2026-10-16T09:00:00Z",
    feedbackCvId: 55,
};

const setHook = (overrides = {}) => {
    const value = {
        notifications: [],
        status: "ready",
        unreadCount: 0,
        markAsRead: vi.fn(),
        ...overrides,
    };
    useNotifications.mockReturnValue(value);
    return value;
};

const renderBell = (hookOverrides = {}) => {
    const hook = setHook(hookOverrides);
    render(<NotificationBell />);
    return hook;
};

const openDropdown = () => {
    fireEvent.click(screen.getByRole("button", { name: "Notifications" }));
};

afterEach(() => {
    cleanup();
});

beforeEach(() => {
    vi.clearAllMocks();
});


describe("NotificationBell - ouverture du menu", () => {
    it("should be closed by default", () => {
        renderBell();

        expect(screen.getByRole("button", { name: "Notifications" })).toHaveAttribute("aria-expanded", "false");
        expect(screen.queryByRole("heading", { name: "Notifications" })).not.toBeInTheDocument();
    });

    it("should open the dropdown when clicking the bell", () => {
        renderBell();

        openDropdown();

        expect(screen.getByRole("button", { name: "Notifications" })).toHaveAttribute("aria-expanded", "true");
        expect(screen.getByRole("heading", { name: "Notifications" })).toBeInTheDocument();
    });

    it("should close the dropdown when clicking the bell a second time", () => {
        renderBell();

        openDropdown();
        openDropdown();

        expect(screen.queryByRole("heading", { name: "Notifications" })).not.toBeInTheDocument();
    });
});

describe("NotificationBell - badge de notifications non lues", () => {
    it("should not show a badge when there are no unread notifications", () => {
        renderBell({ unreadCount: 0 });

        expect(screen.queryByText("0")).not.toBeInTheDocument();
    });

    it("should show the unread count", () => {
        renderBell({ unreadCount: 2 });

        expect(screen.getByText("2")).toBeInTheDocument();
    });

    it("should show 99+ when there are more than 99 unread notifications", () => {
        renderBell({ unreadCount: 120 });

        expect(screen.getByText("99+")).toBeInTheDocument();
    });

    it("should show 99 (not 99+) at exactly 99 unread notifications", () => {
        renderBell({ unreadCount: 99 });

        expect(screen.getByText("99")).toBeInTheDocument();
        expect(screen.queryByText("99+")).not.toBeInTheDocument();
    });
});

describe("NotificationBell - états du chargement", () => {
    it("should show the loading message", () => {
        renderBell({ status: "loading" });
        openDropdown();

        expect(screen.getByText("Chargement...")).toBeInTheDocument();
    });

    it("should show the error message with the alert role", () => {
        renderBell({ status: "error" });
        openDropdown();

        expect(screen.getByRole("alert")).toHaveTextContent("Erreur lors du chargement des notifications.");
    });

    it("should show the empty message", () => {
        renderBell({ status: "empty" });
        openDropdown();

        expect(screen.getByText("Aucune notification.")).toBeInTheDocument();
    });
});

describe("NotificationBell - décision sur le CV", () => {
    it("should render the localized title and message of an accepted CV", () => {
        renderBell({ notifications: [acceptedNotification], unreadCount: 1 });
        openDropdown();

        expect(screen.getByText("Votre CV a été accepté")).toBeInTheDocument();
        expect(screen.getByText("Votre CV a été validé par un gestionnaire.")).toBeInTheDocument();
    });

    it("should render the localized title and message of a rejected CV", () => {
        renderBell({ notifications: [rejectedNotification], unreadCount: 1 });
        openDropdown();

        expect(screen.getByText("Votre CV a été refusé")).toBeInTheDocument();
        expect(
            screen.getByText("Veuillez consulter le commentaire et téléverser un nouveau CV.")
        ).toBeInTheDocument();
    });

    it("should show the feedback button only for a rejected CV", () => {
        renderBell({ notifications: [acceptedNotification, rejectedNotification], unreadCount: 2 });
        openDropdown();

        const buttons = screen.getAllByRole("button", { name: "Voir le feedback" });
        expect(buttons).toHaveLength(1);
        expect(buttons[0].closest("li")).toHaveTextContent("Votre CV a été refusé");
    });

    it("should show the creation date", () => {
        renderBell({ notifications: [acceptedNotification], unreadCount: 1 });
        openDropdown();

        const expected = new Date(acceptedNotification.createdAt).toLocaleDateString("fr");
        expect(screen.getByText(expected)).toBeInTheDocument();
    });

    it("should fall back to the raw title and message for an unknown type", () => {
        const unknown = { id: 9, type: "TYPE_INCONNU", title: "Titre brut", message: "Message brut", isRead: false };
        renderBell({ notifications: [unknown], unreadCount: 1 });
        openDropdown();

        expect(screen.getByText("Titre brut")).toBeInTheDocument();
        expect(screen.getByText("Message brut")).toBeInTheDocument();
    });

    it("should highlight an unread notification and not a read one", () => {
        const read = { ...acceptedNotification, id: 3, isRead: true };
        renderBell({ notifications: [rejectedNotification, read], unreadCount: 1 });
        openDropdown();

        const unreadItem = screen.getByText("Votre CV a été refusé").closest("li");
        const readItem = screen.getByText("Votre CV a été accepté").closest("li");

        expect(unreadItem).toHaveClass("bg-blue-50");
        expect(readItem).not.toHaveClass("bg-blue-50");
    });
});

describe("NotificationBell - marquer comme lu", () => {
    it("should mark an unread notification as read when clicked", () => {
        const hook = renderBell({ notifications: [acceptedNotification], unreadCount: 1 });
        openDropdown();

        fireEvent.click(screen.getByText("Votre CV a été accepté"));

        expect(hook.markAsRead).toHaveBeenCalledWith(1);
    });

    it("should not mark an already read notification again", () => {
        const read = { ...acceptedNotification, isRead: true };
        const hook = renderBell({ notifications: [read], unreadCount: 0 });
        openDropdown();

        fireEvent.click(screen.getByText("Votre CV a été accepté"));

        expect(hook.markAsRead).not.toHaveBeenCalled();
    });
});

describe("NotificationBell - bouton Voir le feedback", () => {
    it("should mark as read once and navigate to the student page with the CV id", () => {
        const hook = renderBell({ notifications: [rejectedNotification], unreadCount: 1 });
        openDropdown();

        fireEvent.click(screen.getByRole("button", { name: "Voir le feedback" }));

        expect(hook.markAsRead).toHaveBeenCalledTimes(1);
        expect(hook.markAsRead).toHaveBeenCalledWith(2);
        expect(mockNavigate).toHaveBeenCalledWith("/etudiant", { state: { feedbackCvId: 55 } });
    });

    it("should close the dropdown after clicking the feedback button", () => {
        renderBell({ notifications: [rejectedNotification], unreadCount: 1 });
        openDropdown();

        fireEvent.click(screen.getByRole("button", { name: "Voir le feedback" }));

        expect(screen.queryByRole("heading", { name: "Notifications" })).not.toBeInTheDocument();
    });

    it("should navigate without marking as read when the notification is already read", () => {
        const read = { ...rejectedNotification, isRead: true };
        const hook = renderBell({ notifications: [read], unreadCount: 0 });
        openDropdown();

        fireEvent.click(screen.getByRole("button", { name: "Voir le feedback" }));

        expect(hook.markAsRead).not.toHaveBeenCalled();
        expect(mockNavigate).toHaveBeenCalledWith("/etudiant", { state: { feedbackCvId: 55 } });
    });
});
