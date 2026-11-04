import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

const useHighlightWhenNotified = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [highlight, setHighlight] = useState(null);

    useEffect(() => {
        const next = location.state?.highlight;
        if (!next) return;
        setHighlight(next);
        navigate(location.pathname, { replace: true, state: null });
    }, [location.state, location.pathname, navigate]);

    const isHighlighted = (kind, id) => highlight?.kind === kind && highlight.id === id;
    const clearHighlight = () => setHighlight(null);

    return { isHighlighted, clearHighlight };
};

export default useHighlightWhenNotified;
