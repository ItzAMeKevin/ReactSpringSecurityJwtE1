import { useState } from 'react';
import { createPortal } from 'react-dom';
import { useTranslation } from 'react-i18next';
import fetcher from '../../../utils/fetcher.js';
import { btnPrimary, textError } from '../../../styles/classes.js';

const ContractGenerationModal = ({ isOpen, onClose, candidature, user }) => {
    const { t } = useTranslation();

    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState(null);

    const handleSubmit = async () => {
        setApiError(null);
        setIsLoading(true);

        try {
            const response = await fetcher('/gestionnaire/contract/create', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    candidature: candidature,
                    managerId: user?.id,
                })
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || t('contractGeneration.errors.generic'));
            }

            onClose();
        } catch (error) {
            console.error('Erreur lors de la génération:', error);
            setApiError(error.message || t('contractGeneration.errors.generic'));
        } finally {
            setIsLoading(false);
        }
    };

    if (!isOpen) return null;

    return createPortal(
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => !isLoading && onClose()}>
            <div
                className="relative bg-white rounded-lg shadow-lg max-w-md w-full mx-4"
                onClick={(e) => e.stopPropagation()}
            >
                {/* Header */}
                <div className="bg-white border-b border-brand/20 px-6 py-4 flex items-center justify-between">
                    <div>
                        <h2 className="text-xl font-bold text-main">
                            {t('contractGeneration.contractModal.title')}
                        </h2>
                    </div>
                    <button
                        onClick={onClose}
                        disabled={isLoading}
                        className="text-main/50 hover:text-main text-2xl font-bold disabled:opacity-50"
                    >
                        ✕
                    </button>
                </div>

                {/* Content */}
                <div className="px-6 py-6 space-y-4">
                    {apiError && (
                        <div className={textError + ' p-4 rounded bg-red-50 border border-red-200'} role="alert">
                            <p className="font-semibold">{t('contractGeneration.errors.title')}</p>
                            <p className="text-sm mt-1">{apiError}</p>
                        </div>
                    )}

                    <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                        <p className="text-sm text-blue-900 font-medium mb-3">
                            {t('contractGeneration.confirmation.message')}
                        </p>
                        <div className="space-y-2 text-sm">
                            <p>
                                <span className="font-semibold text-gray-700">{t('contractGeneration.confirmation.student')}:</span>
                                <br />
                                <span className="text-gray-600">{candidature?.student?.firstName} {candidature?.student?.lastName}</span>
                            </p>
                            <p>
                                <span className="font-semibold text-gray-700">{t('contractGeneration.confirmation.employer')}:</span>
                                <br />
                                <span className="text-gray-600">{candidature?.jobOffer?.companyName}</span>
                            </p>
                            <p>
                                <span className="font-semibold text-gray-700">{t('contractGeneration.confirmation.position')}:</span>
                                <br />
                                <span className="text-gray-600">{candidature?.jobOffer?.title}</span>
                            </p>
                        </div>
                    </div>
                </div>

                {/* Footer */}
                <div className="bg-white border-t border-brand/20 px-6 py-4 flex items-center justify-end gap-3">
                    <button
                        onClick={onClose}
                        disabled={isLoading}
                        className="px-4 py-2 rounded border border-gray-300 text-gray-700 hover:bg-gray-50 disabled:opacity-50"
                    >
                        {t('contractGeneration.contractModal.cancel')}
                    </button>
                    <button
                        onClick={handleSubmit}
                        disabled={isLoading}
                        className={btnPrimary + ' flex items-center gap-2 disabled:opacity-50'}
                    >
                        {isLoading && (
                            <svg className="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                        )}
                        {t('contractGeneration.contractModal.generate')}
                    </button>
                </div>
            </div>
        </div>,
        document.body
    );
};

export default ContractGenerationModal;
