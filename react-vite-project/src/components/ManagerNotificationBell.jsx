import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useManagerNotificationsContext } from "../context/ManagerNotificationsContext.jsx";
import getLocalizedNotification from "../utils/getLocalizedNotification.js";
import { textError } from "../styles/classes.js";

const ManagerNotificationBell = () => {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const { notifications, status, unreadCount, markAsRead } = useManagerNotificationsContext();
    const [isOpen, setIsOpen] = useState(false);

    const toggleDropdown = () => {
        setIsOpen((current) => !current);
    };

    const handleConsulter = async (e, notification) => {
        e.stopPropagation();
        try {
            await markAsRead(notification.id, notification.source);
        } catch (error) {
            console.error("Error marking as read:", error);
        }
        setIsOpen(false);
        if (notification.source === "jobOffer") {
            navigate("/gestionnaire", { state: { highlightOfferId: notification.offerId } });
        } else {
            navigate("/gestionnaire", { state: { highlightCvId: notification.cvId } });
        }
    };


    const parseStoredOfferMessage = (storedMessage) => {
        const match = (storedMessage || "").match(
            /L'entreprise "(.+?)" a déposé une offre : "(.+?)" \((.+?)\)/
        );
        if (!match) {
            return { company: "", offerTitle: "", program: "" };
        }
        return {
            company: match[1],
            offerTitle: match[2],
            program: match[3],
        };
    };

    const getNotificationMessage = (notification) => {
        const { message } = getLocalizedNotification(notification, t);
        if (notification.type === "CV_UPLOADED") {
            return message
                .replace("{firstName}", notification.studentFirstName || "")
                .replace("{lastName}", notification.studentLastName || "")
                .replace("{matricule}", notification.studentMatricule || "")
                .replace("{fileName}", notification.cvFileName || "")
                .replace("{uploadedAt}", new Date(notification.uploadedAt).toLocaleDateString(i18n.language));
        }
        if (notification.type === "JOB_OFFER_UPLOADED") {
            const parsed = parseStoredOfferMessage(notification.message);
            return message
                .replace("{company}", notification.company || parsed.company || "")
                .replace("{offerTitle}", notification.offerTitle || parsed.offerTitle || "")
                .replace("{program}", notification.program || parsed.program || "");
        }
        return message;
    };


    const formatDateTime = (date) => {
        return new Date(date).toLocaleString(i18n.language, {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const renderContent = () => {
        if (status === "loading") {
            return <p className="text-sm text-black p-3">{t("notifications.loading")}</p>;
        }

        if (status === "error") {
            return (
                <p className={`text-sm ${textError} p-3`} role="alert">
                    {t("notifications.error")}
                </p>
            );
        }

        if (status === "empty") {
            return (
                <p className="text-sm text-black p-3">{t("notifications.empty")}</p>
            );
        }

        return (
            <ul className="max-h-96 overflow-y-auto">
                {notifications.map((notification) => (
                    <li
                        key={`${notification.source}-${notification.id}`}
                        className={`px-3 py-3 text-sm transition-colors border-b border-brand/10 flex items-center justify-between gap-4 ${
                            notification.isRead
                                ? "bg-white text-black hover:bg-white/80"
                                : "bg-blue-100 text-black hover:bg-blue-150 font-medium"
                        }`}
                    >
                        <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-2">
                                <span className="inline-block px-2 py-1 text-xs font-semibold bg-blue-200 text-blue-900 rounded">
                                    {getLocalizedNotification(notification, t).title}
                                </span>
                            </div>
                            <p className="mt-1">{getNotificationMessage(notification)}</p>
                            {notification.createdAt && (
                                <p className="text-xs text-[#4b1113]/50 mt-1">
                                    {formatDateTime(notification.createdAt)}
                                </p>
                            )}
                        </div>
                        <button
                            type="button"
                            onClick={(e) => handleConsulter(e, notification)}
                            className="shrink-0 bg-brand text-white px-4 py-2 rounded hover:bg-brand-hover transition-colors font-medium text-sm"
                        >
                            {t("gestionnaire.consulter")}
                        </button>
                    </li>
                ))}
            </ul>
        );
    };

    return (
        <div className="relative">
            <button
                type="button"
                onClick={toggleDropdown}
                aria-label="Notifications"
                aria-expanded={isOpen}
                className="relative inline-flex items-center justify-center w-10 h-10 rounded-full hover:bg-white/20 transition-colors"
            >
                <svg
                    className="w-6 h-6 text-white"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
                    />
                </svg>

                {unreadCount > 0 && (
                    <span className="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white transform translate-x-1 -translate-y-1 bg-red-600 rounded-full">
                        {unreadCount > 99 ? "99+" : unreadCount}
                    </span>
                )}
            </button>

            {isOpen && (
                <div className="absolute right-0 mt-2 w-96 bg-white rounded-lg shadow-lg border border-brand/20 z-50">
                    <div className="p-3 border-b border-brand/10">
                        <h3 className="text-black font-bold">{t("notifications.managerTitle")}</h3>
                    </div>
                    {renderContent()}
                </div>
            )}
        </div>
    );
};

export default ManagerNotificationBell;
