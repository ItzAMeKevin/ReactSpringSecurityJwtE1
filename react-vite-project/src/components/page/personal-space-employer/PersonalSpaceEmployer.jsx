import ListJobOffer from "./ListJobOffer.jsx";
import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import FormJobOffer from "./FormJobOffer.jsx";
import SuccessPopup from "../SuccessPopup.jsx";

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
            <div className="relative min-h-screen overflow-hidden bg-[#f3ebe3]">
                <div className="flex flex-col items-center gap-4 pt-20 pb-20 px-4">
                    <h1 className="text-2xl font-bold text-[#4b1113]">{t("personalSpaceEmployer.title")}</h1>
                    <button className="bg-[#4b1113] text-white px-4 py-2 rounded hover:bg-[#6b1c1f] transition-colors duration-300" onClick={handleOpenForm}>
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
                                        <h2 id="create-job-offer-title" className="mb-6 text-lg font-bold text-[#4b1113]">
                                            {t("personalSpaceEmployer.createJobOffer")}
                                        </h2>
                                        <FormJobOffer onSuccess={handleFormSuccess}/>
                                        <div className="mt-6 flex justify-end gap-2">
                                            <button type="button" className="bg-[#4b1113] text-white px-4 py-2 rounded hover:bg-[#6b1c1f] transition-colors duration-300" onClick={handleRequestClose}>
                                                {t("personalSpaceEmployer.close")}
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            {showCloseConfirm && (
                                <div className="fixed inset-0 z-[1001] flex items-center justify-center bg-black/40">
                                    <div className="bg-white rounded-lg shadow-xl p-6 max-w-sm w-full mx-4">
                                        <h3 className="text-lg font-bold text-[#4b1113] mb-2">{t("personalSpaceEmployer.confirmClose.title")}</h3>
                                        <p className="text-sm text-[#4b1113]/70 mb-6">{t("personalSpaceEmployer.confirmClose.message")}</p>
                                        <div className="flex justify-end gap-2">
                                            <button
                                                type="button"
                                                onClick={() => setShowCloseConfirm(false)}
                                                className="px-4 py-2 rounded text-sm font-medium border border-[#4b1113] text-[#4b1113] hover:bg-[#f3ebe3] transition-colors"
                                            >
                                                {t("personalSpaceEmployer.confirmClose.cancel")}
                                            </button>
                                            <button
                                                type="button"
                                                onClick={handleCloseForm}
                                                className="px-4 py-2 rounded text-sm font-medium bg-[#4b1113] text-white hover:bg-[#6b1c1f] transition-colors"
                                            >
                                                {t("personalSpaceEmployer.confirmClose.confirm")}
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </>
                    )}
                    <h2 className="text-xl font-bold text-[#4b1113]">{t("personalSpaceEmployer.jobOffers")}</h2>
                    <ListJobOffer refreshKey={refreshKey} />
                </div>
            </div>
        </>
    );
}

export default PersonalSpaceEmployer;
