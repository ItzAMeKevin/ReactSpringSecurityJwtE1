import { useState } from "react";
import { useTranslation } from "react-i18next";
import useNotifications from "../hooks/useNotifications.js";
import getLocalizedNotification from "../utils/getLocalizedNotification.js";

const NotificationBell = () => {
    const { t } = useTranslation();
    const { notifications, status, unreadCount, markAsRead } = useNotifications();
    const [isOpen, setIsOpen] = useState(false);

    const handleNotificationClick = (notificationId, isRead) => {
        if (!isRead) {
            markAsRead(notificationId);
        }
    };

    const toggleDropdown = () => {
        setIsOpen((current) => !current);
    };

    const renderContent = () => {
        if (status === "loading") {
            return <p className="text-sm text-[#4b1113]/70 p-3">{t("notifications.loading")}</p>;
        }

        if (status === "error") {
            return (
                <p className="text-sm text-red-700 p-3" role="alert">
                    {t("notifications.error")}
                </p>
            );
        }

        if (status === "empty") {
            return (
                <p className="text-sm text-[#4b1113]/70 p-3">{t("notifications.empty")}</p>
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
                                    ? "bg-white/40 text-[#4b1113]/70 hover:bg-white/60"
                                    : "bg-blue-50 text-[#4b1113] hover:bg-blue-100 font-medium"
                            }`}
                        >
                            <p className="line-clamp-2">{localizedNotif.title}</p>
                            {localizedNotif.message && (
                                <p className="text-xs text-[#4b1113]/60 mt-1 line-clamp-2">
                                    {localizedNotif.message}
                                </p>
                            )}
                            {notification.createdAt && (
                                <p className="text-xs text-[#4b1113]/50 mt-1">
                                    {new Date(notification.createdAt).toLocaleDateString("fr-FR")}
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
                <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg border border-[#4b1113]/20 z-50">
                    <div className="p-3 border-b border-[#4b1113]/10">
                        <h3 className="text-[#4b1113] font-bold">{t("notifications.title")}</h3>
                    </div>
                    {renderContent()}
                </div>
            )}
        </div>
    );
};

export default NotificationBell;
