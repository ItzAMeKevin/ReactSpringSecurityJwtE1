import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import fetcher from "../../../utils/fetcher.js";
import { cardItem, statusBadge, statusBadgeColor, textError, textMuted } from "../../../styles/classes.js";

const StudentJobOfferList = () => {
    const { t } = useTranslation();
    const [offers, setOffers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [noAcceptedCv, setNoAcceptedCv] = useState(false);

    const programs = t("inscription.programsList", { returnObjects: true });
    const programName = (enumName) =>
        programs.find((p) => p.enumName === enumName)?.name ?? enumName;

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
        <ul className="grid w-full max-w-4xl grid-cols-1 gap-4 md:grid-cols-2">
            {offers.map((offer, i) => (
                <li key={offer.id ?? i} className={`${cardItem} p-4`}>
                    <div className="flex items-start justify-between gap-2">
                        <h3 className="text-lg font-bold text-black">{offer.title}</h3>
                        <span className={`${statusBadge} ${statusBadgeColor(offer.status)}`}>
                            {t(`personalSpaceEmployer.statuts.${offer.status}`, offer.status)}
                        </span>
                    </div>
                    <p className="text-sm text-gray-600">{programName(offer.programe)}</p>
                    <p className="mt-2 line-clamp-3">{offer.description}</p>
                    <dl className="mt-3 grid grid-cols-2 gap-1 text-sm">
                        <dt className="font-bold text-black">{t("personalSpaceEmployer.fields.salary")}</dt>
                        <dd>{offer.salary}</dd>
                        <dt className="font-bold text-black">{t("personalSpaceEmployer.fields.startingDate")}</dt>
                        <dd>{offer.startingDate}</dd>
                        <dt className="font-bold text-black">{t("personalSpaceEmployer.fields.durationInWeeks")}</dt>
                        <dd>{offer.durationInWeeks}</dd>
                        <dt className="font-bold text-black">{t("personalSpaceEmployer.fields.address")}</dt>
                        <dd>{offer.location.numeroCivic} {offer.location.rue}, {offer.location.ville}, {offer.location.pays}</dd>
                    </dl>
                </li>
            ))}
        </ul>
    );
};

export default StudentJobOfferList;
