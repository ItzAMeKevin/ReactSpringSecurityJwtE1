import { useEffect, useState, useMemo } from "react";
import { useTranslation } from "react-i18next";
import fetcher from "../../../utils/fetcher.js";
import { pageWrapper, pageTitle, sectionTitle, textMuted, textError, inputField } from "../../../styles/classes.js";
import CandidatureTrackingList from "./CandidatureTrackingList.jsx";

const CandidatureTracking = () => {
    const { t } = useTranslation();
    const [candidatures, setCandidatures] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [searchTerm, setSearchTerm] = useState("");

    const STATUS_OPTIONS = useMemo(() => [
        { value: "ALL", label: t("candidatureTracking.filters.all") },
        { value: "ACCEPTED", label: t("candidatureTracking.filters.accepted") },
        { value: "DECLINED", label: t("candidatureTracking.filters.declined") },
        { value: "PENDING", label: t("candidatureTracking.filters.pending") },
    ], [t]);

    useEffect(() => {
        const fetchCandidatures = async () => {
            try {
                setLoading(true);
                setError(null);
                
                // Backend endpoint will be: /gestionnaire/candidatures
                const response = await fetcher("/gestionnaire/candidatures", {
                    method: "GET",
                });

                if (!response.ok) {
                    if (response.status === 403) {
                        setError(t("candidatureTracking.errors.accessDenied"));
                    } else if (response.status === 404) {
                        setCandidatures([]);
                    } else {
                        setError(t("candidatureTracking.errors.loadingFailed"));
                    }
                    return;
                }

                const data = await response.json();
                setCandidatures(Array.isArray(data) ? data : []);
            } catch (err) {
                console.error("Erreur lors du chargement des candidatures :", err);
                setError(t("candidatureTracking.errors.loadingFailed"));
            } finally {
                setLoading(false);
            }
        };

        fetchCandidatures();
    }, []);

    const filteredCandidatures = useMemo(() => {
        return candidatures.filter((candidature) => {
            if (statusFilter !== "ALL" && candidature.status !== statusFilter) {
                return false;
            }

            if (searchTerm) {
                const searchLower = searchTerm.toLowerCase();
                const studentName = `${candidature.student?.firstName || ""} ${candidature.student?.lastName || ""}`.toLowerCase();
                const matricule = candidature.student?.matricule?.toLowerCase() || "";
                const offerTitle = candidature.jobOffer?.title?.toLowerCase() || "";
                const companyName = candidature.jobOffer?.companyName?.toLowerCase() || "";

                return (
                    studentName.includes(searchLower) ||
                    matricule.includes(searchLower) ||
                    offerTitle.includes(searchLower) ||
                    companyName.includes(searchLower)
                );
            }

            return true;
        });
    }, [candidatures, statusFilter, searchTerm]);

    return (
        <div className={pageWrapper}>
            <div className="flex flex-col items-center gap-8 pt-20 pb-20 px-4">
                <h1 className={pageTitle}>{t("candidatureTracking.title")}</h1>

                <section className="w-full max-w-5xl">
                    <h2 className={sectionTitle}>{t("candidatureTracking.subtitle")}</h2>

                    <div className="mb-6 space-y-4">
                        <div className="flex flex-col gap-2">
                            <label htmlFor="status-filter" className="font-semibold text-main">
                                {t("candidatureTracking.filters.filterByStatus")}
                            </label>
                            <select
                                id="status-filter"
                                value={statusFilter}
                                onChange={(e) => setStatusFilter(e.target.value)}
                                className={inputField}
                            >
                                {STATUS_OPTIONS.map((option) => (
                                    <option key={option.value} value={option.value}>
                                        {option.label}
                                    </option>
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
                    </div>

                    {loading && (
                        <p className={textMuted}>{t("candidatureTracking.loading")}</p>
                    )}

                    {error && !loading && (
                        <div className={`${textError} p-4 rounded bg-red-50 border border-red-200`} role="alert">
                            {error}
                        </div>
                    )}

                    {!loading && !error && filteredCandidatures.length === 0 && (
                        <p className={`${textMuted} text-center py-8`}>
                            {candidatures.length === 0 
                                ? t("candidatureTracking.noCandidatures")
                                : t("candidatureTracking.noResults")}
                        </p>
                    )}

                    {!loading && !error && filteredCandidatures.length > 0 && (
                        <div>
                            <p className={`${textMuted} mb-4`}>
                                {t("candidatureTracking.resultCount", { 
                                    count: filteredCandidatures.length,
                                    total: candidatures.length 
                                })}
                            </p>
                            <CandidatureTrackingList candidatures={filteredCandidatures} />
                        </div>
                    )}
                </section>
            </div>
        </div>
    );
};

export default CandidatureTracking;
