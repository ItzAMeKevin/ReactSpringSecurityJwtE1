import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import useSharedNotifications from "@/hooks/useSharedNotifications.js";

const useHighlightWhenNotified = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { notifications, markAsRead } = useSharedNotifications();
    const [highlight, setHighlight] = useState(null);

    useEffect(() => {
        const next = location.state?.highlight;
        if (!next) return;
        setHighlight(next);
        navigate(location.pathname, { replace: true, state: null });
    }, [location.state, location.pathname, navigate]);

    const unreadFor = (kind, id) => notifications.filter(({ target, referenceId }) =>
        target?.path === location.pathname && target.state.highlight.kind === kind && referenceId === id
    );

    const isHighlighted = (kind, id) =>
        (highlight?.kind === kind && highlight.id === id) || unreadFor(kind, id).length > 0;

    const clearHighlight = (kind, id) => {
        setHighlight(null);
        unreadFor(kind, id).forEach((notification) => markAsRead(notification.id));
    };

    const highlightProps = (kind, id) => ({
        isHighlighted: isHighlighted(kind, id),
        onClearHighlight: () => clearHighlight(kind, id),
    });

    return { isHighlighted, highlightProps };
};

export default useHighlightWhenNotified;
