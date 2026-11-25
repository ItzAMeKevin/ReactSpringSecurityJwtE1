import { useState } from "react";
import { useTranslation } from "react-i18next";
import { btnTextLink, btnTextSuccess, statusBadge, statusBadgeColor } from "@/styles/classes.js";
import { formatSlotDate } from "@/utils/dateFormatting.js";
import getProgramName from "@/utils/getProgramName.js";
import formatSalary from "@/utils/formatSalary.js";
import ContractGenerationModal from "./ContractGenerationModal.jsx";

const formatDate = (dateString, locale) => {
    if (!dateString) return "N/A";
    try {
        return formatSlotDate(dateString.split('T')[0], locale);
    } catch {
        return "N/A";
    }
};

const CandidatureTrackingItem = ({ candidature, user }) => {
    const { t, i18n } = useTranslation();
    const [showDetails, setShowDetails] = useState(false);
    const [showContractModal, setShowContractModal] = useState(false);

    const isAccepted = candidature.status === "ACCEPTED";
    const programs = t("inscription.programsList", { returnObjects: true });
    const salary = candidature.jobOffer?.salary;

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
                        {t([`candidatureTracking.statuses.${candidature.status?.toLowerCase()}`, `applicationStatuses.${candidature.status}`])}
                    </span>
                </td>
                <td className="px-4 py-3 text-main">
                    {formatDate(candidature.updatedAt || candidature.appliedAt, i18n.language)}
                </td>
                <td className="px-4 py-3 text-center">
                    <div className="flex gap-2 justify-center">
                        <button
                            type="button"
                            onClick={() => setShowDetails(!showDetails)}
                            className={btnTextLink}
                        >
                            {showDetails ? t("candidatureTracking.hideDetails") : t("candidatureTracking.viewDetails")}
                        </button>
                        {isAccepted && (
                            <button
                                type="button"
                                onClick={() => setShowContractModal(true)}
                                className={btnTextSuccess}
                            >
                                {t("contractGeneration.generateButton")}
                            </button>
                        )}
                    </div>
                </td>
            </tr>
            {showDetails && (
                <tr className="bg-brand/5 border-b border-brand/20">
                    <td colSpan="6" className="px-4 py-4">
                        <div className="space-y-4">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <p className="font-semibold text-main text-sm mb-2">
                                        {t("candidatureTracking.details.student")}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        <span className="font-medium">{candidature.student?.firstName} {candidature.student?.lastName}</span>
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.email")}: {candidature.student?.email || "N/A"}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.programme")}: {candidature.student?.programe ? getProgramName(programs, candidature.student.programe) : "N/A"}
                                    </p>
                                </div>
                                <div>
                                    <p className="font-semibold text-main text-sm mb-2">
                                        {t("candidatureTracking.details.offer")}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.position")}: {candidature.jobOffer?.title || "N/A"}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.company")}: {candidature.jobOffer?.companyName || "N/A"}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.salary")}: {salary != null ? formatSalary(salary, t) : "N/A"}
                                    </p>
                                </div>
                            </div>

                            <div>
                                <p className="font-semibold text-main text-sm mb-2">
                                    {t("candidatureTracking.details.positionDetails")}
                                </p>
                                <p className="text-main/80 text-sm mb-2">
                                    <span className="font-medium">{t("candidatureTracking.details.description")}:</span>
                                </p>
                                <p className="text-main/80 text-sm mb-3 italic">
                                    {candidature.jobOffer?.description || "N/A"}
                                </p>
                                <p className="text-main/80 text-sm mb-2">
                                    <span className="font-medium">{t("candidatureTracking.details.prerequisites")}:</span>
                                </p>
                                <p className="text-main/80 text-sm italic">
                                    {candidature.jobOffer?.prerequisites || "N/A"}
                                </p>
                            </div>

                            {/* Duration & Dates */}
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <p className="font-semibold text-main text-sm mb-2">
                                        {t("candidatureTracking.details.timeline")}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.startDate")}: {formatDate(candidature.jobOffer?.startingDate, i18n.language)}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.duration")}: {candidature.jobOffer?.durationInWeeks || "N/A"} {t("candidatureTracking.details.weeks")}
                                    </p>
                                </div>
                                <div>
                                    <p className="font-semibold text-main text-sm mb-2">
                                        {t("candidatureTracking.details.employerContact")}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.email")}: {candidature.employer?.email || "N/A"}
                                    </p>
                                    <p className="text-main/80 text-sm">
                                        {t("candidatureTracking.details.phone")}: {candidature.employer?.phoneNumber || "N/A"}
                                    </p>
                                </div>
                            </div>

                            {/* Application Dates */}
                            <div>
                                <p className="font-semibold text-main text-sm mb-2">
                                    {t("candidatureTracking.details.dates")}
                                </p>
                                <p className="text-main/80 text-sm">
                                    {t("candidatureTracking.details.submitted")}: {formatDate(candidature.appliedAt, i18n.language)}
                                </p>
                                <p className="text-main/80 text-sm">
                                    {t("candidatureTracking.details.lastModified")}: {formatDate(candidature.updatedAt, i18n.language)}
                                </p>
                            </div>
                        </div>
                    </td>
                </tr>
            )}
            <ContractGenerationModal
                isOpen={showContractModal}
                onClose={() => setShowContractModal(false)}
                candidature={candidature}
                user={user}
            />
        </>
    );
};

export default CandidatureTrackingItem;
