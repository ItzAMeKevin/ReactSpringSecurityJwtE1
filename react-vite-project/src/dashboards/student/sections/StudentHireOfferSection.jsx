import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";
import HireOfferListItem from "@/dashboards/student/components/HireOfferListItem.jsx";

const StudentHireOfferSection = () => {
    const { t } = useTranslation();
    const { hires, acceptHire, declineHire } = useOutletContext();

    return (
        <DashboardSection
            title={t("etudiant.nav.hireOffers")}
            status={hires.status}
            isEmpty={hires.items.length === 0}
            loadingText={t("etudiantEmbauches.loading")}
            errorText={t("errors.generic")}
            emptyText={t("etudiantEmbauches.empty")}
        >
            {hires.items.map((hire) => (
                <HireOfferListItem key={hire.id} hire={hire} onAccept={acceptHire} onDecline={declineHire} />
            ))}
        </DashboardSection>
    );
};

export default StudentHireOfferSection;
