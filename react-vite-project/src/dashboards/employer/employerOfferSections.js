export const EMPLOYER_OFFER_SECTIONS = [
    { path: "offres/validees", statuses: ["ACCEPTED", "FILLED"], countedStatus: "ACCEPTED", labelKey: "personalSpaceEmployer.nav.accepted", emptyKey: "personalSpaceEmployer.empty.accepted" },
    { path: "offres/en-attente", statuses: ["PENDING"], countedStatus: "PENDING", labelKey: "personalSpaceEmployer.nav.pending", emptyKey: "personalSpaceEmployer.empty.pending" },
    { path: "offres/refusees", statuses: ["DECLINED"], countedStatus: "DECLINED", labelKey: "personalSpaceEmployer.nav.declined", emptyKey: "personalSpaceEmployer.empty.declined" },
];
