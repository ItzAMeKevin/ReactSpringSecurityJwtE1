import { useState, useRef, useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import fetcher from '../../../utils/fetcher.js';
import { btnOutline, btnPrimary, inputField, textError } from '../../../styles/classes.js';

const getInitialFormData = (candidature) => ({
    studentFirstName: candidature?.student?.firstName || '',
    studentLastName: candidature?.student?.lastName || '',
    studentEmail: candidature?.student?.email || '',
    studentMatricule: candidature?.student?.matricule || '',
    studentProgramme: candidature?.student?.programme?.name || '',
    companyName: candidature?.jobOffer?.company?.name || '',
    companyEmail: candidature?.jobOffer?.company?.email || '',
    companyPhone: candidature?.jobOffer?.company?.telephone || '',
    companyAddress: candidature?.jobOffer?.company?.adresse || '',
    companyCity: candidature?.jobOffer?.company?.ville || '',
    companyPostalCode: candidature?.jobOffer?.company?.codePostal || '',
    companyCountry: candidature?.jobOffer?.company?.pays || 'Canada',
    jobTitle: candidature?.jobOffer?.title || '',
    jobDescription: candidature?.jobOffer?.description || '',
    startDate: candidature?.jobOffer?.startingDate || '',
    durationWeeks: candidature?.jobOffer?.durationInWeeks || '',
    hourlyRate: candidature?.jobOffer?.salary || '',
    supervisor: '',
    supervisorEmail: '',
    supervisorPhone: '',
    workSchedule: '',
    objectives: '',
    additionalTerms: '',
    notes: ''
});

const ContractGenerationModal = ({ isOpen, onClose, candidature, user }) => {
    const { t } = useTranslation();
    const modalRef = useRef(null);

    const [formData, setFormData] = useState(() => getInitialFormData(candidature));
    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState(null);
    const [submissionAttempted, setSubmissionAttempted] = useState(false);

    const calculateEndDate = useCallback(() => {
        if (!formData.startDate || !formData.durationWeeks) return '';
        const start = new Date(formData.startDate);
        const durationDays = parseInt(formData.durationWeeks) * 7;
        const end = new Date(start.getTime() + durationDays * 24 * 60 * 60 * 1000);
        return end.toISOString().split('T')[0];
    }, [formData.startDate, formData.durationWeeks]);

    const handleFieldChange = (field, value) => {
        setFormData(prev => ({ ...prev, [field]: value }));
        setApiError(null);
    };

    const validateForm = () => {
        const requiredFields = [
            'studentFirstName', 'studentLastName', 'studentEmail', 'studentMatricule',
            'companyName', 'companyEmail', 'jobTitle',
            'startDate', 'durationWeeks', 'hourlyRate',
            'supervisor', 'supervisorEmail', 'workSchedule', 'objectives'
        ];
        return requiredFields.every(field => formData[field]);
    };

    const handleSubmit = async () => {
        setApiError(null);
        setSubmissionAttempted(true);

        if (!validateForm()) {
            setApiError(t('contractGeneration.errors.pleaseComplete'));
            if (modalRef.current) modalRef.current.scrollTop = 0;
            return;
        }

        setIsLoading(true);

        try {
            const endDate = calculateEndDate();
            const response = await fetcher('/gestionnaire/contract/create', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    studentId: candidature.student?.id,
                    employerId: candidature.jobOffer?.employer?.id,
                    managerId: user.id,
                    ...formData,
                    endDate
                })
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || t('contractGeneration.errors.generic'));
            }

            onClose();
        } catch (error) {
            setApiError(error.message || t('contractGeneration.errors.generic'));
        } finally {
            setIsLoading(false);
        }
    };

    const handleClose = () => {
        if (isLoading) return;
        setApiError(null);
        setSubmissionAttempted(false);
        setFormData(getInitialFormData(candidature));
        onClose();
    };

    if (!isOpen) return null;

    const endDate = calculateEndDate();

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => !isLoading && handleClose()}>
            <div
                ref={modalRef}
                className="relative bg-white rounded-lg shadow-lg max-w-4xl w-full mx-4 max-h-[90vh] overflow-y-auto"
                onClick={(e) => e.stopPropagation()}
            >
                {/* Header */}
                <div className="sticky top-0 bg-white border-b border-brand/20 px-6 py-4 flex items-center justify-between">
                    <div>
                        <h2 className="text-xl font-bold text-main">
                            {t('contractGeneration.contractModal.title')}
                        </h2>
                        <p className="text-sm text-main/70 mt-1">
                            {t('contractGeneration.contractModal.subtitle')}
                        </p>
                    </div>
                    <button
                        onClick={handleClose}
                        disabled={isLoading}
                        className="text-main/50 hover:text-main text-2xl font-bold disabled:opacity-50"
                    >
                        ✕
                    </button>
                </div>

                {/* Content */}
                <div className="px-6 py-6 space-y-6">
                    {apiError && (
                        <div className={textError + ' p-4 rounded bg-red-50 border border-red-200'} role="alert">
                            <div className="flex items-start gap-2">
                                <div>
                                    <p className="font-semibold">{t('contractGeneration.errors.title')}</p>
                                    <p className="text-sm mt-1">{apiError}</p>
                                    <button
                                        onClick={() => setApiError(null)}
                                        className="text-sm mt-2 underline font-medium"
                                    >
                                        {t('contractGeneration.errors.retry')}
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Student Information */}
                    <fieldset className="space-y-3">
                        <legend className="text-sm font-bold text-main border-b border-brand/20 pb-2">
                            {t('contractGeneration.form.studentInfo')}
                        </legend>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.firstName')} *
                                </label>
                                <input
                                    type="text"
                                    value={formData.studentFirstName}
                                    onChange={(e) => handleFieldChange('studentFirstName', e.target.value)}
                                    className={inputField + (submissionAttempted && !formData.studentFirstName ? ' border-red-600' : '')}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.lastName')} *
                                </label>
                                <input
                                    type="text"
                                    value={formData.studentLastName}
                                    onChange={(e) => handleFieldChange('studentLastName', e.target.value)}
                                    className={inputField + (submissionAttempted && !formData.studentLastName ? ' border-red-600' : '')}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.email')} *
                                </label>
                                <input
                                    type="email"
                                    value={formData.studentEmail}
                                    onChange={(e) => handleFieldChange('studentEmail', e.target.value)}
                                    className={inputField + (submissionAttempted && !formData.studentEmail ? ' border-red-600' : '')}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.matricule')} *
                                </label>
                                <input
                                    type="text"
                                    value={formData.studentMatricule}
                                    disabled
                                    className={inputField + ' bg-gray-100 cursor-not-allowed'}
                                />
                            </div>
                        </div>
                        <div>
                            <label className="block text-sm font-semibold text-main mb-1">
                                {t('contractGeneration.form.programme')}
                            </label>
                            <input
                                type="text"
                                value={formData.studentProgramme}
                                onChange={(e) => handleFieldChange('studentProgramme', e.target.value)}
                                className={inputField}
                            />
                        </div>
                    </fieldset>

                    {/* Employer Information */}
                    <fieldset className="space-y-3">
                        <legend className="text-sm font-bold text-main border-b border-brand/20 pb-2">
                            {t('contractGeneration.form.employerInfo')}
                        </legend>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.companyName')} *
                                </label>
                                <input
                                    type="text"
                                    value={formData.companyName}
                                    onChange={(e) => handleFieldChange('companyName', e.target.value)}
                                    className={inputField + (submissionAttempted && !formData.companyName ? ' border-red-600' : '')}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.companyEmail')} *
                                </label>
                                <input
                                    type="email"
                                    value={formData.companyEmail}
                                    onChange={(e) => handleFieldChange('companyEmail', e.target.value)}
                                    className={inputField + (submissionAttempted && !formData.companyEmail ? ' border-red-600' : '')}
                                />
                            </div>
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.companyPhone')}
                                </label>
                                <input
                                    type="tel"
                                    value={formData.companyPhone}
                                    onChange={(e) => handleFieldChange('companyPhone', e.target.value)}
                                    className={inputField}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.companyCountry')}
                                </label>
                                <input
                                    type="text"
                                    value={formData.companyCountry}
                                    onChange={(e) => handleFieldChange('companyCountry', e.target.value)}
                                    className={inputField}
                                />
                            </div>
                        </div>
                        <div className="grid grid-cols-3 gap-4">
                            <div className="col-span-2">
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.companyAddress')}
                                </label>
                                <input
                                    type="text"
                                    value={formData.companyAddress}
                                    onChange={(e) => handleFieldChange('companyAddress', e.target.value)}
                                    className={inputField}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.companyCity')}
                                </label>
                                <input
                                    type="text"
                                    value={formData.companyCity}
                                    onChange={(e) => handleFieldChange('companyCity', e.target.value)}
                                    className={inputField}
                                />
                            </div>
                        </div>
                        <div>
                            <label className="block text-sm font-semibold text-main mb-1">
                                {t('contractGeneration.form.companyPostalCode')}
                            </label>
                            <input
                                type="text"
                                value={formData.companyPostalCode}
                                onChange={(e) => handleFieldChange('companyPostalCode', e.target.value)}
                                className={inputField}
                            />
                        </div>
                    </fieldset>

                    {/* Position Information */}
                    <fieldset className="space-y-3">
                        <legend className="text-sm font-bold text-main border-b border-brand/20 pb-2">
                            {t('contractGeneration.form.positionInfo')}
                        </legend>
                        <div>
                            <label className="block text-sm font-semibold text-main mb-1">
                                {t('contractGeneration.form.jobTitle')} *
                            </label>
                            <input
                                type="text"
                                value={formData.jobTitle}
                                onChange={(e) => handleFieldChange('jobTitle', e.target.value)}
                                className={inputField + (submissionAttempted && !formData.jobTitle ? ' border-red-600' : '')}
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-semibold text-main mb-1">
                                {t('contractGeneration.form.jobDescription')}
                            </label>
                            <textarea
                                value={formData.jobDescription}
                                onChange={(e) => handleFieldChange('jobDescription', e.target.value)}
                                rows="3"
                                className={inputField}
                            />
                        </div>
                        <div className="grid grid-cols-3 gap-4">
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.startDate')} *
                                </label>
                                <input
                                    type="date"
                                    value={formData.startDate}
                                    onChange={(e) => handleFieldChange('startDate', e.target.value)}
                                    className={inputField + (submissionAttempted && !formData.startDate ? ' border-red-600' : '')}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.endDate')}
                                </label>
                                <input
                                    type="date"
                                    value={endDate}
                                    disabled
                                    className={inputField + ' bg-gray-100 cursor-not-allowed'}
                                />
                                <p className="text-xs text-main/70 mt-1">
                                    {t('contractGeneration.form.endDateAuto')}
                                </p>
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.durationWeeks')} *
                                </label>
                                <input
                                    type="number"
                                    min="1"
                                    value={formData.durationWeeks}
                                    onChange={(e) => handleFieldChange('durationWeeks', e.target.value)}
                                    className={inputField + (submissionAttempted && !formData.durationWeeks ? ' border-red-600' : '')}
                                />
                            </div>
                        </div>
                        <div>
                            <label className="block text-sm font-semibold text-main mb-1">
                                {t('contractGeneration.form.hourlyRate')} *
                            </label>
                            <input
                                type="number"
                                step="0.01"
                                min="0"
                                placeholder="20.00"
                                value={formData.hourlyRate}
                                onChange={(e) => handleFieldChange('hourlyRate', e.target.value)}
                                className={inputField + (submissionAttempted && !formData.hourlyRate ? ' border-red-600' : '')}
                            />
                        </div>
                    </fieldset>

                    {/* Contract Terms */}
                    <fieldset className="space-y-3">
                        <legend className="text-sm font-bold text-main border-b border-brand/20 pb-2">
                            {t('contractGeneration.form.contractTerms')}
                        </legend>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.supervisor')} *
                                </label>
                                <input
                                    type="text"
                                    placeholder={t('contractGeneration.form.supervisorPlaceholder')}
                                    value={formData.supervisor}
                                    onChange={(e) => handleFieldChange('supervisor', e.target.value)}
                                    className={inputField + (submissionAttempted && !formData.supervisor ? ' border-red-600' : '')}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.supervisorEmail')} *
                                </label>
                                <input
                                    type="email"
                                    value={formData.supervisorEmail}
                                    onChange={(e) => handleFieldChange('supervisorEmail', e.target.value)}
                                    className={inputField + (submissionAttempted && !formData.supervisorEmail ? ' border-red-600' : '')}
                                />
                            </div>
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-semibold text-main mb-1">
                                    {t('contractGeneration.form.supervisorPhone')}
                                </label>
                                <input
                                    type="tel"
                                    value={formData.supervisorPhone}
                                    onChange={(e) => handleFieldChange('supervisorPhone', e.target.value)}
                                    className={inputField}
                                />
                            </div>
                        </div>
                        <div>
                            <label className="block text-sm font-semibold text-main mb-1">
                                {t('contractGeneration.form.workSchedule')} *
                            </label>
                            <input
                                type="text"
                                placeholder={t('contractGeneration.form.workSchedulePlaceholder')}
                                value={formData.workSchedule}
                                onChange={(e) => handleFieldChange('workSchedule', e.target.value)}
                                className={inputField + (submissionAttempted && !formData.workSchedule ? ' border-red-600' : '')}
                            />
                        </div>
                    </fieldset>

                    {/* Additional Information */}
                    <fieldset className="space-y-3">
                        <legend className="text-sm font-bold text-main border-b border-brand/20 pb-2">
                            {t('contractGeneration.form.additionalInfo')}
                        </legend>
                        <div>
                            <label className="block text-sm font-semibold text-main mb-1">
                                {t('contractGeneration.form.objectives')} *
                            </label>
                            <textarea
                                placeholder={t('contractGeneration.form.objectivesPlaceholder')}
                                value={formData.objectives}
                                onChange={(e) => handleFieldChange('objectives', e.target.value)}
                                rows="3"
                                className={inputField + (submissionAttempted && !formData.objectives ? ' border-red-600' : '')}
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-semibold text-main mb-1">
                                {t('contractGeneration.form.additionalTerms')}
                            </label>
                            <textarea
                                placeholder={t('contractGeneration.form.additionalTermsPlaceholder')}
                                value={formData.additionalTerms}
                                onChange={(e) => handleFieldChange('additionalTerms', e.target.value)}
                                rows="3"
                                className={inputField}
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-semibold text-main mb-1">
                                {t('contractGeneration.form.notes')}
                            </label>
                            <textarea
                                value={formData.notes}
                                onChange={(e) => handleFieldChange('notes', e.target.value)}
                                rows="2"
                                className={inputField}
                            />
                        </div>
                    </fieldset>
                </div>

                {/* Footer */}
                <div className="sticky bottom-0 bg-white border-t border-brand/20 px-6 py-4 flex items-center justify-end gap-3">
                    <button
                        onClick={handleClose}
                        disabled={isLoading}
                        className={btnOutline + ' disabled:opacity-50'}
                    >
                        {t('contractGeneration.contractModal.cancel')}
                    </button>
                    <button
                        onClick={handleSubmit}
                        disabled={isLoading}
                        className={btnPrimary + ' flex items-center gap-2 disabled:opacity-50'}
                    >
                        {isLoading && (
                            <svg className="animate-spin -ml-1 mr-1 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                        )}
                        {isLoading ? t('contractGeneration.contractModal.submitting') : t('contractGeneration.contractModal.submit')}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ContractGenerationModal;
