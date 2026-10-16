import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import useNotifications from "../hooks/useNotifications.js";
import getLocalizedNotification from "../utils/getLocalizedNotification.js";
import { textError } from "../styles/classes.js";

const NotificationBell = () => {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const { notifications, status, unreadCount, markAsRead } = useNotifications();
    const [isOpen, setIsOpen] = useState(false);

    const handleNotificationClick = (notificationId, isRead) => {
        if (!isRead) {
            markAsRead(notificationId);
        }
    };

    const handleViewFeedback = (e, notification) => {
        e.stopPropagation();
        handleNotificationClick(notification.id, notification.isRead);
        setIsOpen(false);
        navigate("/etudiant", { state: { feedbackCvId: notification.feedbackCvId } });
    };

    const toggleDropdown = () => {
        setIsOpen((current) => !current);
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
                {notifications.map((notification) => {
                    const localizedNotif = getLocalizedNotification(notification, t);
                    return (
                        <li
                            key={notification.id}
                            onClick={() =>
                                handleNotificationClick(notification.id, notification.isRead)
                            }
                            className={`px-3 py-2 text-sm cursor-pointer transition-colors ${
                                notification.isRead
                                    ? "bg-white/40 text-brand/70 hover:bg-white/60"
                                    : "bg-blue-50 text-black hover:bg-blue-100 font-medium"
                            }`}
                        >
                            <p className="">{localizedNotif.title}</p>
                            {localizedNotif.message && (
                                <p className="text-xs text-black mt-1">
                                    {localizedNotif.message}
                                </p>
                            )}
                            {notification.feedbackCvId && (
                                <button
                                    type="button"
                                    onClick={(e) => handleViewFeedback(e, notification)}
                                    className="mt-2 bg-brand text-white text-xs font-medium py-1 px-2 rounded hover:bg-brand-hover transition-colors"
                                >
                                    {t("notifications.voirFeedback")}
                                </button>
                            )}
                            {notification.createdAt && (
                                <p className="text-xs text-black mt-1">
                                    {new Date(notification.createdAt).toLocaleDateString(i18n.language)}
                                </p>
                            )}
                        </li>
                    );
                })}
            </ul>
        );
    };

    return (
        <div className="relative">
            <button
                type="button"
                onClick={toggleDropdown}
                aria-label={t("notifications.ariaLabel")}
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
                <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg border border-brand/20 z-50">
                    <div className="p-3 border-b border-brand/10">
                        <h3 className="text-black font-bold">{t("notifications.title")}</h3>
                    </div>
                    {renderContent()}
                </div>
            )}
        </div>
    );
};

export default NotificationBell;
