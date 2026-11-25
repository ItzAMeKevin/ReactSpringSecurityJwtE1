import { useMemo, useState } from "react";
import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";
import CandidatureTrackingList from "@/dashboards/manager/components/CandidatureTrackingList.jsx";
import { inputField, textMuted } from "@/styles/classes.js";

const STATUSES = ["ALL", "PENDING", "HIRE_PENDING", "DECLINED"];

const matchesSearch = ({ student, jobOffer }, term) => {
    if (!term) return true;
    const lowerTerm = term.toLowerCase();
    return [
        `${student?.firstName || ""} ${student?.lastName || ""}`,
        student?.matricule,
        jobOffer?.title,
        jobOffer?.companyName,
    ].some((value) => value?.toLowerCase().includes(lowerTerm));
};

const CandidatureTracking = () => {
    const { t } = useTranslation();
    const { candidatures } = useOutletContext();
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [searchTerm, setSearchTerm] = useState("");

    const filtered = useMemo(
        () => candidatures.items.filter((candidature) =>
            (statusFilter === "ALL" || candidature.status === statusFilter) && matchesSearch(candidature, searchTerm)
        ),
        [candidatures.items, statusFilter, searchTerm]
    );

    return (
        <DashboardSection
            title={t("candidatureTracking.subtitle")}
            status={candidatures.status}
            isEmpty={candidatures.items.length === 0}
            loadingText={t("candidatureTracking.loading")}
            errorText={t("candidatureTracking.errors.loadingFailed")}
            emptyText={t("candidatureTracking.noCandidatures")}
        >
            <div className="flex flex-col gap-2">
                <label htmlFor="status-filter" className="font-semibold text-main">
                    {t("candidatureTracking.filters.filterByStatus")}
                </label>
                <select id="status-filter" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)} className={inputField}>
                    {STATUSES.map((status) => (
                        <option key={status} value={status}>{t(`candidatureTracking.filters.${status.toLowerCase()}`)}</option>
                    ))}
                </select>
            </div>
            <div className="flex flex-col gap-2">
                <label htmlFor="search-input" className="font-semibold text-main">
                    {t("candidatureTracking.filters.searchHint")}
                </label>
                <input
                    id="search-input"
                    type="text"
                    placeholder={t("candidatureTracking.filters.searchPlaceholder")}
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className={inputField}
                />
            </div>
            {filtered.length === 0 ? (
                <p className={`${textMuted} py-8 text-center`}>{t("candidatureTracking.noResults")}</p>
            ) : (
                <>
                    <p className={textMuted}>
                        {t("candidatureTracking.resultCount", { count: filtered.length, total: candidatures.items.length })}
                    </p>
                    <CandidatureTrackingList candidatures={filtered} />
                </>
            )}
        </DashboardSection>
    );
};

export default CandidatureTracking;
