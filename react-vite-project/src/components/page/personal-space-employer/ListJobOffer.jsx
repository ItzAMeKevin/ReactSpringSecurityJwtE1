import {useEffect, useMemo, useState} from "react";
import {useTranslation} from "react-i18next";
import fetcher from "../../../utils/fetcher.js";
import useHighlightWhenNotified from "../../../hooks/useHighlightWhenNotified.js";
import scrollIntoViewOnMount from "../../../utils/scrollIntoViewOnMount.js";
import formatSalary from "../../../utils/formatSalary.js";
import {btnPrimary, btnSmPrimary, cardItem, cardHighlighted, inputField, statusBadge, statusBadgeColor, textError, textMuted} from "../../../styles/classes.js";
import CandidateList from "./CandidateList.jsx";
import { getApiErrorKey } from "../../../utils/apiError.js";
import {compareSessionCodesDesc, getSessionCode, getCurrentSessionCode, getSessionsFromOffers} from "../../../utils/session.js";

const formatSessionLabel = (code, t) => {
    const prefix = code[0];
    const year = code.slice(1);
    return t(`personalSpaceEmployer.sessions.${prefix}`, { year });
};

const ListJobOffer = ({refreshKey, onEdit, onCreateOffer}) => {
    const {t} = useTranslation();
    const [offers, setOffers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedSession, setSelectedSession] = useState(getCurrentSessionCode);
    const { isHighlighted, clearHighlight } = useHighlightWhenNotified();
    const [selectedOfferId, setSelectedOfferId] = useState(null);

    const handleToggleCandidates = (offerId) => {
        setSelectedOfferId((prev) => (prev === offerId ? null : offerId));
    };

    const programs = t("inscription.programsList", {returnObjects: true});
    const programName = (enumName) =>
        Array.isArray(programs)
            ? programs.find((p) => p.enumName === enumName)?.name ?? enumName
            : enumName;

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            setLoading(true);
            setError(null);
            try {
                const res = await fetcher("/employer", {method: "GET"});
                if (!res.ok) throw new Error(await getApiErrorKey(res));
                const data = await res.json();
                if (!cancelled) setOffers(Array.isArray(data) ? data : []);
            } catch (error) {
                if (!cancelled) {
                    setError(error.message?.startsWith("errors.") ? t(error.message) : t("errors.generic"));
                }
            } finally {
                if (!cancelled) setLoading(false);
            }
        };

        load();
        return () => {
            cancelled = true;
        };
    }, [refreshKey]);

    const allSessions = useMemo(() => {
        const current = getCurrentSessionCode();
        const derived = getSessionsFromOffers(offers);
        if (!derived.includes(current)) derived.push(current);
        return derived.sort(compareSessionCodesDesc);
    }, [offers]);

    const filteredOffers = useMemo(
        () => offers.filter(o => getSessionCode(o.startingDate) === selectedSession),
        [offers, selectedSession]
    );

    if (loading) return <p className={textMuted}>{t("personalSpaceEmployer.loading")}</p>;
    if (error) return <p className={textError}>{error}</p>;
    if (offers.length === 0) {
        return (
            <div className="flex flex-col items-center gap-3">
                <p className={textMuted}>{t("personalSpaceEmployer.noJobOffers")}</p>
                <button className={btnPrimary} onClick={onCreateOffer}>
                    {t("personalSpaceEmployer.createJobOffer")}
                </button>
            </div>
        );
    }

    return (
        <div className="w-full max-w-4xl flex flex-col gap-4">
            <div className="flex items-center gap-3 w-full max-w-4xl">
                <label htmlFor="session-select" className="text-sm font-medium text-main shrink-0">
                    {t("personalSpaceEmployer.sessionFilter.label")}
                </label>
                <select
                    id="session-select"
                    value={selectedSession}
                    onChange={e => setSelectedSession(e.target.value)}
                    className={`${inputField} max-w-xs`}
                >
                    {allSessions.map(code => (
                        <option key={code} value={code}>{formatSessionLabel(code, t)}</option>
                    ))}
                </select>
            </div>

            {filteredOffers.length === 0 ? (
                <div className="flex flex-col items-center gap-3">
                    <p className={textMuted}>{t("personalSpaceEmployer.noOffersForSession")}</p>
                    <button className={btnPrimary} onClick={onCreateOffer}>
                        {t("personalSpaceEmployer.createJobOffer")}
                    </button>
                </div>
            ) : (
                <ul className="grid w-full grid-cols-1 gap-4 md:grid-cols-2">
                    {filteredOffers.map((offer, i) => (
                        <li
                            key={offer.id ?? i}
                            ref={isHighlighted("offer", offer.id) ? scrollIntoViewOnMount : undefined}
                            onClick={isHighlighted("offer", offer.id) ? clearHighlight : undefined}
                            className={`${cardItem} p-4 ${isHighlighted("offer", offer.id) ? cardHighlighted : ''}`}
                        >
                            <div className="flex items-start justify-between gap-2">
                                <h3 className="text-lg font-bold text-main">{offer.title}</h3>
                                {offer.status && (
                                    <span className={`${statusBadge} ${statusBadgeColor(offer.status)}`}>
                                        {t(`personalSpaceEmployer.statuts.${offer.status}`, offer.status)}
                                    </span>
                                )}
                            </div>
                            <p className="text-sm text-main/70">{programName(offer.programe)}</p>
                            <p className="mt-2 line-clamp-3">{offer.description}</p>
                            <dl className="mt-3 grid grid-cols-2 gap-1 text-sm">
                                <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.salary")}</dt>
                                <dd>{formatSalary(offer.salary, t)}</dd>
                                <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.startingDate")}</dt>
                                <dd>{offer.startingDate}</dd>
                                <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.durationInWeeks")}</dt>
                                <dd>{offer.durationInWeeks}</dd>
                                <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.address")}</dt>
                                <dd>{offer.location?.numeroCivic} {offer.location?.rue}, {offer.location?.ville}, {offer.location?.pays}</dd>
                            </dl>
                            {offer.status === "DECLINED" && (
                                <div className="mt-3 rounded-md border border-red-200 bg-red-50 px-3 py-2">
                                    <p className="text-sm font-semibold text-red-800">{t("personalSpaceEmployer.feedbackTitre")}</p>
                                    <p className="text-sm text-main mt-1 whitespace-pre-line break-words">
                                        {offer.refusalFeedback ?? t("personalSpaceEmployer.noFeedback")}
                                    </p>
                                </div>
                            )}
                            <div className="flex flex-wrap gap-2 mt-3">
                                <button
                                    type="button"
                                    className={`${btnSmPrimary} disabled:opacity-50 disabled:cursor-not-allowed`}
                                    onClick={(e) => { e.stopPropagation(); handleToggleCandidates(offer.id); }}
                                    disabled={offer.status !== "ACCEPTED"}
                                >
                                    {selectedOfferId === offer.id
                                        ? t("employeurCandidats.masquerCandidats")
                                        : t("employeurCandidats.voirCandidats")}
                                </button>
                                {offer.status === "DECLINED" && (
                                    <button
                                        type="button"
                                        className={btnSmPrimary}
                                        onClick={(e) => { e.stopPropagation(); onEdit?.(offer); }}
                                    >
                                        {t("personalSpaceEmployer.editJobOffer")}
                                    </button>
                                )}
                            </div>
                            {selectedOfferId === offer.id && (
                                <CandidateList offerId={offer.id} />
                            )}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default ListJobOffer;
