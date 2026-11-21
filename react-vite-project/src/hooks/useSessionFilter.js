import { useMemo, useState } from "react";
import { compareSessionCodesDesc, getNextSessionCode, getSessionCode, getSessionsFromOffers } from "@/utils/session.js";

const useSessionFilter = (offers) => {
    const [selectedSession, setSelectedSession] = useState(() => getNextSessionCode());

    const sessions = useMemo(() => {
        const next = getNextSessionCode();
        const derived = getSessionsFromOffers(offers);
        if (!derived.includes(next)) derived.push(next);
        return derived.sort(compareSessionCodesDesc);
    }, [offers]);

    const filteredOffers = useMemo(
        () => offers.filter((o) => getSessionCode(o.startingDate) === selectedSession),
        [offers, selectedSession]
    );

    return { sessions, selectedSession, setSelectedSession, filteredOffers };
};

export default useSessionFilter;
