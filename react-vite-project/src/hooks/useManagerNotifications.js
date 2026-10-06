import { useCallback, useEffect, useRef, useState } from "react";
import fetcher from "../utils/fetcher.js";

const POLLING_INTERVAL = 30000;

const useManagerNotifications = (enabled = true) => {
    const [notifications, setNotifications] = useState([]);
    const [status, setStatus] = useState("loading");
    const [unreadCount, setUnreadCount] = useState(0);
    const pollingIntervalRef = useRef(null);

    const fetchNotifications = useCallback(async () => {
        try {
            const response = await fetcher("/gestionnaire/notifications");
            if (!response.ok) {
                setStatus("error");
                return;
            }
            const data = await response.json();
            setNotifications(data);
            const unread = data.filter((n) => !n.isRead).length;
            setUnreadCount(unread);
            setStatus(data.length === 0 ? "empty" : "ready");
        } catch (error) {
            console.error("Error loading manager notifications:", error);
            setStatus("error");
        }
    }, []);

    useEffect(() => {
        if (!enabled) return;

        fetchNotifications();

        pollingIntervalRef.current = setInterval(() => {
            fetchNotifications();
        }, POLLING_INTERVAL);

        return () => {
            if (pollingIntervalRef.current) {
                clearInterval(pollingIntervalRef.current);
            }
        };
    }, [fetchNotifications, enabled]);

    const markAsRead = useCallback(async (notificationId) => {
        try {
            const response = await fetcher(`/gestionnaire/notifications/${notificationId}/read`, {
                method: "PUT",
            });
            if (!response.ok) {
                console.error("Error marking notification as read");
                return;
            }
            setNotifications((current) =>
                current.map((n) =>
                    n.id === notificationId ? { ...n, isRead: true } : n
                )
            );
            setUnreadCount((current) => Math.max(0, current - 1));
        } catch (error) {
            console.error("Error marking notification as read:", error);
        }
    }, []);

    const removeNotificationsByCvId = useCallback((cvId) => {
        setNotifications((current) => {
            const removedUnreadCount = current
                .filter((n) => n.cvId === cvId && !n.isRead).length;
            setUnreadCount((prev) => Math.max(0, prev - removedUnreadCount));
            return current.filter((n) => n.cvId !== cvId);
        });
    }, []);

    return {
        notifications,
        status,
        unreadCount,
        markAsRead,
        removeNotificationsByCvId,
        refetch: fetchNotifications,
    };
};

export default useManagerNotifications;
