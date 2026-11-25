export const CANDIDATURE_PHASES = [
    { key: "application", statuses: ["PENDING"] },
    { key: "interview", statuses: ["PENDING_INTERVIEW", "INTERVIEW_SCHEDULED", "INTERVIEW_COMPLETED"] },
    { key: "offer", statuses: ["HIRE_PENDING"] },
    { key: "contract", statuses: ["ACCEPTED", "AWAITING_CONTRACT", "AWAITING_SIGNATURES", "CONTRACT_SIGNED", "WORKING", "COMPLETED"] },
];

export const DECLINED_FILTER = "declined";

export const getPhaseIndex = (status) => CANDIDATURE_PHASES.findIndex((phase) => phase.statuses.includes(status));

export const getPhaseKey = (status) => CANDIDATURE_PHASES[getPhaseIndex(status)]?.key ?? null;

export const matchesPhaseFilter = (status, filter) => {
    if (filter === DECLINED_FILTER) return status === "DECLINED";
    if (filter) return getPhaseKey(status) === filter;
    return status !== "DECLINED";
};

export const countByPhase = (candidatures) =>
    candidatures.reduce(
        (counts, { status }) => {
            const key = status === "DECLINED" ? DECLINED_FILTER : getPhaseKey(status);
            if (key) counts[key] += 1;
            return counts;
        },
        { ...Object.fromEntries(CANDIDATURE_PHASES.map(({ key }) => [key, 0])), [DECLINED_FILTER]: 0 }
    );
