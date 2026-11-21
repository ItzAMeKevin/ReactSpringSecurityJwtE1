import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";
import InterviewCard from "@/dashboards/employer/components/InterviewCard.jsx";

const EmployerInterviewSection = () => {
    const { t } = useTranslation();
    const { interviews } = useOutletContext();

    return (
        <DashboardSection
            title={t("personalSpaceEmployer.nav.interviews")}
            status={interviews.status}
            isEmpty={interviews.items.length === 0}
            loadingText={t("employerInterviews.loading")}
            errorText={t("errors.generic")}
            emptyText={t("employerInterviews.empty")}
        >
            {interviews.items.map((interview) => (
                <InterviewCard key={interview.id} interview={interview} />
            ))}
        </DashboardSection>
    );
};

export default EmployerInterviewSection;
