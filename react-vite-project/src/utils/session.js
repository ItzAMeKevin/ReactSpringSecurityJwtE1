export function getSessionCode(dateStr) {
    const d = new Date(dateStr);
    const month = d.getUTCMonth() + 1;
    const year = d.getUTCFullYear();
    const prefix = month <= 4 ? "H" : month <= 8 ? "E" : "A";
    return `${prefix}${year}`;
}

export function getCurrentSessionCode() {
    return getSessionCode(new Date());
}

// Returns sorted descending (A2026, H2026, A2025, …) with no duplicates
export function getSessionsFromOffers(offers) {
    const codes = new Set(offers.map(o => getSessionCode(o.startingDate)));
    return [...codes].sort().reverse();
}
