import {useState} from "react";
import { useTranslation } from "react-i18next";
import scrollIntoViewOnMount from "../../../utils/scrollIntoViewOnMount.js";
import formatSalary from "../../../utils/formatSalary.js";
import { cardItem, cardHighlighted, btnAccept, btnRefuse, btnSmPrimary, textError } from "../../../styles/classes.js";

const ManagerJobOfferListItem = ({ offer, isHighlighted, onClearHighlight, onAccept, onRefuse }) => {
    const {t} = useTranslation();
    const [actionError, setActionError] = useState(null);
    const [processing, setProcessing] = useState(null);
    const [pendingAction, setPendingAction] = useState(null);
    const [expanded, setExpanded] = useState(false);
    const [feedback, setFeedback] = useState("");
    const [feedbackError, setFeedbackError] = useState(null);

    const programs = t("inscription.programsList", {returnObjects: true});
    const programName = (enumName) =>
        Array.isArray(programs)
            ? programs.find((p) => p.enumName === enumName)?.name ?? enumName
            : enumName;

    const loc = offer.location;
    const address = loc
        ? [
            [loc.numeroCivic, loc.rue].filter(Boolean).join(" "),
            loc.ville,
            loc.codePostal,
            loc.pays,
        ].filter(Boolean).join(", ")
        : null;


    const isAccept = pendingAction === "accept";

    const openConfirm = (action) => {
        setActionError(null);
        setPendingAction(action);
    };

    const cancelConfirm = () => {
        setPendingAction(null);
        setFeedback("");
        setFeedbackError(null);
    };

    const handleConfirm = async () => {
        setActionError(null);
        if (!isAccept && !feedback.trim()) {
            setFeedbackError(t("gestionnaire.offres.feedbackRequired"));
            return;
        }
        setProcessing(pendingAction);
        try {
            if (isAccept) await onAccept(offer.id);
            else await onRefuse(offer.id, { feedback: feedback.trim() });
            setPendingAction(null);
        } catch {
            setActionError(t("gestionnaire.offres.errorProcessing"));
        } finally {
            setProcessing(null);
        }
    };
    return (
        <div
            ref={isHighlighted ? scrollIntoViewOnMount : undefined}
            onClick={isHighlighted ? onClearHighlight : undefined}
            className={`${cardItem} ${isHighlighted ? cardHighlighted : ''}`}
        >
            <div className="flex items-start justify-between gap-4">
                <div className="flex-1 min-w-0">
                    <h3 className="font-semibold text-main text-lg">{offer.title}</h3>
                    <p className="text-sm text-main/70 mt-1">
                        {t("gestionnaire.offres.company")}: <span className="font-medium">{offer.companyName}</span>
                    </p>
                    <p className="text-sm text-main/70 mt-1">
                        {offer.startingDate} · {offer.durationInWeeks} {t("gestionnaire.offres.weeks")}
                    </p>

                    {offer.description && (
                        <p className={`text-sm text-main/70 mt-2 whitespace-pre-line ${expanded ? '' : 'line-clamp-2'}`}>
                            {offer.description}
                        </p>
                    )}

                    {expanded && (
                        <dl className="mt-4 grid grid-cols-1 gap-x-4 gap-y-3 text-sm sm:grid-cols-[max-content_1fr] border-t border-main/10 pt-4">
                            {offer.prerequisites && (
                                <>
                                    <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.requirements")}</dt>
                                    <dd className="text-main/70 whitespace-pre-line">{offer.prerequisites}</dd>
                                </>
                            )}
                            {offer.programe && (
                                <>
                                    <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.programe")}</dt>
                                    <dd className="text-main/70">{programName(offer.programe)}</dd>
                                </>
                            )}
                            {offer.salary != null && (
                                <>
                                    <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.salary")}</dt>
                                    <dd className="text-main/70">{formatSalary(offer.salary, t)}</dd>
                                </>
                            )}
                            <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.startingDate")}</dt>
                            <dd className="text-main/70">{offer.startingDate}</dd>
                            <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.durationInWeeks")}</dt>
                            <dd className="text-main/70">{offer.durationInWeeks} {t("gestionnaire.offres.weeks")}</dd>
                            {address && (
                                <>
                                    <dt className="font-bold text-main">{t("personalSpaceEmployer.fields.address")}</dt>
                                    <dd className="text-main/70">{address}</dd>
                                </>
                            )}
                        </dl>
                    )}

                    <button
                        type="button"
                        onClick={() => setExpanded((prev) => !prev)}
                        aria-expanded={expanded}
                        className="text-sm font-medium text-main underline underline-offset-2 hover:text-main/80 mt-2 cursor-pointer"
                    >
                        {expanded ? t("gestionnaire.offres.showLess") : t("gestionnaire.offres.readMore")}
                    </button>
                </div>

                <div className="flex flex-col gap-2 shrink-0">
                    <button
                        type="button"
                        onClick={() => openConfirm("accept")}
                        disabled={!!(processing || pendingAction)}
                        className={btnAccept}
                    >
                        {t("gestionnaire.accepter")}
                    </button>
                    <button
                        type="button"
                        onClick={() => openConfirm("refuse")}
                        disabled={!!(processing || pendingAction)}
                        className={btnRefuse}
                    >
                        {t("gestionnaire.refuser")}
                    </button>
                </div>
            </div>

            {pendingAction && (
                <div className="mt-3 flex flex-col gap-2 border-t pt-3" role="group"
                     aria-label={t("gestionnaire.offres.confirmation.confirmer")}>
                    <p className="text-sm font-semibold text-black">
                        {t(`gestionnaire.offres.confirmation.${isAccept ? "accepter" : "refuser"}`,
                            {title: offer.title})}
                    </p>
                    {!isAccept && (
                        <>
                            <p className="text-sm text-main">{t("gestionnaire.offres.feedbackTitle")}</p>
                            <textarea
                                value={feedback}
                                onChange={(e) => { setFeedback(e.target.value); setFeedbackError(null); }}
                                placeholder={t("gestionnaire.offres.feedbackPlaceholder")}
                                rows={3}
                                className="w-full border border-red-300 rounded-md py-2 px-3 text-sm text-main bg-white resize-y focus:outline-none focus:ring-2 focus:ring-red-400"
                            />
                            {feedbackError && <p className={`${textError} text-sm`} role="alert">{feedbackError}</p>}
                        </>
                    )}
                    <div className="flex gap-2">
                        <button
                            type="button"
                            className={isAccept ? btnAccept : btnRefuse}
                            disabled={!!processing}
                            onClick={handleConfirm}
                        >
                            {t("gestionnaire.offres.confirmation.confirmer")}
                        </button>
                        <button
                            type="button"
                            className={btnSmPrimary}
                            disabled={!!processing}
                            onClick={cancelConfirm}
                        >
                            {t("gestionnaire.offres.confirmation.annuler")}
                        </button>
                    </div>
                </div>
            )}

            {actionError && (
                <p className={`${textError} text-sm mt-2`} role="alert">{actionError}</p>
            )}
        </div>
    );
};
export default ManagerJobOfferListItem;