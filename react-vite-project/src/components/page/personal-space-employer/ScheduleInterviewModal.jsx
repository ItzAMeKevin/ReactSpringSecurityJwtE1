import { useState, useRef, useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import fetcher from '../../../utils/fetcher.js';
import InterviewSlotsForm from './InterviewSlotsForm.jsx';
import { btnOutline, btnPrimary, textError } from '../../../styles/classes.js';

const INITIAL_FORM_STATE = {
    interviewType: 'ONLINE',
    videoLink: '',
    address: '',
    notes: '',
    slots: []
};

const ScheduleInterviewModal = ({ isOpen, onClose, candidate, candidatureId, onSuccess }) => {
    const { t } = useTranslation();
    const modalRef = useRef(null);

    const [formData, setFormData] = useState(INITIAL_FORM_STATE);
    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState(null);
    const [submissionAttempted, setSubmissionAttempted] = useState(false);

    const resetForm = useCallback(() => {
        setFormData(INITIAL_FORM_STATE);
        setApiError(null);
        setSubmissionAttempted(false);
    }, []);

    const handleFieldChange = (field, value) => {
        setFormData(prev => {
            const updated = { ...prev, [field]: value };
            
            if (field === 'interviewType') {
                if (value === 'ONLINE') updated.address = '';
                if (value === 'IN_PERSON') updated.videoLink = '';
            }
            
            return updated;
        });
    };

    const handleAddSlot = (slot) => {
        setFormData(prev => ({
            ...prev,
            slots: [...prev.slots, slot]
        }));
    };

    const handleRemoveSlot = (slotId) => {
        setFormData(prev => ({
            ...prev,
            slots: prev.slots.filter(slot => slot.id !== slotId)
        }));
    };

    const handleModifySlot = (slotId, updatedSlot) => {
        setFormData(prev => ({
            ...prev,
            slots: prev.slots.map(slot => 
                slot.id === slotId ? { ...slot, ...updatedSlot } : slot
            )
        }));
    };

    const validateForm = () => {
        if (!formData.interviewType) return false;
        if (formData.interviewType === 'ONLINE' && !formData.videoLink) return false;
        if (formData.interviewType === 'IN_PERSON' && !formData.address) return false;
        if (formData.slots.length === 0) return false;
        return true;
    };

    const handleSubmit = async () => {
        setApiError(null);

        if (!validateForm()) {
            setApiError(t('scheduleInterview.errors.fillAllFields'));
            setSubmissionAttempted(true);
            if (modalRef.current) {
                modalRef.current.scrollTop = 0;
            }
            return;
        }

        setIsLoading(true);

        try {
            const response = await fetcher('/interview/schedule', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    candidatureId,
                    type: formData.interviewType,
                    videoLink: formData.interviewType === 'ONLINE' ? formData.videoLink : null,
                    address: formData.interviewType === 'IN_PERSON' ? formData.address : null,
                    notes: formData.notes || null,
                    slots: formData.slots.map(slot => ({
                        date: slot.date,
                        startTime: slot.startTime,
                        endTime: slot.endTime
                    }))
                })
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || t('scheduleInterview.errors.generic'));
            }

            resetForm();
            if (onSuccess) onSuccess();
            onClose();
        } catch (error) {
            console.error('Erreur lors de la planification:', error);
            setApiError(error.message || t('scheduleInterview.errors.generic'));
        } finally {
            setIsLoading(false);
        }
    };

    const handleClose = () => {
        if (isLoading) return;
        resetForm();
        onClose();
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => !isLoading && handleClose()}>
            <div
                ref={modalRef}
                className="relative bg-white rounded-lg shadow-lg max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto"
                onClick={(e) => e.stopPropagation()}
            >
                <div className="sticky top-0 bg-white border-b border-brand/20 px-6 py-4 flex items-center justify-between">
                    <div>
                        <h2 className="text-xl font-bold text-main">
                            {t('scheduleInterview.title')}
                        </h2>
                        {candidate && (
                            <p className="text-sm text-main/70 mt-1">
                                {t('scheduleInterview.with')} {candidate.firstName} {candidate.lastName}
                            </p>
                        )}
                    </div>
                    <button
                        onClick={handleClose}
                        disabled={isLoading}
                        className="text-main/50 hover:text-main text-2xl font-bold disabled:opacity-50"
                    >
                        ✕
                    </button>
                </div>

                <div className="px-6 py-6">
                    {apiError && (
                        <div className={textError + ' p-4 rounded bg-red-50 border border-red-200 mb-6'} role="alert">
                            <div className="flex items-start gap-2">
                                <div>
                                    <p className="font-semibold">{t('scheduleInterview.errors.title')}</p>
                                    <p className="text-sm mt-1">{apiError}</p>
                                    <button
                                        onClick={() => setApiError(null)}
                                        className="text-sm mt-2 underline font-medium"
                                    >
                                        {t('scheduleInterview.errors.retry')}
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}

                    <InterviewSlotsForm
                        slots={formData.slots}
                        onAddSlot={handleAddSlot}
                        onRemoveSlot={handleRemoveSlot}
                        onModifySlot={handleModifySlot}
                        interviewType={formData.interviewType}
                        videoLink={formData.videoLink}
                        address={formData.address}
                        notes={formData.notes}
                        onFieldChange={handleFieldChange}
                        submissionAttempted={submissionAttempted}
                    />
                </div>

                {/* Footer */}
                <div className="sticky bottom-0 bg-white border-t border-brand/20 px-6 py-4 flex items-center justify-end gap-3">
                    <button
                        onClick={handleClose}
                        disabled={isLoading}
                        className={btnOutline + ' disabled:opacity-50'}
                    >
                        {t('scheduleInterview.cancel')}
                    </button>
                    <button
                        onClick={handleSubmit}
                        disabled={isLoading || formData.slots.length === 0}
                        className={btnPrimary + ' flex items-center gap-2 disabled:opacity-50'}
                    >
                        {isLoading && (
                            <svg className="animate-spin -ml-1 mr-1 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                        )}
                        {isLoading ? t('scheduleInterview.scheduling') : t('scheduleInterview.proposeSlots')}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ScheduleInterviewModal;
