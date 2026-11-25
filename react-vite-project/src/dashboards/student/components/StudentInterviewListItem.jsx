import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { btnTextLink, cardItem, interviewTypeBadgeColor, statusBadge, statusBadgeColor, textMuted } from '../../../styles/classes.js';
import { formatSlotDate } from '../../../utils/dateFormatting.js';
import StudentInterviewResponseModal from './StudentInterviewResponseModal.jsx';

const StudentInterviewListItem = ({ interview, onRespond }) => {
    const { t, i18n } = useTranslation();
    const [modalOpen, setModalOpen] = useState(false);

    const isExpired = interview.expired;
    const isPending = interview.status === 'PENDING';
    const displayStatus = isPending && isExpired ? 'EXPIRED' : interview.status;

    const earliestSlot = interview.slots?.length > 0
        ? interview.slots.reduce((a, b) => a.date < b.date ? a : b)
        : null;

    const formattedDate = earliestSlot ? formatSlotDate(earliestSlot.date, i18n.language) : null;

    const canRespond = isPending && !isExpired;

    return (
        <>
            <div className={`${cardItem} flex items-center justify-between gap-4`}>
                <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 flex-wrap mb-1">
                        <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${interviewTypeBadgeColor(interview.type)}`}>
                            {t(`convocations.type.${interview.type}`)}
                        </span>
                        <span className={`${statusBadge} ${statusBadgeColor(displayStatus)}`}>
                            {t(`convocations.status.${displayStatus}`)}
                        </span>
                    </div>
                    <p className="font-semibold text-main truncate">{interview.companyName}</p>
                    <p className={`${textMuted} text-sm truncate`}>{interview.offerTitle}</p>
                    {formattedDate && (
                        <p className={`${textMuted} text-xs mt-1`}>{formattedDate}</p>
                    )}
                    {displayStatus === 'EXPIRED' && (
                        <p className="text-xs text-gray-500 mt-1 italic">{t('convocations.expired')}</p>
                    )}
                    {interview.status === 'DECLINED' && interview.studentMessage && (
                        <p className={`${textMuted} text-xs mt-1 italic`}>"{interview.studentMessage}"</p>
                    )}
                </div>

                {canRespond && (
                    <button
                        onClick={() => setModalOpen(true)}
                        className={`shrink-0 ${btnTextLink}`}
                    >
                        {t('convocations.modal.title')} →
                    </button>
                )}
            </div>

            <StudentInterviewResponseModal
                isOpen={modalOpen}
                onClose={() => setModalOpen(false)}
                interview={interview}
                onRespond={(updated) => {
                    onRespond(updated);
                    setModalOpen(false);
                }}
            />
        </>
    );
};

export default StudentInterviewListItem;
