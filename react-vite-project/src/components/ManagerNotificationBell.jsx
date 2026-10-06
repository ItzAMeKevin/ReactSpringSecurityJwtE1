import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import useManagerNotifications from "../hooks/useManagerNotifications.js";

const ManagerNotificationBell = () => {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const { notifications, status, unreadCount, markAsRead } = useManagerNotifications();
    const [isOpen, setIsOpen] = useState(false);

    const toggleDropdown = () => {
        setIsOpen((current) => !current);
    };

    const handleConsulter = async (e, notificationId, cvId) => {
        e.stopPropagation();
        try {
            await markAsRead(notificationId);
        } catch (error) {
            console.error("Error marking as read:", error);
        }
        setIsOpen(false);
        navigate("/gestionnaire", { state: { highlightCvId: cvId } });
    };

    const getNotificationTypeLabel = (type) => {
        switch (type) {
            case "CV_UPLOADED":
                return "New CV";
            case "CV_ACCEPTED":
                return "CV Accepted";
            case "CV_REJECTED":
                return "CV Rejected";
            case "CV_REVIEW_READY":
                return "Review Ready";
            default:
                return "Notification";
        }
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
            return <p className="text-sm text-[#4b1113]/70 p-3">Loading...</p>;
        }

        if (status === "error") {
            return (
                <p className="text-sm text-red-700 p-3" role="alert">
                    Error loading notifications
                </p>
            );
        }

        if (status === "empty") {
            return (
                <p className="text-sm text-[#4b1113]/70 p-3">No notifications</p>
            );
        }

        return (
            <ul className="max-h-96 overflow-y-auto">
                {notifications.map((notification) => (
                    <li
                        key={notification.id}
                        className={`px-3 py-3 text-sm transition-colors border-b border-[#4b1113]/10 flex items-center justify-between gap-4 ${
                            notification.isRead
                                ? "bg-white text-[#4b1113]/70 hover:bg-white/80"
                                : "bg-blue-100 text-[#4b1113] hover:bg-blue-150 font-medium"
                        }`}
                    >
                        <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-2">
                                <span className="inline-block px-2 py-1 text-xs font-semibold bg-blue-200 text-blue-900 rounded">
                                    {getNotificationTypeLabel(notification.type)}
                                </span>
                            </div>
                            <p className="line-clamp-2 mt-1">{notification.title}</p>
                            {(notification.studentFirstName || notification.studentLastName) && (
                                <p className="text-xs text-[#4b1113]/60 mt-1 font-medium">
                                    {notification.studentFirstName} {notification.studentLastName}
                                    {notification.studentMatricule && ` - ${notification.studentMatricule}`}
                                </p>
                            )}
                            {notification.cvFileName && (
                                <p className="text-xs text-[#4b1113]/60">
                                    {notification.cvFileName}
                                </p>
                            )}
                            {notification.uploadedAt && (
                                <p className="text-xs text-[#4b1113]/50 mt-1">
                                    {formatDateTime(notification.uploadedAt)}
                                </p>
                            )}
                        </div>
                        <button
                            type="button"
                            onClick={(e) => handleConsulter(e, notification.id, notification.cvId)}
                            className="shrink-0 bg-[#4b1113] text-white px-4 py-2 rounded hover:bg-[#3a0d0f] transition-colors font-medium text-sm"
                        >
                            Consulter
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
                <div className="absolute right-0 mt-2 w-96 bg-white rounded-lg shadow-lg border border-[#4b1113]/20 z-50">
                    <div className="p-3 border-b border-[#4b1113]/10">
                        <h3 className="text-[#4b1113] font-bold">CVs to Review</h3>
                    </div>
                    {renderContent()}
                </div>
            )}
        </div>
    );
};

export default ManagerNotificationBell;
