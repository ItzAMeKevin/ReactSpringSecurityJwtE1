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
            const [cvRes, offerRes] = await Promise.all([
                fetcher("/gestionnaire/notifications"),
                fetcher("/gestionnaire/offres/notifications"),
            ]);
            if (!cvRes.ok || !offerRes.ok) {
                setStatus("error");
                return;
            }
            const cvNotifs = (await cvRes.json()).map((n) => ({ ...n, source: "cv" }));
            const offerNotifs = (await offerRes.json()).map((n) => ({ ...n, source: "jobOffer" }));
            const all = [...cvNotifs, ...offerNotifs];
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
        const endpoint = source === "jobOffer"
            ? `/gestionnaire/offres/notifications/${notificationId}/read`
            : `/gestionnaire/notifications/${notificationId}/read`;
        try {
            const response = await fetcher(endpoint, { method: "PUT" });
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
                .filter((n) => n.source === "cv" && n.cvId === cvId && !n.isRead).length;
            setUnreadCount((prev) => Math.max(0, prev - removedUnreadCount));
            return current.filter((n) => !(n.source === "cv" && n.cvId === cvId));
        });
    }, []);

    const removeNotificationsByOfferId = useCallback((offerId) => {
        setNotifications((current) => {
            const removedUnreadCount = current
                .filter((n) => n.source === "jobOffer" && n.offerId === offerId && !n.isRead).length;
            setUnreadCount((prev) => Math.max(0, prev - removedUnreadCount));
            return current.filter((n) => !(n.source === "jobOffer" && n.offerId === offerId));
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
