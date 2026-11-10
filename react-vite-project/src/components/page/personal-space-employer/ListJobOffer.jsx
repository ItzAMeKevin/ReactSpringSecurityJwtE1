import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import fetcher from "../../../utils/fetcher.js";
import useHighlightWhenNotified from "../../../hooks/useHighlightWhenNotified.js";
import scrollIntoViewOnMount from "../../../utils/scrollIntoViewOnMount.js";
import formatSalary from "../../../utils/formatSalary.js";
import {btnSmPrimary, cardItem, cardHighlighted, statusBadge, statusBadgeColor, textError, textMuted} from "../../../styles/classes.js";
import CandidateList from "./CandidateList.jsx";

const ListJobOffer = ({refreshKey}) => {
    const {t} = useTranslation();
    const [offers, setOffers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { isHighlighted, clearHighlight } = useHighlightWhenNotified();
    const [selectedOfferId, setSelectedOfferId] = useState(null);

    const handleToggleCandidates = (offerId) => {
        setSelectedOfferId((prev) => (prev === offerId ? null : offerId));
    };

    const programs = t("inscription.programsList", {returnObjects: true});
    const programName = (enumName) =>
        programs.find((p) => p.enumName === enumName)?.name ?? enumName;

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            setLoading(true);
            setError(null);
            try {
                const res = await fetcher("/employer", {method: "GET"});
                if (!res.ok) throw new Error(`${res.status}`);
                const data = await res.json();
                if (!cancelled) setOffers(Array.isArray(data) ? data : []);
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
    }, [refreshKey]);

    if (loading) return <p className={textMuted}>{t("personalSpaceEmployer.loading")}</p>;
    if (error) return <p className={textError}>{error}</p>;
    if (offers.length === 0) {
        return <p className={textMuted}>{t("personalSpaceEmployer.noJobOffers")}</p>;
    }

    return (
        <div className="w-full max-w-4xl flex flex-col gap-4">
            <ul className="grid w-full grid-cols-1 gap-4 md:grid-cols-2">
                {offers.map((offer, i) => (
                    <li
                        key={offer.id ?? i}
                        ref={isHighlighted("offer", offer.id) ? scrollIntoViewOnMount : undefined}
                        onClick={isHighlighted("offer", offer.id) ? clearHighlight : undefined}
                        className={`${cardItem} p-4 ${isHighlighted("offer", offer.id) ? cardHighlighted : ''}`}
                    >
                        <div className="flex items-start justify-between gap-2">
                            <h3 className="text-lg font-bold text-black">{offer.title}</h3>
                            {offer.status && (
                                <span className={`${statusBadge} ${statusBadgeColor(offer.status)}`}>
                                    {t(`personalSpaceEmployer.statuts.${offer.status}`, offer.status)}
                                </span>
                            )}
                        </div>
                        <p className="text-sm text-gray-600">{programName(offer.programe)}</p>
                        <p className="mt-2 line-clamp-3">{offer.description}</p>
                        <dl className="mt-3 grid grid-cols-2 gap-1 text-sm">
                            <dt className="font-bold text-black">{t("personalSpaceEmployer.fields.salary")}</dt>
                            <dd>{formatSalary(offer.salary, t)}</dd>
                            <dt className="font-bold text-black">{t("personalSpaceEmployer.fields.startingDate")}</dt>
                            <dd>{offer.startingDate}</dd>
                            <dt className="font-bold text-black">{t("personalSpaceEmployer.fields.durationInWeeks")}</dt>
                            <dd>{offer.durationInWeeks}</dd>
                            <dt className="font-bold text-black">{t("personalSpaceEmployer.fields.address")}</dt>
                            <dd>{offer.location.numeroCivic} {offer.location.rue}, {offer.location.ville}, {offer.location.pays}</dd>
                        </dl>
                        <button
                            type="button"
                            className={`${btnSmPrimary} mt-3 disabled:opacity-50 disabled:cursor-not-allowed`}
                            onClick={() => handleToggleCandidates(offer.id)}
                            disabled={offer.status !== "ACCEPTED"}
                        >
                            {selectedOfferId === offer.id
                                ? t("employeurCandidats.masquerCandidats")
                                : t("employeurCandidats.voirCandidats")}
                        </button>
                        {selectedOfferId === offer.id && (
                            <CandidateList offerId={offer.id} />
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default ListJobOffer;