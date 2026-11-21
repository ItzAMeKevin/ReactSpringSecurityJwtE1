import { useTranslation } from "react-i18next";
import useFetchList from "@/hooks/useFetchList.js";
import useToast from "@/hooks/useToast.js";
import sendPut from "@/utils/sendPut.js";
import DashboardLayout from "@/components/layout/DashboardLayout.jsx";
import ToastPopup from "@/components/ui/ToastPopup.jsx";

const cvParams = (cv) => ({ name: `${cv?.studentFirstName} ${cv?.studentLastName}` });
const offerParams = (offer) => ({ title: offer?.title });

const PersonalSpaceManager = () => {
    const { t } = useTranslation();
    const cvs = useFetchList("/gestionnaire/cv/pending");
    const offers = useFetchList("/gestionnaire/offres/pending");
    const candidatures = useFetchList("/gestionnaire/candidatures");
    const { message, showMessage, clearMessage } = useToast();

    const reviewAndRemove = (list, buildUrl, messageKey, toParams, variant) => async (id, payload) => {
        const item = list.items.find((entry) => entry.id === id);
        await sendPut(buildUrl(id), payload);
        list.removeById(id);
        showMessage(messageKey, toParams(item), variant);
    };

    const context = {
        cvs,
        offers,
        candidatures,
        acceptCv: reviewAndRemove(cvs, (id) => `/gestionnaire/cv/${id}/accept`, "gestionnaire.accept.success", cvParams),
        declineCv: reviewAndRemove(cvs, (id) => `/gestionnaire/cv/${id}/decline`, "gestionnaire.decline.success", cvParams, "danger"),
        acceptOffer: reviewAndRemove(offers, (id) => `/gestionnaire/offres/${id}/accept`, "gestionnaire.offres.acceptSuccess", offerParams),
        refuseOffer: reviewAndRemove(offers, (id) => `/gestionnaire/offres/${id}/refuse`, "gestionnaire.offres.refuseSuccess", offerParams, "danger"),
    };

    const items = [
        { to: "cv", label: t("gestionnaire.nav.cvs"), count: cvs.count },
        { to: "offres", label: t("gestionnaire.nav.offres"), count: offers.count },
        { to: "suivi", label: t("gestionnaire.nav.suivi"), count: candidatures.count },
    ];

    return (
        <>
            {message && <ToastPopup message={t(message.key, message.params)} variant={message.variant} onClose={clearMessage} />}
            <DashboardLayout title={t("gestionnaire.title")} items={items} context={context} />
        </>
    );
};

export default PersonalSpaceManager;
