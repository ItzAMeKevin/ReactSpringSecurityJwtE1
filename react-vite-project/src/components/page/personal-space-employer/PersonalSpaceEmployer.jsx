import ListJobOffer from "./ListJobOffer.jsx";
import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import FormJobOffer from "./FormJobOffer.jsx";
import SuccessPopup from "../SuccessPopup.jsx";
import { pageWrapper, pageInner, pageTitle, sectionTitle, btnPrimary, btnOutline, btnModalPrimary, textMuted } from "../../../styles/classes.js";

const PersonalSpaceEmployer = () => {
    
    const {t} = useTranslation();
    const [refreshKey, setRefreshKey] = useState(0);
    const [openForm, setOpenForm] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);
    const [showCloseConfirm, setShowCloseConfirm] = useState(false);

    const handleOpenForm = () => {
        setOpenForm(true);
        setShowCloseConfirm(false);
    }
    const handleCloseForm = () => {
        setOpenForm(false);
        setShowCloseConfirm(false);
    };
    const handleRequestClose = () => setShowCloseConfirm(true);
    const handleFormSuccess = () => {
        handleCloseForm();
        setRefreshKey((key) => key + 1);
        setShowSuccess(true);
    };

    useEffect(() => {
        if (!showSuccess) return undefined;

        const timeoutId = setTimeout(() => setShowSuccess(false), 3000);
        return () => clearTimeout(timeoutId);
    }, [showSuccess]);

    useEffect(() => {
        if (!openForm) {
            return undefined;
        }

        const handleEscape = (event) => {
            if (event.key === "Escape") {
                handleRequestClose();
            }
        };

        document.addEventListener("keydown", handleEscape);
        return () => document.removeEventListener("keydown", handleEscape);
    }, [openForm]);
    
    return (
        <>
            {showSuccess && (
                <SuccessPopup
                    message={t("personalSpaceEmployer.offerCreated")}
                    onClose={() => setShowSuccess(false)}
                />
            )}
            <div className={pageWrapper}>
                <div className={pageInner}>
                    <h1 className={pageTitle}>{t("personalSpaceEmployer.title")}</h1>
                    <button className={btnPrimary} onClick={handleOpenForm}>
                        {t("personalSpaceEmployer.createJobOffer")}
                    </button>
                    {openForm && (
                        <>
                            <div
                                className="fixed inset-0 z-[1000] flex items-center justify-center bg-black/50 p-4"
                                role="presentation"
                                onMouseDown={(event) => {
                                    if (event.target === event.currentTarget) {
                                        handleRequestClose();
                                    }
                                }}
                            >
                                <div
                                    className="flex max-h-[calc(100vh-2rem)] w-full max-w-2xl flex-col overflow-hidden rounded bg-white shadow-2xl"
                                    role="dialog"
                                    aria-modal="true"
                                    aria-labelledby="create-job-offer-title"
                                >
                                    <div className="overflow-y-auto p-6">
                                        <h2 id="create-job-offer-title" className="mb-2 text-lg font-bold text-black">
                                            {t("personalSpaceEmployer.createJobOffer")}
                                        </h2>
                                        <FormJobOffer onSuccess={handleFormSuccess} onCancel={handleRequestClose}/>
                                    </div>
                                </div>
                            </div>
                            {showCloseConfirm && (
                                <div className="fixed inset-0 z-[1001] flex items-center justify-center bg-black/40">
                                    <div className="bg-white rounded-lg shadow-xl p-6 max-w-sm w-full mx-4">
                                        <h3 className="text-lg font-bold text-black mb-2">{t("personalSpaceEmployer.confirmClose.title")}</h3>
                                        <p className={`text-sm ${textMuted} mb-6`}>{t("personalSpaceEmployer.confirmClose.message")}</p>
                                        <div className="flex justify-end gap-2">
                                            <button
                                                type="button"
                                                onClick={() => setShowCloseConfirm(false)}
                                                className={`${btnOutline} text-sm`}
                                            >
                                                {t("personalSpaceEmployer.confirmClose.cancel")}
                                            </button>
                                            <button
                                                type="button"
                                                onClick={handleCloseForm}
                                                className={btnModalPrimary}
                                            >
                                                {t("personalSpaceEmployer.confirmClose.confirm")}
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </>
                    )}
                    <h2 className={sectionTitle}>{t("personalSpaceEmployer.jobOffers")}</h2>
                    <ListJobOffer refreshKey={refreshKey} />
                </div>
            </div>
        </>
    );
}

export default PersonalSpaceEmployer;
