// ── Buttons ───────────────────────────────────────────────────────────────────

/** Large primary button — used in employer/form pages */
export const btnPrimary = "bg-brand text-white px-4 py-2 rounded hover:bg-brand-soft transition-colors duration-300";

/** Small primary button — used in CV/offer list items */
export const btnSmPrimary = "bg-brand text-white text-sm font-medium py-1.5 px-3 rounded-md hover:bg-brand-hover transition-colors";

/** Accept / upload button (small) — used in manager and student pages */
export const btnAccept = "bg-accept text-brand text-sm font-bold py-1.5 px-3 rounded-md hover:bg-accept-hover transition-colors disabled:opacity-60";

/** Accept / upload button (large) — used in student upload page */
export const btnAcceptLg = "bg-accept text-brand font-bold text-lg py-2 px-4 rounded-md hover:bg-accept-hover transition-colors disabled:opacity-60";

/** Outlined brand button — used in modals (cancel actions) */
export const btnOutline = "border border-brand text-black px-4 py-2 rounded hover:bg-mint-bg transition-colors";

/** Primary action button inside modals (publish, confirm) */
export const btnModalPrimary = "bg-brand text-white text-sm font-medium px-4 py-2 rounded hover:bg-brand-soft transition-colors disabled:opacity-50";

/** Refuse / decline button (small) — used for destructive reject actions */
export const btnRefuse = "bg-red-700 text-white text-sm font-bold py-1.5 px-3 rounded-md hover:bg-red-800 transition-colors disabled:opacity-60";

// ── Cards ─────────────────────────────────────────────────────────────────────

/** CV / offer list item card */
export const cardItem = "rounded-xl border border-brand/20 bg-white/40 px-4 py-3 transition-all duration-500";

// ── Layout ────────────────────────────────────────────────────────────────────

/** Full-page wrapper for all dashboard pages */
export const pageWrapper = "relative min-h-screen overflow-hidden bg-mint-bg";

/** Inner content column for all dashboard pages */
export const pageInner = "flex flex-col items-center gap-4 pt-20 pb-20 px-4";

// ── Typography ────────────────────────────────────────────────────────────────

/** Page title (h1) */
export const pageTitle = "text-2xl font-bold text-black";

/** Section title (h2) with bottom divider */
export const sectionTitle = "text-xl font-bold text-black mb-4 pb-2 border-b border-brand/20";

/** Secondary / status text */
export const textMuted = "text-brand/70";

/** Error feedback text */
export const textError = "text-red-700";

/** Success feedback text */
export const textSuccess = "text-green-700";

/** Success toast / popup card colors */
export const successCard = "border border-green-200 bg-green-50 text-green-800";

// ── Forms ─────────────────────────────────────────────────────────────────────

/** Standard text input / select / textarea */
export const inputField = "w-full border border-brand rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-brand";
