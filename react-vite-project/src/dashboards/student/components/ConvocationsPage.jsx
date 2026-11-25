import { useTranslation } from 'react-i18next';
import DashboardSection from '@/components/layout/DashboardSection.jsx';
import StudentInterviewList from '@/dashboards/student/components/StudentInterviewList.jsx';

const ConvocationsPage = () => {
    const { t } = useTranslation();

    return (
        <DashboardSection title={t('convocations.title')} status="loaded" isEmpty={false}>
            <StudentInterviewList />
        </DashboardSection>
    );
};

export default ConvocationsPage;
