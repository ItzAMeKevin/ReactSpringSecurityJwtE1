import { useState } from "react";
import { useTranslation } from "react-i18next";

const ManagerJobOfferListItem = ({ offer, isHighlighted, onAccept, onRefuse }) => {
    const { t } = useTranslation();
    const [actionError, setActionError] = useState(null);
    const [processing, setProcessing] = useState(null);

    const handleAccept = async () => {
        setActionError(null);
        setProcessing("accept");
        try {
            await onAccept(offer.id);
        } catch {
            setActionError(t("gestionnaire.decline.errorProcessing"));
        } finally {
            setProcessing(null);
        }
    };

    const handleRefuse = async () => {
        setActionError(null);
        setProcessing("refuse");
        try {
            await onRefuse(offer.id);
        } catch {
            setActionError(t("gestionnaire.decline.errorProcessing"));
        } finally {
            setProcessing(null);
        }
    };

    return (
        <div className={`bg-white rounded-xl shadow-sm p-5 border transition-all duration-500 ${
            isHighlighted ? "border-[#4b1113] ring-2 ring-[#4b1113]/30" : "border-[#4b1113]/10"
        }`}>
            <div className="flex items-start justify-between gap-4">
                <div className="flex-1 min-w-0">
                    <h3 className="font-semibold text-[#4b1113] text-lg">{offer.title}</h3>
                    <p className="text-sm text-[#4b1113]/70 mt-1">
                        {t("gestionnaire.offres.company")}: <span
                        className="font-medium">{offer.companyName}</span>
                    </p>
                    <p className="text-sm text-[#4b1113]/70 mt-1">
                        {offer.startingDate} · {offer.durationInWeeks} {t("gestionnaire.offres.weeks")}
                    </p>
                    {offer.description && (
                        <p className="text-sm text-[#4b1113]/70 mt-2 line-clamp-2">{offer.description}</p>
                    )}
                </div>
                <div className="flex flex-col gap-2 shrink-0">
                    <button
                        type="button"
                        onClick={handleAccept}
                        disabled={processing !== null}
                        className="bg-green-700 text-white px-4 py-2 rounded hover:bg-green-800 transition-colors
  text-sm font-medium disabled:opacity-50"
                    >
                        {t("gestionnaire.accepter")}
                    </button>
                    <button
                        type="button"
                        onClick={handleRefuse}
                        disabled={processing !== null}
                        className="bg-[#4b1113] text-white px-4 py-2 rounded hover:bg-[#3a0d0f] transition-colors
  text-sm font-medium disabled:opacity-50"
                    >
                        {t("gestionnaire.refuser")}
                    </button>
                </div>
            </div>
            {actionError && (
                <p className="text-red-700 text-sm mt-2" role="alert">{actionError}</p>
            )}
        </div>
    );
};

export default ManagerJobOfferListItem;