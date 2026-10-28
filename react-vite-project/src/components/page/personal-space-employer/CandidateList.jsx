import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import fetcher from "../../../utils/fetcher.js";
import {sectionTitle, textError, textMuted} from "../../../styles/classes.js";
import CandidateListItem from "./CandidateListItem.jsx";

const CandidateList = ({offerId}) => {
    const {t} = useTranslation();
    const [candidates, setCandidates] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            setLoading(true);
            setError(null);
            try {
                const res = await fetcher(`/employer/${offerId}/candidates`, {method: "GET"});
                if (!res.ok) throw new Error(`${res.status}`);
                const data = await res.json();
                if (!cancelled) setCandidates(Array.isArray(data) ? data : []);
            } catch {
                if (!cancelled) setError(t("employeurCandidats.erreurChargement"));
            } finally {
                if (!cancelled) setLoading(false);
            }
        };

        load();
        return () => { cancelled = true; };
    }, [offerId, t]);

    const handleStatusChange = (cvId, newStatus) => {
        setCandidates((prev) =>
            prev.map((c) => (c.cvId === cvId ? {...c, status: newStatus} : c))
        );
    };

    if (loading) return <p className={textMuted}>{t("employeurCandidats.loading")}</p>;
    if (error) return <p className={textError}>{error}</p>;
    if (candidates.length === 0) {
        return <p className={textMuted}>{t("employeurCandidats.aucunCandidats")}</p>;
    }

    return (
        <section>
            <h2 className={sectionTitle}>{t("employeurCandidats.titreCandidats")}</h2>
            <ul className="flex flex-col gap-3">
                {candidates.map((candidate) => (
                    <CandidateListItem
                        key={candidate.cvId}
                        candidate={candidate}
                        offerId={offerId}
                        onStatusChange={handleStatusChange}
                    />
                ))}
            </ul>
        </section>
    );
};

export default CandidateList;
