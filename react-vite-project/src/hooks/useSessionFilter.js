import { useMemo, useState } from "react";
import { compareSessionCodesDesc, getCurrentSessionCode, getSessionCode, getSessionsFromOffers } from "../utils/session.js";

const useSessionFilter = (offers) => {
    const [selectedSession, setSelectedSession] = useState(getCurrentSessionCode);

    const sessions = useMemo(() => {
        const current = getCurrentSessionCode();
        const derived = getSessionsFromOffers(offers);
        if (!derived.includes(current)) derived.push(current);
        return derived.sort(compareSessionCodesDesc);
    }, [offers]);

    const filteredOffers = useMemo(
        () => offers.filter((o) => getSessionCode(o.startingDate) === selectedSession),
        [offers, selectedSession]
    );

    return { sessions, selectedSession, setSelectedSession, filteredOffers };
};

export default useSessionFilter;
