import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { render, screen, fireEvent, waitFor, cleanup } from "@testing-library/react";
import "@testing-library/jest-dom/vitest";
import ManagerNotificationBell from "./ManagerNotificationBell";
import { useManagerNotificationsContext } from "../context/ManagerNotificationsContext.jsx";



const mockNavigate = vi.hoisted(() => vi.fn());


const translations = vi.hoisted(() => ({
    "notifications.title": "Notifications",
    "notifications.loading": "Chargement...",
    "notifications.error": "Erreur lors du chargement des notifications.",
    "notifications.empty": "Aucune notification.",
    "gestionnaire.consulter": "Consulter",
    "notifications.types.JOB_OFFER_UPLOADED.title": "Nouvelle offre de stage",
    "notifications.types.JOB_OFFER_UPLOADED.message":
        "L'entreprise {company} a déposé une nouvelle offre de stage : {offerTitle} ({program}).",
    "notifications.types.CV_UPLOADED.title": "Nouveau CV à valider",
    "notifications.types.CV_UPLOADED.message":
        "L'étudiant {firstName} {lastName} (Matricule: {matricule}) a téléversé {fileName} le {uploadedAt}.",
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

vi.mock("../context/ManagerNotificationsContext.jsx", () => ({
    useManagerNotificationsContext: vi.fn(),
}));


const jobOfferNotification = {
    id: 1,
    source: "jobOffer",
    type: "JOB_OFFER_UPLOADED",
    title: "Nouvelle offre de stage",
    message: 'L\'entreprise "ACME Inc." a déposé une offre : "Développeur Java" (TECHNIQUES_INFORMATIQUE)',
    offerId: 12,
    company: "ACME Inc.",
    offerTitle: "Développeur Java",
    program: "TECHNIQUES_INFORMATIQUE",
    isRead: false,
    createdAt: "2026-10-15T14:30:00Z",
};

const cvNotification = {
    id: 2,
    source: "cv",
    type: "CV_UPLOADED",
    title: "Nouveau CV",
    message: "brut",
    cvId: 55,
    studentFirstName: "Marie",
    studentLastName: "Tremblay",
    studentMatricule: "1234567",
    cvFileName: "cv_marie.pdf",
    uploadedAt: "2026-10-10T12:00:00Z",
    isRead: false,
};

const setContext = (overrides = {}) => {
    const value = {
        notifications: [],
        status: "success",
        unreadCount: 0,
        markAsRead: vi.fn().mockResolvedValue(),
        ...overrides,
    };
    useManagerNotificationsContext.mockReturnValue(value);
    return value;
};

const renderBell = (contextOverrides = {}) => {
    const context = setContext(contextOverrides);
    render(<ManagerNotificationBell />);
    return context;
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


describe("ManagerNotificationBell - ouverture du menu", () => {
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

describe("ManagerNotificationBell - badge de notifications non lues", () => {
    it("should not show a badge when there are no unread notifications", () => {
        renderBell({ unreadCount: 0 });

        expect(screen.queryByText("0")).not.toBeInTheDocument();
    });

    it("should show the unread count", () => {
        renderBell({ unreadCount: 3 });

        expect(screen.getByText("3")).toBeInTheDocument();
    });

    it("should show 99+ when there are more than 99 unread notifications", () => {
        renderBell({ unreadCount: 150 });

        expect(screen.getByText("99+")).toBeInTheDocument();
    });

    it("should show 99 (not 99+) at exactly 99 unread notifications", () => {
        renderBell({ unreadCount: 99 });

        expect(screen.getByText("99")).toBeInTheDocument();
        expect(screen.queryByText("99+")).not.toBeInTheDocument();
    });
});

describe("ManagerNotificationBell - états du chargement", () => {
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

describe("ManagerNotificationBell - notification d'offre de stage (JOB_OFFER_UPLOADED)", () => {
    it("should render the localized title and the message with the offer information", () => {
        renderBell({ notifications: [jobOfferNotification], unreadCount: 1 });
        openDropdown();

        expect(screen.getByText("Nouvelle offre de stage")).toBeInTheDocument();
        expect(
            screen.getByText(
                "L'entreprise ACME Inc. a déposé une nouvelle offre de stage : Développeur Java (TECHNIQUES_INFORMATIQUE)."
            )
        ).toBeInTheDocument();
    });

    it("should parse the stored message when the offer fields are missing", () => {
        const withoutFields = {
            ...jobOfferNotification,
            company: undefined,
            offerTitle: undefined,
            program: undefined,
        };
        renderBell({ notifications: [withoutFields], unreadCount: 1 });
        openDropdown();

        expect(
            screen.getByText(
                "L'entreprise ACME Inc. a déposé une nouvelle offre de stage : Développeur Java (TECHNIQUES_INFORMATIQUE)."
            )
        ).toBeInTheDocument();
    });

    it("should show the creation date and time", () => {
        renderBell({ notifications: [jobOfferNotification], unreadCount: 1 });
        openDropdown();

        const expected = new Date(jobOfferNotification.createdAt).toLocaleString("fr", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit",
        });
        expect(screen.getByText(expected)).toBeInTheDocument();
    });

    it("should highlight an unread notification and not a read one", () => {
        const read = { ...jobOfferNotification, id: 3, isRead: true, company: "Lue Inc." };
        renderBell({ notifications: [jobOfferNotification, read], unreadCount: 1 });
        openDropdown();

        const unreadItem = screen.getByText(/ACME Inc\./).closest("li");
        const readItem = screen.getByText(/Lue Inc\./).closest("li");

        expect(unreadItem).toHaveClass("bg-blue-100");
        expect(readItem).toHaveClass("bg-white");
        expect(readItem).not.toHaveClass("bg-blue-100");
    });
});

describe("ManagerNotificationBell - autres types de notifications", () => {
    it("should render a CV_UPLOADED notification with the student information", () => {
        renderBell({ notifications: [cvNotification], unreadCount: 1 });
        openDropdown();

        const date = new Date(cvNotification.uploadedAt).toLocaleDateString("fr");
        expect(screen.getByText("Nouveau CV à valider")).toBeInTheDocument();
        expect(
            screen.getByText(
                `L'étudiant Marie Tremblay (Matricule: 1234567) a téléversé cv_marie.pdf le ${date}.`
            )
        ).toBeInTheDocument();
    });

    it("should fall back to the raw title and message for an unknown type", () => {
        const unknown = {
            id: 9,
            source: "cv",
            type: "TYPE_INCONNU",
            title: "Titre brut",
            message: "Message brut",
            isRead: false,
        };
        renderBell({ notifications: [unknown], unreadCount: 1 });
        openDropdown();

        expect(screen.getByText("Titre brut")).toBeInTheDocument();
        expect(screen.getByText("Message brut")).toBeInTheDocument();
    });

    it("should render one consult button per notification", () => {
        renderBell({ notifications: [jobOfferNotification, cvNotification], unreadCount: 2 });
        openDropdown();

        expect(screen.getAllByRole("button", { name: "Consulter" })).toHaveLength(2);
    });
});

describe("ManagerNotificationBell - bouton Consulter", () => {
    it("should mark a job offer notification as read and navigate with highlightOfferId", async () => {
        const context = renderBell({ notifications: [jobOfferNotification], unreadCount: 1 });
        openDropdown();

        fireEvent.click(screen.getByRole("button", { name: "Consulter" }));

        await waitFor(() => expect(mockNavigate).toHaveBeenCalledTimes(1));
        expect(context.markAsRead).toHaveBeenCalledWith(1, "jobOffer");
        expect(mockNavigate).toHaveBeenCalledWith("/gestionnaire", {
            state: { highlightOfferId: 12 },
        });
    });

    it("should mark a CV notification as read and navigate with highlightCvId", async () => {
        const context = renderBell({ notifications: [cvNotification], unreadCount: 1 });
        openDropdown();

        fireEvent.click(screen.getByRole("button", { name: "Consulter" }));

        await waitFor(() => expect(mockNavigate).toHaveBeenCalledTimes(1));
        expect(context.markAsRead).toHaveBeenCalledWith(2, "cv");
        expect(mockNavigate).toHaveBeenCalledWith("/gestionnaire", {
            state: { highlightCvId: 55 },
        });
    });

    it("should close the dropdown after consulting", async () => {
        renderBell({ notifications: [jobOfferNotification], unreadCount: 1 });
        openDropdown();

        fireEvent.click(screen.getByRole("button", { name: "Consulter" }));

        await waitFor(() => expect(screen.queryByRole("heading", { name: "Notifications" })).not.toBeInTheDocument());
    });

    it("should still navigate when markAsRead fails", async () => {
        const consoleError = vi.spyOn(console, "error").mockImplementation(() => {});
        renderBell({
            notifications: [jobOfferNotification],
            unreadCount: 1,
            markAsRead: vi.fn().mockRejectedValue(new Error("network error")),
        });
        openDropdown();

        fireEvent.click(screen.getByRole("button", { name: "Consulter" }));

        await waitFor(() => expect(mockNavigate).toHaveBeenCalledTimes(1));
        expect(consoleError).toHaveBeenCalled();
        expect(mockNavigate).toHaveBeenCalledWith("/gestionnaire", {
            state: { highlightOfferId: 12 },
        });

        consoleError.mockRestore();
    });
});
