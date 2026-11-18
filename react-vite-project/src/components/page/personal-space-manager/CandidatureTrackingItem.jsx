import { useState } from "react";
import { useTranslation } from "react-i18next";
import { statusBadge, statusBadgeColor } from "../../../styles/classes.js";

const formatDate = (dateString, locale) => {
    if (!dateString) return "N/A";
    try {
        return new Date(dateString).toLocaleDateString(locale, {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
        });
    } catch {
        return "N/A";
    }
};

const CandidatureTrackingItem = ({ candidature }) => {
    const { t, i18n } = useTranslation();
    const [showDetails, setShowDetails] = useState(false);

    return (
        <>
            <tr className="border-b border-brand/20 hover:bg-brand/5 transition-colors">
                <td className="px-4 py-3">
                    <div className="font-semibold text-main">
                        {candidature.student?.firstName} {candidature.student?.lastName}
                    </div>
                    <div className="text-sm text-main/70">
                        {t("candidatureTracking.table.matricule")}: {candidature.student?.matricule || "N/A"}
                    </div>
                </td>
                <td className="px-4 py-3 text-main">
                    {candidature.jobOffer?.companyName || "N/A"}
                </td>
                <td className="px-4 py-3 text-main">
                    {candidature.jobOffer?.title || "N/A"}
                </td>
                <td className="px-4 py-3">
                    <span className={`${statusBadge} ${statusBadgeColor(candidature.status)}`}>
                        {t(`candidatureTracking.statuses.${candidature.status?.toLowerCase()}`)}
                    </span>
                </td>
                <td className="px-4 py-3 text-main">
                    {formatDate(candidature.updatedAt || candidature.createdAt, i18n.language)}
                </td>
                <td className="px-4 py-3 text-center">
                    <button
                        type="button"
                        onClick={() => setShowDetails(!showDetails)}
                        className="text-brand hover:underline font-semibold text-sm transition-colors"
                    >
                        {showDetails ? t("candidatureTracking.hideDetails") : t("candidatureTracking.viewDetails")}
                    </button>
                </td>
            </tr>
            {showDetails && (
                <tr className="bg-brand/5 border-b border-brand/20">
                    <td colSpan="6" className="px-4 py-4">
                        <div className="space-y-3">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <p className="font-semibold text-main text-sm mb-1">
                                        {t("candidatureTracking.details.student")}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {candidature.student?.firstName} {candidature.student?.lastName}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.table.matricule")}: {candidature.student?.matricule || "N/A"}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.email")}: {candidature.student?.email || "N/A"}
                                    </p>
                                </div>
                                <div>
                                    <p className="font-semibold text-main text-sm mb-1">
                                        {t("candidatureTracking.details.offer")}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.position")}: {candidature.jobOffer?.title || "N/A"}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.company")}: {candidature.jobOffer?.companyName || "N/A"}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.duration")}: {candidature.jobOffer?.durationInWeeks || "N/A"} {t("candidatureTracking.details.weeks")}
                                    </p>
                                </div>
                            </div>
                            <div>
                                <p className="font-semibold text-main text-sm mb-1">
                                    {t("candidatureTracking.details.dates")}
                                </p>
                                <p className="text-main/80 text-sm">
                                    {t("candidatureTracking.details.submitted")}: {formatDate(candidature.createdAt, i18n.language)}
                                </p>
                                <p className="text-main/80 text-sm">
                                    {t("candidatureTracking.details.lastModified")}: {formatDate(candidature.updatedAt, i18n.language)}
                                </p>
                            </div>
                        </div>
                    </td>
                </tr>
            )}
        </>
    );
};

export default CandidatureTrackingItem;
