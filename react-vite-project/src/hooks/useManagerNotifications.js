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
            const res = await fetcher("/notifications");
            if (!res.ok) {
                setStatus("error");
                return;
            }
            const data = await res.json();
            const all = data.map((n) => ({
                ...n,
                isRead: n.status === "READ",
                type: n.notificationType,
                source: n.notificationType === "JOB_OFFER" ? "jobOffer" : "cv",
            }));
            setNotifications(all);
            setUnreadCount(all.filter((n) => !n.isRead).length);
            setStatus(all.length === 0 ? "empty" : "ready");
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

    const markAsRead = useCallback(async (notificationId, source) => {
        const endpoint = `/notifications/${notificationId}`;
        try {
            const response = await fetcher(endpoint, { method: "PATCH" });
            if (!response.ok) {
                console.error("Error marking notification as read");
                return;
            }
            setNotifications((current) =>
                current.map((n) =>
                    n.id === notificationId && n.source === source ? { ...n, isRead: true } : n
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
                .filter((n) => n.source === "cv" && n.referenceId === cvId && !n.isRead).length;
            setUnreadCount((prev) => Math.max(0, prev - removedUnreadCount));
            return current.filter((n) => !(n.source === "cv" && n.referenceId === cvId));
        });
    }, []);

    const removeNotificationsByOfferId = useCallback((offerId) => {
        setNotifications((current) => {
            const removedUnreadCount = current
                .filter((n) => n.source === "jobOffer" && n.referenceId === offerId && !n.isRead).length;
            setUnreadCount((prev) => Math.max(0, prev - removedUnreadCount));
            return current.filter((n) => !(n.source === "jobOffer" && n.referenceId === offerId));
        });
    }, []);


    return {
        notifications,
        status,
        unreadCount,
        markAsRead,
        removeNotificationsByCvId,
        removeNotificationsByOfferId,
        refetch: fetchNotifications,
    };
};

export default useManagerNotifications;
