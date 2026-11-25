import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import fetcher from "@/utils/fetcher.js";
import { getApiErrorKey } from "@/utils/apiError.js";
import { getNextSessionCode, getSessionCode } from "@/utils/session.js";
import useFetchList from "@/hooks/useFetchList.js";
import useToast from "@/hooks/useToast.js";
import sendPut from "@/utils/sendPut.js";
import DashboardLayout from "@/components/layout/DashboardLayout.jsx";
import ToastPopup from "@/components/ui/ToastPopup.jsx";
import CvUploadModal from "@/dashboards/student/components/CvUploadModal.jsx";
import { STUDENT_CV_SECTIONS } from "@/dashboards/student/studentCvSections.js";
import { btnPrimary } from "@/styles/classes.js";

const fileToBase64 = (file) =>
    new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result.split(",")[1]);
        reader.onerror = () => reject(reader.error);
        reader.readAsDataURL(file);
    });

const isUniqueFileName = (cv, index, all) => all.findIndex((other) => other.fileName === cv.fileName) === index;

const PersonalSpaceStudent = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const cvs = useFetchList("/etudiant/cv");
    const offers = useFetchList("/etudiant/offres");
    const hires = useFetchList("/etudiant/embauches");
    const interviews = useFetchList("/etudiant/interviews");
    const [isUploadOpen, setIsUploadOpen] = useState(false);
    const { message, showMessage, clearMessage } = useToast();

    const visibleCvs = cvs.items.filter(isUniqueFileName);
    const acceptedCvs = cvs.items.filter((cv) => cv.status === "ACCEPTED");
    const nextSession = getNextSessionCode();
    const applicableOffers = offers.items.filter((offer) => offer.startingDate && getSessionCode(offer.startingDate) === nextSession);

    const countCvs = (status) => (cvs.status === "loaded" ? visibleCvs.filter((cv) => cv.status === status).length : null);

    const uploadCv = async (file) => {
        const response = await fetcher("/etudiant/cv", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ fileName: file.name, contentType: file.type, content: await fileToBase64(file) }),
        });
        if (response.status !== 201) throw new Error(await getApiErrorKey(response));
        const cvMeta = await response.json();
        cvs.setItems((current) => [{ ...cvMeta, file }, ...current]);
        return cvMeta;
    };

    const handleUploaded = (cv) => {
        setIsUploadOpen(false);
        navigate("cv/en-attente", { state: { highlight: { kind: "cv", id: cv.id } } });
    };

    const respondToHire = (action, messageKey, variant) => async (id) => {
        const hire = hires.items.find((item) => item.id === id);
        await sendPut(`/etudiant/embauches/${id}/${action}`);
        hires.removeById(id);
        showMessage(messageKey, { title: hire?.jobOffer.title }, variant);
    };

    const items = [
        ...STUDENT_CV_SECTIONS.map(({ status, path, labelKey }) => ({ to: path, label: t(labelKey), count: countCvs(status) })),
        { to: "offres", label: t("etudiant.nav.offres"), count: offers.status === "loaded" ? applicableOffers.length : null },
        { to: "embauches", label: t("etudiant.nav.hireOffers"), count: hires.count },
        { to: "convocations", label: t("etudiant.nav.convocations"), count: interviews.count },
    ];

    const actions = (
        <button type="button" onClick={() => setIsUploadOpen(true)} className={`${btnPrimary} w-full`}>
            {t("televerser.choisirFichier")}
        </button>
    );

    return (
        <>
            {message && <ToastPopup message={t(message.key, message.params)} variant={message.variant} onClose={clearMessage} />}
            <DashboardLayout
                title={t("etudiant.title")}
                items={items}
                actions={actions}
                context={{
                    cvs, visibleCvs, acceptedCvs, offers, uploadCv, hires, interviews,
                    acceptHire: respondToHire("accepter", "etudiantEmbauches.acceptSuccess"),
                    declineHire: respondToHire("decliner", "etudiantEmbauches.declineSuccess", "danger"),
                }}
            />
            {isUploadOpen && (
                <CvUploadModal onUpload={uploadCv} onUploaded={handleUploaded} onClose={() => setIsUploadOpen(false)} />
            )}
        </>
    );
};

export default PersonalSpaceStudent;
