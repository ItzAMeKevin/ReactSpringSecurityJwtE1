import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import fetcher from "../../../utils/fetcher.js";
import { textError, textMuted } from "../../../styles/classes.js";
import StudentJobOfferListItem from "./StudentJobOfferListItem.jsx";

const StudentJobOfferList = () => {
    const { t } = useTranslation();
    const [offers, setOffers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [noAcceptedCv, setNoAcceptedCv] = useState(false)

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            setLoading(true);
            setError(null);
            setNoAcceptedCv(false);
            try {
                const res = await fetcher("/etudiant/offres");
                if (res.status === 403) {
                    if (!cancelled) setNoAcceptedCv(true);
                    return;
                }
                if (!res.ok) throw new Error(`${res.status}`);
                const data = await res.json();
                if (!Array.isArray(data)) throw new Error("invalid response");
                if (!cancelled) setOffers(data);
            } catch {
                if (!cancelled) setError(t("errors.generic"));
            } finally {
                if (!cancelled) setLoading(false);
            }
        };

        load();
        return () => {
            cancelled = true;
        };
    }, [t]);

    if (loading) return <p className={`${textMuted} text-sm`}>{t("etudiantOffres.loading")}</p>;
    if (noAcceptedCv) return <p className={`${textMuted} text-sm`}>{t("etudiantOffres.noAcceptedCv")}</p>;
    if (error) return <p className={textError}>{error}</p>;
    if (offers.length === 0) return <p className={`${textMuted} text-sm`}>{t("etudiantOffres.noOffers")}</p>;

    return (
        <ul className="grid w-full max-w-4xl grid-cols-1 items-start gap-4 md:grid-cols-2">
            {offers.map((offer, i) => (
                <StudentJobOfferListItem key={offer.id ?? `fallback-${i}`} offer={offer} />
            ))}
        </ul>
    );
};


export default StudentJobOfferList;
