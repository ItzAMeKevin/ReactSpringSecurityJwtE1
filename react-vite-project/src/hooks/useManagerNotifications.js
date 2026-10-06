import { useCallback, useEffect, useRef, useState } from "react";
import fetcher from "../utils/fetcher.js";

const POLLING_INTERVAL = 30000;

const useManagerNotifications = () => {
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
        fetchNotifications();

        pollingIntervalRef.current = setInterval(() => {
            fetchNotifications();
        }, POLLING_INTERVAL);

        return () => {
            if (pollingIntervalRef.current) {
                clearInterval(pollingIntervalRef.current);
            }
        };
    }, [fetchNotifications]);

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

    return {
        notifications,
        status,
        unreadCount,
        markAsRead,
        refetch: fetchNotifications,
    };
};

export default useManagerNotifications;
