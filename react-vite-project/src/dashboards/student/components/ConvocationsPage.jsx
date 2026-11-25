import { useTranslation } from 'react-i18next';
import { pageWrapper, pageInner } from '../../../styles/classes.js';
import StudentInterviewList from './StudentInterviewList.jsx';

const ConvocationsPage = () => {
    const { t } = useTranslation();

    return (
        <div className={pageWrapper}>
            <div className={pageInner}>
                <h2 className="text-main text-center text-xl font-bold mb-6">
                    {t('convocations.title')}
                </h2>
                <StudentInterviewList />
            </div>
        </div>
    );
};

export default ConvocationsPage;
