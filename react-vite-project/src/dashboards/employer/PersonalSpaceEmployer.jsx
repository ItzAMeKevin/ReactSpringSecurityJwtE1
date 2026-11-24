import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import useFetchList from "@/hooks/useFetchList.js";
import useToast from "@/hooks/useToast.js";
import DashboardLayout from "@/components/layout/DashboardLayout.jsx";
import Modal from "@/components/ui/Modal.jsx";
import ToastPopup from "@/components/ui/ToastPopup.jsx";
import FormJobOffer from "@/dashboards/employer/components/FormJobOffer.jsx";
import { EMPLOYER_OFFER_SECTIONS } from "@/dashboards/employer/employerOfferSections.js";
import { btnPrimary, btnOutline, btnModalPrimary, textMuted } from "@/styles/classes.js";

const withNewHire = (offer) => {
    const hiredCount = (offer.hiredCount ?? 0) + 1;
    const isFilled = offer.numberOfPositions != null && hiredCount >= offer.numberOfPositions;
    return { ...offer, hiredCount, status: isFilled ? "FILLED" : offer.status };
};

const PersonalSpaceEmployer = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const offers = useFetchList("/employer");
    const interviews = useFetchList("/employer/interviews");
    const hirePending = useFetchList("/employer/candidates/hire-pending");
    const [form, setForm] = useState(null);
    const [showCloseConfirm, setShowCloseConfirm] = useState(false);
    const { message, showMessage, clearMessage } = useToast();

    const openForm = (offer = null) => {
        setForm({ offer });
        setShowCloseConfirm(false);
    };

    const closeForm = () => {
        setForm(null);
        setShowCloseConfirm(false);
    };

    const handleFormSuccess = () => {
        showMessage(form.offer ? "personalSpaceEmployer.offerUpdated" : "personalSpaceEmployer.offerCreated");
        closeForm();
        void offers.reload();
        navigate("offres/en-attente");
    };

    const hireInOffer = (offerId) =>
        offers.setItems((current) => current.map((offer) => (offer.id === offerId ? withNewHire(offer) : offer)));

    const countOffers = (status) =>
        offers.status === "loaded" ? offers.items.filter((offer) => offer.status === status).length : null;

    const items = [
        ...EMPLOYER_OFFER_SECTIONS.map(({ path, labelKey, countedStatus }) => ({
            to: path,
            label: t(labelKey),
            count: countOffers(countedStatus),
        })),
        { to: "entrevues", label: t("personalSpaceEmployer.nav.interviews"), count: interviews.count },
        { to: "embauches-en-attente", label: t("personalSpaceEmployer.nav.hirePending"), count: hirePending.count },
    ];

    const actions = (
        <button type="button" onClick={() => openForm()} className={`${btnPrimary} w-full`}>
            {t("personalSpaceEmployer.createJobOffer")}
        </button>
    );

    return (
        <>
            {message && <ToastPopup message={t(message.key, message.params)} variant={message.variant} onClose={clearMessage} />}
            <DashboardLayout
                title={t("personalSpaceEmployer.title")}
                items={items}
                actions={actions}
                context={{ offers, interviews, hirePending, openForm, hireInOffer, onInterviewScheduled: interviews.reload }}
            />
            {form && (
                <Modal
                    title={t(form.offer ? "personalSpaceEmployer.editJobOffer" : "personalSpaceEmployer.createJobOffer")}
                    onClose={() => setShowCloseConfirm(true)}
                    widthClass="w-full max-w-2xl"
                >
                    <FormJobOffer offer={form.offer} onSuccess={handleFormSuccess} onCancel={() => setShowCloseConfirm(true)} />
                </Modal>
            )}
            {showCloseConfirm && (
                <div className="fixed inset-0 z-[1001] flex items-center justify-center bg-black/40">
                    <div className="mx-4 w-full max-w-sm rounded-lg bg-white p-6 shadow-xl">
                        <h3 className="mb-2 text-lg font-bold text-main">{t("personalSpaceEmployer.confirmClose.title")}</h3>
                        <p className={`mb-6 text-sm ${textMuted}`}>{t("personalSpaceEmployer.confirmClose.message")}</p>
                        <div className="flex justify-end gap-2">
                            <button type="button" onClick={() => setShowCloseConfirm(false)} className={`${btnOutline} text-sm`}>
                                {t("personalSpaceEmployer.confirmClose.cancel")}
                            </button>
                            <button type="button" onClick={closeForm} className={btnModalPrimary}>
                                {t("personalSpaceEmployer.confirmClose.confirm")}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
};

export default PersonalSpaceEmployer;
