import { useTranslation } from "react-i18next";
import scrollIntoViewOnMount from "../../../utils/scrollIntoViewOnMount.js";
import formatSalary from "../../../utils/formatSalary.js";
import CandidateList from "./CandidateList.jsx";
import { btnSmPrimary, cardItem, cardHighlighted, statusBadge, statusBadgeColor } from "../../../styles/classes.js";

const CANDIDATE_STATUSES = ["ACCEPTED", "FILLED"];

const JobOfferCard = ({ offer, isHighlighted, onClearHighlight, showCandidates, onToggleCandidates, onEdit, onHire, onInterviewScheduled }) => {
    const { t } = useTranslation();
    const programs = t("inscription.programsList", { returnObjects: true });
    const programName = Array.isArray(programs)
        ? programs.find((p) => p.enumName === offer.programe)?.name ?? offer.programe
        : offer.programe;
    const isDeclined = offer.status === "DECLINED";
    const canViewCandidates = CANDIDATE_STATUSES.includes(offer.status);
    const remainingPositions = offer.numberOfPositions != null ? offer.numberOfPositions - (offer.hiredCount ?? 0) : null;

    return (
        <li
            ref={isHighlighted ? scrollIntoViewOnMount : undefined}
            onClick={isHighlighted ? onClearHighlight : undefined}
            className={`${cardItem} p-4 ${isHighlighted ? cardHighlighted : ""}`}
        >
            <div className="flex items-start justify-between gap-2">
                <h3 className="text-lg font-bold text-main">{offer.title}</h3>
                {offer.status && (
                    <span className={`${statusBadge} ${statusBadgeColor(offer.status)}`}>
                        {t(`personalSpaceEmployer.statuts.${offer.status}`, offer.status)}
                    </span>
                )}
            </div>
            <p className="text-sm text-main/70">{programName}</p>
            <p className="mt-2 line-clamp-3">{offer.description}</p>
            <dl className="mt-3 grid grid-cols-2 gap-1 text-sm">
                <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.salary")}</dt>
                <dd>{formatSalary(offer.salary, t)}</dd>
                <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.startingDate")}</dt>
                <dd>{offer.startingDate}</dd>
                <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.durationInWeeks")}</dt>
                <dd>{offer.durationInWeeks}</dd>
                {offer.numberOfPositions != null && (
                    <>
                        <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.positions")}</dt>
                        <dd>{offer.hiredCount ?? 0} / {offer.numberOfPositions} {t("personalSpaceEmployer.fields.positionsFilled")}</dd>
                    </>
                )}
                <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.address")}</dt>
                <dd>{offer.location?.numeroCivic} {offer.location?.rue}, {offer.location?.ville}, {offer.location?.pays}</dd>
            </dl>
            {isDeclined && (
                <div className="mt-3 rounded-md border border-red-200 bg-red-50 px-3 py-2">
                    <p className="text-sm font-semibold text-red-800">{t("personalSpaceEmployer.feedbackTitre")}</p>
                    <p className="mt-1 whitespace-pre-line break-words text-sm text-main">
                        {offer.refusalFeedback ?? t("personalSpaceEmployer.noFeedback")}
                    </p>
                </div>
            )}
            {(canViewCandidates || isDeclined) && (
                <div className="mt-3 flex flex-wrap gap-2">
                    {canViewCandidates && (
                        <button type="button" className={btnSmPrimary} onClick={(e) => { e.stopPropagation(); onToggleCandidates(); }}>
                            {t(showCandidates ? "employeurCandidats.masquerCandidats" : "employeurCandidats.voirCandidats")}
                        </button>
                    )}
                    {isDeclined && (
                        <button type="button" className={btnSmPrimary} onClick={(e) => { e.stopPropagation(); onEdit(); }}>
                            {t("personalSpaceEmployer.editJobOffer")}
                        </button>
                    )}
                </div>
            )}
            {showCandidates && (
                <CandidateList
                    offerId={offer.id}
                    onHire={onHire}
                    onInterviewScheduled={onInterviewScheduled}
                    remainingPositions={remainingPositions}
                />
            )}
        </li>
    );
};

export default JobOfferCard;
