import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";

const useHighlightWhenNotified = () => {
    const location = useLocation();
    const [highlightedId, setHighlightedId] = useState(null);

    useEffect(() => {
        if (location.state?.highlightCvId) {
            setHighlightedId(location.state.highlightCvId);
        }
    }, [location.state?.highlightCvId]);

    return {
        isHighlighted: (id) => highlightedId === id,
        clearHighlight: () => setHighlightedId(null),
    };
};

export default useHighlightWhenNotified;
