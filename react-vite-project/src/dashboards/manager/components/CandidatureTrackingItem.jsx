import { useState } from "react";
import { useTranslation } from "react-i18next";
import { btnTextLink, btnTextSuccess, textMuted } from "@/styles/classes.js";
import { formatSlotDate } from "@/utils/dateFormatting.js";
import getProgramName from "@/utils/getProgramName.js";
import formatSalary from "@/utils/formatSalary.js";
import { getPhaseKey } from "@/dashboards/manager/candidaturePhases.js";
import CandidatureProgress from "./CandidatureProgress.jsx";
import ContractGenerationModal from "./ContractGenerationModal.jsx";

const formatDate = (dateString, locale, fallback) => {
    if (!dateString) return fallback;
    try {
        return formatSlotDate(dateString.split('T')[0], locale);
    } catch {
        return fallback;
    }
};

const DetailSection = ({ title, children, className = "" }) => (
    <div className={`bg-white rounded-lg border border-gray-200 p-4 ${className}`}>
        <h3 className="font-semibold text-gray-900 mb-3">{title}</h3>
        {children}
    </div>
);

const DetailRow = ({ label, value, highlighted = false }) => (
    <div className={`flex justify-between py-2 ${highlighted ? "bg-blue-50 px-2 rounded" : ""}`}>
        <span className="text-sm font-medium text-gray-600">{label}</span>
        <span className={`text-sm ${highlighted ? "font-semibold text-blue-900" : "text-gray-900"}`}>
            {value}
        </span>
    </div>
);

const CandidatureTrackingItem = ({ candidature, user }) => {
    const { t, i18n } = useTranslation();
    const [showDetails, setShowDetails] = useState(false);
    const [showContractModal, setShowContractModal] = useState(false);

    const canGenerateContract = getPhaseKey(candidature.status) === "contract";
    const programs = t("inscription.programsList", { returnObjects: true });
    const salary = candidature.jobOffer?.salary;
    const na = t("common.na");
    const empty = t("common.empty");

    return (
        <>
            <tr className="border-b border-brand/20 hover:bg-brand/5 transition-colors">
                <td className="px-4 py-3">
                    <div className="font-semibold text-main">
                        {candidature.student?.firstName} {candidature.student?.lastName}
                    </div>
                    <div className="text-sm text-main/70">
                        {t("candidatureTracking.table.matricule")}: {candidature.student?.matricule || na}
                    </div>
                </td>
                <td className="px-4 py-3">
                    <div className="font-semibold text-main">
                        {candidature.jobOffer?.companyName || na}
                    </div>
                    <div className="text-sm text-main/70">
                        {candidature.jobOffer?.title || na}
                    </div>
                </td>
                <td className="px-4 py-3">
                    <CandidatureProgress status={candidature.status} />
                    <div className={`${textMuted} mt-1 text-xs`}>
                        {t("candidatureTracking.table.lastModified")}: {formatDate(candidature.updatedAt || candidature.appliedAt, i18n.language, na)}
                    </div>
                </td>
                <td className="px-4 py-3 text-center">
                    <div className="flex flex-col items-center gap-1">
                        <button
                            type="button"
                            onClick={() => setShowDetails(!showDetails)}
                            className={btnTextLink}
                        >
                            {showDetails ? t("candidatureTracking.hideDetails") : t("candidatureTracking.viewDetails")}
                        </button>
                        {canGenerateContract && (
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
                <tr className="bg-gray-50 border-b border-brand/20">
                    <td colSpan="4" className="px-6 py-6">
                        <div className="max-w-6xl mx-auto">
                            {/* En-tête avec statut */}
                            <div className="mb-6">
                                <div className="flex items-center justify-between mb-4">
                                    <h2 className="text-xl font-bold text-gray-900">
                                        {t("candidatureTracking.details.candidatureSummary")}
                                    </h2>
                                    <CandidatureProgress status={candidature.status} />
                                </div>
                            </div>

                            {/* Grille de sections */}
                            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-4">
                                {/* Section Étudiant */}
                                <DetailSection title={t("candidatureTracking.details.student")}>
                                    <DetailRow
                                        label={t("candidatureTracking.details.name")}
                                        value={`${candidature.student?.firstName} ${candidature.student?.lastName}`}
                                        highlighted
                                    />
                                    <DetailRow
                                        label={t("candidatureTracking.details.email")}
                                        value={candidature.student?.email || na}
                                    />
                                    <DetailRow
                                        label={t("candidatureTracking.details.programme")}
                                        value={candidature.student?.programe ? getProgramName(programs, candidature.student.programe) : na}
                                    />
                                </DetailSection>

                                {/* Section Offre */}
                                <DetailSection title={t("candidatureTracking.details.offer")}>
                                    <DetailRow
                                        label={t("candidatureTracking.details.position")}
                                        value={candidature.jobOffer?.title || na}
                                        highlighted
                                    />
                                    <DetailRow
                                        label={t("candidatureTracking.details.company")}
                                        value={candidature.jobOffer?.companyName || na}
                                    />
                                    <DetailRow
                                        label={t("candidatureTracking.details.salary")}
                                        value={salary != null ? formatSalary(salary, t) : na}
                                    />
                                </DetailSection>
                            </div>

                            {/* Section Description du poste */}
                            <DetailSection title={t("candidatureTracking.details.positionDetails")}>
                                <div className="space-y-4">
                                    <div>
                                        <p className="text-sm font-semibold text-gray-700 mb-2">
                                            {t("candidatureTracking.details.description")}
                                        </p>
                                        <p className="text-sm text-gray-600 leading-relaxed bg-gray-50 p-3 rounded border border-gray-200">
                                            {candidature.jobOffer?.description || empty}
                                        </p>
                                    </div>
                                    <div>
                                        <p className="text-sm font-semibold text-gray-700 mb-2">
                                            {t("candidatureTracking.details.prerequisites")}
                                        </p>
                                        <p className="text-sm text-gray-600 leading-relaxed bg-gray-50 p-3 rounded border border-gray-200">
                                            {candidature.jobOffer?.prerequisites || empty}
                                        </p>
                                    </div>
                                </div>
                            </DetailSection>

                            {/* Grille Calendrier et Contact */}
                            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mt-4">
                                {/* Section Calendrier */}
                                <DetailSection title={t("candidatureTracking.details.timeline")}>
                                    <DetailRow
                                        label={t("candidatureTracking.details.startDate")}
                                        value={formatDate(candidature.jobOffer?.startingDate, i18n.language, na)}
                                    />
                                    <DetailRow
                                        label={t("candidatureTracking.details.duration")}
                                        value={`${candidature.jobOffer?.durationInWeeks || na} ${t("candidatureTracking.details.weeks")}`}
                                    />
                                </DetailSection>

                                {/* Section Contact Employeur */}
                                <DetailSection title={t("candidatureTracking.details.employerContact")}>
                                    <DetailRow
                                        label={t("candidatureTracking.details.email")}
                                        value={candidature.employer?.email || na}
                                    />
                                    <DetailRow
                                        label={t("candidatureTracking.details.phone")}
                                        value={candidature.employer?.phoneNumber || na}
                                    />
                                </DetailSection>
                            </div>

                            {/* Section Dates de candidature */}
                            <DetailSection title={t("candidatureTracking.details.dates")} className="mt-4">
                                <DetailRow
                                    label={t("candidatureTracking.details.submitted")}
                                    value={formatDate(candidature.appliedAt, i18n.language, na)}
                                />
                                <DetailRow
                                    label={t("candidatureTracking.details.lastModified")}
                                    value={formatDate(candidature.updatedAt, i18n.language, na)}
                                />
                            </DetailSection>
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
