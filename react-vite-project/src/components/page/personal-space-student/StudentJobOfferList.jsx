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

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            setLoading(true);
            setError(null);
            try {
                const [offersRes, cvRes] = await Promise.all([
                    fetcher("/etudiant/offres"),
                    fetcher("/student-cv"),
                ]);

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
    if (error) return <p className={textError}>{error}</p>;
    if (offers.length === 0) return <p className={`${textMuted} text-sm`}>{t("etudiantOffres.noOffers")}</p>;

    return (
        <div className="flex w-full flex-col gap-4">
            {!acceptedCv && (
                <p className="rounded-md border border-amber-200 bg-amber-50 px-3 py-2 text-sm text-amber-900" role="status">
                    {t("etudiantOffres.noValidatedCv")}
                </p>
            )}
            <ul className="grid w-full grid-cols-1 items-start gap-4 md:grid-cols-2">
                {offers.map((offer, i) => (
                    <StudentJobOfferListItem
                        key={offer.id ?? `fallback-${i}`}
                        offer={offer}
                        acceptedCv={acceptedCv}
                    />
                ))}
            </ul>
        </div>
    );
};

export default StudentJobOfferList;