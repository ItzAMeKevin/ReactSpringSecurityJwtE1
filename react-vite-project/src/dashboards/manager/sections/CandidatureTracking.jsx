import { useMemo, useState } from "react";
import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";
import CandidatureTrackingList from "@/dashboards/manager/components/CandidatureTrackingList.jsx";
import CandidaturePhaseFilter from "@/dashboards/manager/components/CandidaturePhaseFilter.jsx";
import { countByPhase, matchesPhaseFilter } from "@/dashboards/manager/candidaturePhases.js";
import { inputField, textMuted } from "@/styles/classes.js";

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
    const [phaseFilter, setPhaseFilter] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");

    const searched = useMemo(
        () => candidatures.items.filter((candidature) => matchesSearch(candidature, searchTerm)),
        [candidatures.items, searchTerm]
    );
    const counts = useMemo(() => countByPhase(searched), [searched]);
    const filtered = useMemo(
        () => searched.filter((candidature) => matchesPhaseFilter(candidature.status, phaseFilter)),
        [searched, phaseFilter]
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
            <CandidaturePhaseFilter counts={counts} value={phaseFilter} onChange={setPhaseFilter} />
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
