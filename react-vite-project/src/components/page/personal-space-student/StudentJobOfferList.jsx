import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import fetcher from "../../../utils/fetcher.js";
import { textError, textMuted } from "../../../styles/classes.js";
import StudentJobOfferListItem from "./StudentJobOfferListItem.jsx";

const VALIDATED_CV_STATUS = "ACCEPTED";

const StudentJobOfferList = () => {
    const { t } = useTranslation();
    const [offers, setOffers] = useState([]);
    const [acceptedCv, setAcceptedCv] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [noAcceptedCv, setNoAcceptedCv] = useState(false);

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            setLoading(true);
            setError(null);
            setNoAcceptedCv(false);
            try {
                const [offersRes, cvRes] = await Promise.all([
                    fetcher("/etudiant/offres"),
                    fetcher("/student-cv"),
                ]);

                if (offersRes.status === 403) {
                    if (!cancelled) setNoAcceptedCv(true);
                    return;
                }
                if (!offersRes.ok) throw new Error(`${offersRes.status}`);
                const data = await offersRes.json();
                if (!Array.isArray(data)) throw new Error("invalid response");

                let validatedCv = null;
                if (cvRes.ok) {
                    const cvs = await cvRes.json();
                    validatedCv = Array.isArray(cvs)
                        ? cvs.find((cv) => cv.status === VALIDATED_CV_STATUS) ?? null
                        : null;
                }

                if (!cancelled) {
                    setOffers(data);
                    setAcceptedCv(validatedCv);
                }
            } catch (e) {
                console.error("Erreur chargement offres :", e);
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
                <StudentJobOfferListItem
                    key={offer.id ?? `fallback-${i}`}
                    offer={offer}
                    acceptedCv={acceptedCv}
                />
            ))}
        </ul>
    );
};

export default StudentJobOfferList;