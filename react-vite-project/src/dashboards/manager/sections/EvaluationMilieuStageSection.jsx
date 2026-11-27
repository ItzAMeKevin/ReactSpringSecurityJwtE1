import { useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import DashboardSection from '@/components/layout/DashboardSection.jsx';
import EvaluationMilieuStageForm from '@/dashboards/manager/components/EvaluationMilieuStageForm.jsx';
import { cardItem, btnSmPrimary, textMuted } from '@/styles/classes.js';
import { formatDateFrench } from '@/utils/dateFormatting.js';

const EvaluationMilieuStageSection = () => {
    const { t } = useTranslation();
    const { milieux } = useOutletContext();
    const [selected, setSelected] = useState(null);

    const handleSuccess = () => {
        milieux.removeById(selected?.id);
        setSelected(null);
    };

    return (
        <>
            <DashboardSection
                title={t('evaluationMilieu.title')}
                status={milieux.status}
                isEmpty={milieux.items.length === 0}
                loadingText={t('evaluationMilieu.loading')}
                errorText={t('evaluationMilieu.error')}
                emptyText={t('evaluationMilieu.empty')}
            >
                {milieux.items.map(item => (
                    <div key={item.id} className={`${cardItem} flex items-center justify-between gap-4`}>
                        <div className="min-w-0 flex-1">
                            <p className="font-semibold text-main">
                                {item.studentFirstName} {item.studentLastName}
                            </p>
                            <p className={`${textMuted} text-sm`}>{item.companyName}</p>
                            <p className={`${textMuted} text-xs`}>
                                {formatDateFrench(item.startDate)}
                            </p>
                        </div>
                        <button
                            type="button"
                            onClick={() => setSelected(item)}
                            className={btnSmPrimary}
                        >
                            {t('evaluationMilieu.evaluer')}
                        </button>
                    </div>
                ))}
            </DashboardSection>

            <EvaluationMilieuStageForm
                isOpen={selected !== null}
                internship={selected}
                onClose={() => setSelected(null)}
                onSuccess={handleSuccess}
            />
        </>
    );
};

export default EvaluationMilieuStageSection;
