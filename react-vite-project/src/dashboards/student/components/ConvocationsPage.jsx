import { useOutletContext } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import DashboardSection from '@/components/layout/DashboardSection.jsx';
import StudentInterviewList from '@/dashboards/student/components/StudentInterviewList.jsx';

const ConvocationsPage = () => {
    const { t } = useTranslation();
    const { interviews } = useOutletContext();

    return (
        <DashboardSection
            title={t('convocations.title')}
            status={interviews.status}
            isEmpty={interviews.items.length === 0}
            loadingText={t('convocations.loading')}
            errorText={t('convocations.error')}
            emptyText={t('convocations.empty')}
        >
            <StudentInterviewList interviews={interviews} />
        </DashboardSection>
    );
};

export default ConvocationsPage;
