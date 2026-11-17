import { useCallback, useEffect, useRef, useState } from "react";
import fetcher from "../utils/fetcher.js";

const POLLING_INTERVAL = 30000;

const EVENT_KEY_PATTERN = /\.types\.([A-Z_]+)\.message$/;

const EVENT_TARGETS = {
    CV_UPLOADED: { path: "/gestionnaire", kind: "cv" },
    JOB_OFFER_CREATED: { path: "/gestionnaire", kind: "offer" },
    JOB_OFFER_RESUBMITTED: { path: "/gestionnaire", kind: "offer" },
    CV_ACCEPTED: { path: "/etudiant", kind: "cv" },
    CV_DECLINED: { path: "/etudiant", kind: "cv" },
    JOB_OFFER_ACCEPTED: { path: "/employeur", kind: "offer" },
    JOB_OFFER_DECLINED: { path: "/employeur", kind: "offer" },
};

const getTarget = (event, referenceId) => {
    const target = EVENT_TARGETS[event];
    if (!target) return null;
    return { path: target.path, state: { highlight: { kind: target.kind, id: referenceId } } };
};

const normalizeNotification = (n) => {
    const event = n.message?.match(EVENT_KEY_PATTERN)?.[1] ?? null;
    return {
        id: n.id,
        category: n.notificationType,
        event,
        referenceId: n.referenceId,
        message: event ? null : n.message,
        isRead: n.status === "READ",
        date: n.creationDate,
        target: getTarget(event, n.referenceId),
    };
};

const useNotifications = () => {
    const [notifications, setNotifications] = useState([]);
    const [status, setStatus] = useState("loading");
    const [unreadCount, setUnreadCount] = useState(0);
    const pollingIntervalRef = useRef(null);

    const fetchNotifications = useCallback(async () => {
        try {
            const response = await fetcher("/notifications");
            if (!response.ok) {
                setStatus("error");
                return;
            }
            const data = (await response.json())
                .map(normalizeNotification)
                .sort((a, b) => new Date(b.date) - new Date(a.date));
            setNotifications(data);
            setUnreadCount(data.filter((n) => !n.isRead).length);
            setStatus(data.length === 0 ? "empty" : "ready");
        } catch (error) {
            console.error("Erreur lors du chargement des notifications :", error);
            setStatus("error");
        }
    }, []);

    useEffect(() => {
        fetchNotifications();

        pollingIntervalRef.current = setInterval(fetchNotifications, POLLING_INTERVAL);

        return () => clearInterval(pollingIntervalRef.current);
    }, [fetchNotifications]);

    const markAsRead = useCallback(async (notificationId) => {
        try {
            const response = await fetcher(`/notifications?notificationId=${notificationId}`, {
                method: "PATCH",
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
