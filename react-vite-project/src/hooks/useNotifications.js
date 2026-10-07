import { useCallback, useEffect, useRef, useState } from "react";
import fetcher from "../utils/fetcher.js";

const POLLING_INTERVAL = 30000;

const useNotifications = () => {
    const [notifications, setNotifications] = useState([]);
    const [status, setStatus] = useState("loading");
    const [unreadCount, setUnreadCount] = useState(0);
    const pollingIntervalRef = useRef(null);

    const fetchNotifications = useCallback(async () => {
        try {
            const response = await fetcher("/etudiant/notifications");
            if (!response.ok) {
                setStatus("error");
                return;
            }
            const data = await response.json();
            const enrichedData = data.map((n) => ({
                ...n,
                actionUrl: n.type === "CV_REJECTED" && n.studentCv ? `/student-cv/${n.studentCv.id}/review` : null,
            }));
            setNotifications(enrichedData);
            const unread = enrichedData.filter((n) => !n.isRead).length;
            setUnreadCount(unread);
            setStatus(enrichedData.length === 0 ? "empty" : "ready");
        } catch (error) {
            console.error("Erreur lors du chargement des notifications :", error);
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
            const response = await fetcher(`/etudiant/notifications/${notificationId}/read`, {
                method: "PUT",
            });
            if (!response.ok) {
                console.error("Erreur lors du marquage de la notification comme lue");
                return;
            }
            setNotifications((current) =>
                current.map((n) =>
                    n.id === notificationId ? { ...n, isRead: true } : n
                )
            );
            setUnreadCount((current) => Math.max(0, current - 1));
        } catch (error) {
            console.error("Erreur lors du marquage de la notification :", error);
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

export default useNotifications;
