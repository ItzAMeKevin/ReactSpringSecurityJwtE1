import { useTranslation } from "react-i18next";
import { statusBadge, statusBadgeColor } from "@/styles/classes.js";
import { CANDIDATURE_PHASES, getPhaseIndex } from "@/dashboards/manager/candidaturePhases.js";

const CandidatureProgress = ({ status }) => {
    const { t } = useTranslation();
    const currentIndex = getPhaseIndex(status);
    const statusLabel = t([`candidatureTracking.statuses.${status?.toLowerCase()}`, `applicationStatuses.${status}`]);

    return (
        <div className="flex flex-col gap-1.5">
            {currentIndex >= 0 && (
                <ol className="flex items-center" aria-label={t(`candidatureTracking.phases.${CANDIDATURE_PHASES[currentIndex].key}`)}>
                    {CANDIDATURE_PHASES.map(({ key }, index) => {
                        const isReached = index <= currentIndex;
                        return (
                            <li key={key} className="flex items-center">
                                {index > 0 && <span className={`h-0.5 w-6 ${isReached ? "bg-brand" : "bg-brand/20"}`} />}
                                <span
                                    title={t(`candidatureTracking.phases.${key}`)}
                                    className={`h-3 w-3 rounded-full ${isReached ? "bg-brand" : "border-2 border-brand/30 bg-white"}`}
                                />
                            </li>
                        );
                    })}
                </ol>
            )}
            <span className={`${statusBadge} ${statusBadgeColor(status)} self-start`}>{statusLabel}</span>
        </div>
    );
};

export default CandidatureProgress;
