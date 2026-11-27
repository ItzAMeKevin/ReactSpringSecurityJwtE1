import { useCallback, useEffect, useRef, useState } from "react";
import fetcher from "@/utils/fetcher.js";

const POLLING_INTERVAL = 30000;

const EVENT_KEY_PATTERN = /\.types\.([A-Z_]+)\.message(?:\|(.*))?$/;

const EVENT_TARGETS = {
    CV_UPLOADED: { path: "/gestionnaire/cv", kind: "cv" },
    JOB_OFFER_CREATED: { path: "/gestionnaire/offres", kind: "offer" },
    JOB_OFFER_RESUBMITTED: { path: "/gestionnaire/offres", kind: "offer" },
    CV_ACCEPTED: { path: "/etudiant/cv/valides", kind: "cv" },
    CV_DECLINED: { path: "/etudiant/cv/refuses", kind: "cv" },
    JOB_OFFER_ACCEPTED: { path: "/employeur/offres/validees", kind: "offer" },
    JOB_OFFER_DECLINED: { path: "/employeur/offres/refusees", kind: "offer" },
    JOB_OFFER_HIRED: { path: "/etudiant/embauches", kind: "hire" },
    JOB_OFFER_HIRE_ACCEPTED: { path: "/employeur/offres/validees", kind: "offer" },
    JOB_OFFER_HIRE_DECLINED: { path: "/employeur/offres/validees", kind: "offer" },
    INTERVIEW_SCHEDULED: { path: "/etudiant/convocations", kind: "interview" },
    JOB_OFFER_CANDIDATE_REJECTED: { path: "/etudiant/offres", kind: "offer" },
    EVAL_REQUIRED: { path: "/employeur", kind: "evaluation" },
};

const getTarget = (event, referenceId) => {
    const target = EVENT_TARGETS[event];
    if (!target) return null;
    return { path: target.path, state: { highlight: { kind: target.kind, id: referenceId } } };
};

const normalizeNotification = (n) => {
    const match = n.message?.match(EVENT_KEY_PATTERN);
    const event = match?.[1] ?? null;
    const params = match?.[2] ? match[2].split("|") : [];
    return {
        id: n.id,
        category: n.notificationType,
        event,
        params,
        referenceId: n.referenceId,
        message: event ? null : n.message,
        isRead: n.status === "READ",
        date: n.creationDate,
        target: getTarget(event, n.referenceId),
    };
};

const useNotifications = (enabled = true) => {
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
                .filter((n) => !n.isRead)
                .sort((a, b) => new Date(b.date) - new Date(a.date));
            setNotifications(data);
            setUnreadCount(data.length);
            setStatus(data.length === 0 ? "empty" : "ready");
        } catch (error) {
            console.error("Erreur lors du chargement des notifications :", error);
            setStatus("error");
        }
    }, []);

    useEffect(() => {
        if (!enabled) {
            setNotifications([]);
            setUnreadCount(0);
            return;
        }
        fetchNotifications();

        pollingIntervalRef.current = setInterval(fetchNotifications, POLLING_INTERVAL);

        return () => clearInterval(pollingIntervalRef.current);
    }, [enabled, fetchNotifications]);

    const markAsRead = useCallback(async (notificationId) => {
        try {
            const response = await fetcher(`/notifications/${notificationId}/read`, {
                method: "PUT",
            });
            if (!response.ok) {
                console.error("Erreur lors du marquage de la notification comme lue");
                return;
            }
            setNotifications((current) => current.filter((n) => n.id !== notificationId));
            setUnreadCount((current) => Math.max(0, current - 1));
        } catch (error) {
            console.error("Erreur lors du marquage de la notification :", error);
        }
    }, []);

    return {
        notifications,
        status: status === "ready" && notifications.length === 0 ? "empty" : status,
        unreadCount,
        markAsRead,
        refetch: fetchNotifications,
    };
};

export default useNotifications;
