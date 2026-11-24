import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";
import { cardItem, statusBadge, textMuted } from "@/styles/classes.js";

const EmployerHirePendingSection = () => {
    const { t, i18n } = useTranslation();
    const { hirePending } = useOutletContext();

    return (
        <DashboardSection
            title={t("personalSpaceEmployer.nav.hirePending")}
            status={hirePending.status}
            isEmpty={hirePending.items.length === 0}
            loadingText={t("employerHirePending.loading")}
            errorText={t("errors.generic")}
            emptyText={t("employerHirePending.empty")}
        >
            {hirePending.items.map((candidate) => (
                <div key={candidate.candidatureId} className={cardItem}>
                    <div className="flex flex-wrap items-start justify-between gap-2">
                        <div className="min-w-0">
                            <p className="font-bold text-main">{candidate.firstName} {candidate.lastName}</p>
                            <p className={`break-all text-sm ${textMuted}`}>{candidate.email}</p>
                        </div>
                        <span className={`${statusBadge} bg-yellow-100 text-yellow-800`}>
                            {t("employerHirePending.awaitingAnswer")}
                        </span>
                    </div>
                    {candidate.appliedAt && (
                        <p className={`mt-2 text-sm ${textMuted}`}>
                            {t("employerHirePending.appliedOn", { date: new Date(candidate.appliedAt).toLocaleDateString(i18n.language) })}
                        </p>
                    )}
                </div>
            ))}
        </DashboardSection>
    );
};

export default EmployerHirePendingSection;
