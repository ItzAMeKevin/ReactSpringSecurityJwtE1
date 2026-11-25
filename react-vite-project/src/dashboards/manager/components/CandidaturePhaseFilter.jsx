import { useTranslation } from "react-i18next";
import { CANDIDATURE_PHASES, DECLINED_FILTER } from "@/dashboards/manager/candidaturePhases.js";

const phaseCardClass = (isActive) =>
    `flex flex-col items-start gap-1 rounded-xl border px-4 py-3 text-left transition-colors ${
        isActive ? "border-brand bg-brand/10 ring-2 ring-brand" : "border-brand/20 bg-white/40 hover:bg-brand/5"
    }`;

const declinedChipClass = (isActive) =>
    `self-end rounded-full border px-3 py-1 text-sm font-medium transition-colors ${
        isActive ? "border-red-400 bg-red-100 text-red-800" : "border-brand/20 text-main/70 hover:bg-red-50"
    }`;

const CandidaturePhaseFilter = ({ counts, value, onChange }) => {
    const { t } = useTranslation();
    const toggle = (key) => onChange(value === key ? null : key);

    return (
        <div className="flex flex-col gap-2">
            <div className="grid grid-cols-2 gap-3 md:grid-cols-4" role="group" aria-label={t("candidatureTracking.phases.filterLabel")}>
                {CANDIDATURE_PHASES.map(({ key }, index) => (
                    <button
                        key={key}
                        type="button"
                        aria-pressed={value === key}
                        onClick={() => toggle(key)}
                        className={phaseCardClass(value === key)}
                    >
                        <span className="text-xs font-semibold uppercase tracking-wide text-main/60">
                            {index + 1}. {t(`candidatureTracking.phases.${key}`)}
                        </span>
                        <span className="text-2xl font-bold text-main">{counts[key]}</span>
                    </button>
                ))}
            </div>
            <button
                type="button"
                aria-pressed={value === DECLINED_FILTER}
                onClick={() => toggle(DECLINED_FILTER)}
                className={declinedChipClass(value === DECLINED_FILTER)}
            >
                {t("candidatureTracking.phases.declined", { count: counts[DECLINED_FILTER] })}
            </button>
        </div>
    );
};

export default CandidaturePhaseFilter;
