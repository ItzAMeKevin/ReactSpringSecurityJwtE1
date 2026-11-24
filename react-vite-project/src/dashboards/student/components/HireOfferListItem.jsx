import { useState } from "react";
import { useTranslation } from "react-i18next";
import ConfirmPanel from "@/components/ui/ConfirmPanel.jsx";
import { translateErrorKey } from "@/utils/apiError.js";
import scrollIntoViewOnMount from "@/utils/scrollIntoViewOnMount.js";
import { cardItem, cardHighlighted, btnAccept, btnRefuse, textError, textMuted, successCard, dangerCard } from "@/styles/classes.js";

const DECISIONS = {
    accept: { confirmKey: "etudiantEmbauches.confirmAccept", button: btnAccept, panel: successCard },
    decline: { confirmKey: "etudiantEmbauches.confirmDecline", button: btnRefuse, panel: dangerCard },
};

const HireOfferListItem = ({ hire, isHighlighted, onClearHighlight, onAccept, onDecline }) => {
    const { t } = useTranslation();
    const [decision, setDecision] = useState(null);
    const [isProcessing, setIsProcessing] = useState(false);
    const [errorKey, setErrorKey] = useState(null);
    const { title, companyName, durationInWeeks } = hire.jobOffer;

    const openDecision = (kind) => {
        setErrorKey(null);
        setDecision(kind);
    };

    const handleConfirm = async () => {
        setIsProcessing(true);
        setErrorKey(null);
        try {
            await (decision === "accept" ? onAccept : onDecline)(hire.id);
        } catch (error) {
            setErrorKey(error.message);
            setIsProcessing(false);
        }
    };

    const current = decision && DECISIONS[decision];

    return (
        <div
            ref={isHighlighted ? scrollIntoViewOnMount : undefined}
            onClick={isHighlighted ? onClearHighlight : undefined}
            className={`${cardItem} ${isHighlighted ? cardHighlighted : ""}`}
        >
            <div className="flex flex-wrap items-center justify-between gap-3">
                <div className="min-w-0">
                    <p className="truncate font-medium text-main">{title}</p>
                    <p className={`truncate text-sm ${textMuted}`}>
                        {companyName} · {t("etudiantEmbauches.weeks", { count: durationInWeeks })}
                    </p>
                </div>
                <div className="flex shrink-0 flex-wrap items-center gap-2">
                    <button type="button" onClick={() => openDecision("accept")} disabled={decision !== null} className={btnAccept}>
                        {t("etudiantEmbauches.accept")}
                    </button>
                    <button type="button" onClick={() => openDecision("decline")} disabled={decision !== null} className={btnRefuse}>
                        {t("etudiantEmbauches.decline")}
                    </button>
                </div>
            </div>

            {current && (
                <ConfirmPanel
                    message={t(current.confirmKey, { title, company: companyName })}
                    panelClass={current.panel}
                    confirmClass={current.button}
                    isProcessing={isProcessing}
                    onConfirm={handleConfirm}
                    onCancel={() => setDecision(null)}
                />
            )}

            {errorKey && (
                <p className={`${textError} mt-2 text-sm`} role="alert">
                    {translateErrorKey(errorKey, t, "etudiantEmbauches.error")}
                </p>
            )}
        </div>
    );
};

export default HireOfferListItem;
