import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";
import { cardItem, statusBadge, statusBadgeColor, textMuted } from "@/styles/classes.js";

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
            {hirePending.items.map(({ id, student, jobOffer, status, updatedAt }) => (
                <div key={id} className={cardItem}>
                    <div className="flex flex-wrap items-start justify-between gap-2">
                        <div className="min-w-0">
                            <p className="font-bold text-main">{student.firstName} {student.lastName}</p>
                            <p className={`break-all text-sm ${textMuted}`}>{student.email}</p>
                        </div>
                        <span className={`${statusBadge} ${statusBadgeColor(status)}`}>
                            {t("employerHirePending.awaitingAnswer")}
                        </span>
                    </div>
                    <p className="mt-2 text-sm">
                        <span className="font-semibold">{t("employerInterviews.offer")} :</span> {jobOffer.title}
                    </p>
                    {updatedAt && (
                        <p className={`mt-1 text-sm ${textMuted}`}>
                            {t("employerHirePending.sentOn", { date: new Date(updatedAt).toLocaleDateString(i18n.language) })}
                        </p>
                    )}
                </div>
            ))}
        </DashboardSection>
    );
};

export default EmployerHirePendingSection;
