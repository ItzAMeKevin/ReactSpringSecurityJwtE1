import { useTranslation } from "react-i18next";
import useFetchList from "../../../hooks/useFetchList.js";
import sendPut from "../../../utils/sendPut.js";
import DashboardLayout from "../../DashboardLayout.jsx";

const PersonalSpaceManager = () => {
    const { t } = useTranslation();
    const cvs = useFetchList("/gestionnaire/cv/pending");
    const offers = useFetchList("/gestionnaire/offres/pending");
    const candidatures = useFetchList("/gestionnaire/candidatures");

    const reviewAndRemove = (list, buildUrl) => async (id, payload) => {
        await sendPut(buildUrl(id), payload);
        list.removeById(id);
    };

    const context = {
        cvs,
        offers,
        candidatures,
        acceptCv: reviewAndRemove(cvs, (id) => `/gestionnaire/cv/${id}/accept`),
        declineCv: reviewAndRemove(cvs, (id) => `/gestionnaire/cv/${id}/decline`),
        acceptOffer: reviewAndRemove(offers, (id) => `/gestionnaire/offres/${id}/accept`),
        refuseOffer: reviewAndRemove(offers, (id) => `/gestionnaire/offres/${id}/refuse`),
    };

    const items = [
        { to: "cv", label: t("gestionnaire.nav.cvs"), count: cvs.count },
        { to: "offres", label: t("gestionnaire.nav.offres"), count: offers.count },
        { to: "suivi", label: t("gestionnaire.nav.suivi"), count: candidatures.count },
    ];

    return <DashboardLayout title={t("gestionnaire.title")} items={items} context={context} />;
};

export default PersonalSpaceManager;
