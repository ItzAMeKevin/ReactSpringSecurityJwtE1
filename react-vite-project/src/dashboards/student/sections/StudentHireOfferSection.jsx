import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";

const StudentHireOfferSection = () => {
    const { t } = useTranslation();

    return (
        <DashboardSection
            title={t("etudiant.nav.hireOffers")}
            status="loaded"
            isEmpty
            emptyText={t("dashboard.comingSoon")}
        />
    );
};

export default StudentHireOfferSection;
