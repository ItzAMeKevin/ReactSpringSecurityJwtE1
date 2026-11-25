import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";
import StudentInterviewListItem from "@/dashboards/student/components/StudentInterviewListItem.jsx";

const StudentConvocationSection = () => {
    const { t } = useTranslation();
    const { interviews } = useOutletContext();

    const handleRespond = (updated) => {
        interviews.setItems((prev) => prev.map((i) => (i.id === updated.id ? updated : i)));
    };

    return (
        <DashboardSection
            title={t("convocations.title")}
            status={interviews.status}
            isEmpty={interviews.items.length === 0}
            loadingText={t("convocations.loading")}
            errorText={t("convocations.error")}
            emptyText={t("convocations.empty")}
        >
            {interviews.items.map((interview) => (
                <StudentInterviewListItem
                    key={interview.id}
                    interview={interview}
                    onRespond={handleRespond}
                />
            ))}
        </DashboardSection>
    );
};

export default StudentConvocationSection;
