const SEASON_ORDER = { H: 0, E: 1, A: 2 };
const SEASONS = Object.keys(SEASON_ORDER);

export function getSessionCode(dateStr) {
    const d = new Date(dateStr);
    const month = d.getUTCMonth() + 1;
    const year = d.getUTCFullYear();
    const prefix = month <= 4 ? "H" : month <= 8 ? "E" : "A";
    return `${prefix}${year}`;
}

export function getNextSessionCode(date = new Date()) {
    const current = getSessionCode(date);
    const year = Number(current.slice(1));
    const nextIndex = SEASON_ORDER[current[0]] + 1;
    return nextIndex < SEASONS.length ? `${SEASONS[nextIndex]}${year}` : `H${year + 1}`;
}

export function compareSessionCodesDesc(a, b) {
    const yearDiff = Number(b.slice(1)) - Number(a.slice(1));
    return yearDiff !== 0 ? yearDiff : SEASON_ORDER[b[0]] - SEASON_ORDER[a[0]];
}

export function getSessionsFromOffers(offers) {
    const codes = new Set(offers.map(o => getSessionCode(o.startingDate)));
    return [...codes].sort(compareSessionCodesDesc);
}
