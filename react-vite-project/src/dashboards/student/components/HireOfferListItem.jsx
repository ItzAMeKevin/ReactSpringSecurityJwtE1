import { useState } from "react";
import { useTranslation } from "react-i18next";
import { cardItem, btnAccept, btnRefuse, btnSmPrimary, textError, textMuted, successCard, dangerCard } from "@/styles/classes.js";

const DECISIONS = {
    accept: { confirmKey: "etudiantEmbauches.confirmAccept", button: btnAccept, panel: successCard },
    decline: { confirmKey: "etudiantEmbauches.confirmDecline", button: btnRefuse, panel: dangerCard },
};

const HireOfferListItem = ({ hire, onAccept, onDecline }) => {
    const { t } = useTranslation();
    const [decision, setDecision] = useState(null);
    const [isProcessing, setIsProcessing] = useState(false);
    const [hasError, setHasError] = useState(false);
    const { title, companyName, durationInWeeks } = hire.jobOffer;

    const openDecision = (kind) => {
        setHasError(false);
        setDecision(kind);
    };

    const handleConfirm = async () => {
        setIsProcessing(true);
        setHasError(false);
        try {
            await (decision === "accept" ? onAccept : onDecline)(hire.id);
        } catch (error) {
            console.error("Erreur lors de la réponse à l'offre d'embauche :", error);
            setHasError(true);
            setIsProcessing(false);
        }
    };

    const current = decision && DECISIONS[decision];

    return (
        <div className={cardItem}>
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
                <div className={`mt-3 flex flex-col gap-2 rounded-md p-3 ${current.panel}`} role="group">
                    <p className="text-sm font-semibold text-main">{t(current.confirmKey, { title, company: companyName })}</p>
                    <div className="flex gap-2">
                        <button type="button" onClick={handleConfirm} disabled={isProcessing} className={current.button}>
                            {isProcessing ? t("etudiantEmbauches.processing") : t("etudiantEmbauches.confirm")}
                        </button>
                        <button type="button" onClick={() => setDecision(null)} disabled={isProcessing} className={btnSmPrimary}>
                            {t("etudiantEmbauches.cancel")}
                        </button>
                    </div>
                </div>
            )}

            {hasError && <p className={`${textError} mt-2 text-sm`} role="alert">{t("etudiantEmbauches.error")}</p>}
        </div>
    );
};

export default HireOfferListItem;
