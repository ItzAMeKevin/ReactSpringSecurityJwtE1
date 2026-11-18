import {useEffect} from "react";
import {useTranslation} from "react-i18next";
import formatSalary from "../../../utils/formatSalary.js";
import {btnModalPrimary, btnOutline} from "../../../styles/classes.js";

const OfferDetailModal = ({offer, programName, deciding, hasError, onAccept, onRefuse, onClose}) => {
    const {t} = useTranslation();

    useEffect(() => {
        const handleEscape = (event) => {
            if (event.key === "Escape" && !deciding) onClose();
        };
        document.addEventListener("keydown", handleEscape);
        return () => document.removeEventListener("keydown", handleEscape);
    }, [deciding, onClose]);

    const address = offer.location;

    return (
        <div
            className="fixed inset-0 z-[1000] flex items-center justify-center bg-black/50 p-4"
            role="presentation"
            onMouseDown={(event) => {
                if (event.target === event.currentTarget && !deciding) onClose();
            }}
        >
            <div
                className="flex max-h-[calc(100vh-2rem)] w-full max-w-2xl flex-col overflow-hidden rounded bg-white shadow-2xl"
                role="dialog"
                aria-modal="true"
                aria-labelledby="offer-detail-title"
            >
                <div className="overflow-y-auto p-6">
                    <h2 id="offer-detail-title" className="text-lg font-bold text-main">
                        {offer.title}
                    </h2>
                    <p className="mb-4 text-sm text-main/70">{programName(offer.programe)}</p>

                    <dl className="space-y-3 text-sm">
                        <div>
                            <dt className="font-semibold text-main">{t("personalSpaceEmployer.fields.description")}</dt>
                            <dd className="whitespace-pre-line">{offer.description}</dd>
                        </div>
                        <div>
                            <dt className="font-semibold text-main">{t("personalSpaceEmployer.fields.requirements")}</dt>
                            <dd className="whitespace-pre-line">{offer.prerequisites}</dd>
                        </div>
                        <div>
                            <dt className="font-semibold text-main">{t("personalSpaceEmployer.fields.salary")}</dt>
                            <dd>{formatSalary(offer.salary, t)}</dd>
                        </div>
                        <div>
                            <dt className="font-semibold text-main">{t("personalSpaceEmployer.fields.startingDate")}</dt>
                            <dd>{offer.startingDate}</dd>
                        </div>
                        <div>
                            <dt className="font-semibold text-main">{t("personalSpaceEmployer.fields.durationInWeeks")}</dt>
                            <dd>{offer.durationInWeeks}</dd>
                        </div>
                        <div>
                            <dt className="font-semibold text-main">{t("personalSpaceEmployer.fields.address")}</dt>
                            <dd>
                                {address
                                    ? `${address.numeroCivic} ${address.rue}, ${address.ville}, ${address.pays}`
                                    : "-"}
                            </dd>
                        </div>
                    </dl>

                    {hasError && (
                        <p role="alert" className="mt-4 text-red-600">{t("errors.generic")}</p>
                    )}

                    <div className="mt-6 flex items-center justify-between gap-2">
                        <button type="button" className="text-sm text-main/70 underline hover:no-underline px-2 py-2" disabled={deciding} onClick={onClose}>
                            {t("personalSpaceManager.close")}
                        </button>
                        <div className="flex gap-3">
                            <button type="button" className={btnOutline} disabled={deciding} onClick={onRefuse}>
                                {t("personalSpaceManager.refuse")}
                            </button>
                            <button type="button" className={btnModalPrimary} disabled={deciding} onClick={onAccept}>
                                {t("personalSpaceManager.accept")}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default OfferDetailModal;
