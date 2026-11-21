import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";

const EmployerHirePendingSection = () => {
    const { t } = useTranslation();

    return (
        <DashboardSection
            title={t("personalSpaceEmployer.nav.hirePending")}
            status="loaded"
            isEmpty
            emptyText={t("dashboard.comingSoon")}
        />
    );
};

export default EmployerHirePendingSection;
