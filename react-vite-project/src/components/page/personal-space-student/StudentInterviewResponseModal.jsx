import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import fetcher from '../../../utils/fetcher.js';
import { btnAccept, btnOutline, btnRefuse, inputField, textError, textMuted } from '../../../styles/classes.js';

const StudentInterviewResponseModal = ({ isOpen, onClose, interview, onRespond }) => {
    const { t, i18n } = useTranslation();
    const [declining, setDeclining] = useState(false);
    const [declineMessage, setDeclineMessage] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    if (!isOpen || !interview) return null;

    const handleClose = () => {
        if (loading) return;
        setDeclining(false);
        setDeclineMessage('');
        setError(null);
        onClose();
    };

    const respond = async (status) => {
        setError(null);
        setLoading(true);
        try {
            const res = await fetcher(`/etudiant/interviews/${interview.id}/respond`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    status,
                    message: status === 'DECLINED' ? declineMessage || null : null
                })
            });
            if (res.status === 409) {
                const body = await res.json().catch(() => ({}));
                const key = body.message === 'expired'
                    ? 'convocations.errors.expired'
                    : 'convocations.errors.alreadyResponded';
                throw new Error(t(key));
            }
            if (!res.ok) throw new Error(t('convocations.errors.generic'));
            const updated = await res.json();
            onRespond(updated);
        } catch (e) {
            setError(e.message || t('convocations.errors.generic'));
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (date) =>
        new Date(date + 'T00:00:00').toLocaleDateString(i18n.language, {
            weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
        });

    return (
        <div
            className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
            onClick={() => !loading && handleClose()}
        >
            <div
                className="relative bg-white rounded-lg shadow-lg max-w-lg w-full mx-4 max-h-[90vh] overflow-y-auto"
                onClick={(e) => e.stopPropagation()}
            >
                {/* Header */}
                <div className="sticky top-0 bg-white border-b border-brand/20 px-6 py-4 flex items-center justify-between">
                    <h2 className="text-xl font-bold text-main">{t('convocations.modal.title')}</h2>
                    <button
                        onClick={handleClose}
                        disabled={loading}
                        className="text-main/50 hover:text-main text-2xl font-bold disabled:opacity-50"
                    >
                        ✕
                    </button>
                </div>

                {/* Body */}
                <div className="px-6 py-6 space-y-4">
                    {error && (
                        <p className={`${textError} p-3 rounded bg-red-50 border border-red-200`}>{error}</p>
                    )}

                    {/* Info */}
                    <div className="bg-brand/5 border border-brand/20 rounded-lg p-4 space-y-2">
                        <Row label={t('convocations.modal.company')} value={interview.companyName} />
                        <Row label={t('convocations.modal.offer')} value={interview.offerTitle} />
                        <Row label={t('convocations.modal.type')} value={t(`convocations.type.${interview.type}`)} />
                        {interview.videoLink && (
                            <Row label={t('convocations.modal.link')}>
                                <a href={interview.videoLink} target="_blank" rel="noreferrer"
                                    className="text-blue-600 underline text-sm break-all">
                                    {interview.videoLink}
                                </a>
                            </Row>
                        )}
                        {interview.address && (
                            <Row label={t('convocations.modal.address')}
                                value={[interview.address.numeroCivic, interview.address.rue, interview.address.ville, interview.address.codePostal].filter(Boolean).join(', ')} />
                        )}
                        {interview.notes && (
                            <Row label={t('convocations.modal.notes')} value={interview.notes} />
                        )}
                    </div>

                    {/* Slots */}
                    {interview.slots?.length > 0 && (
                        <div>
                            <p className="text-sm font-semibold text-main mb-2">{t('convocations.modal.slots')}</p>
                            <ul className="space-y-1">
                                {interview.slots.map((slot, i) => (
                                    <li key={i} className={`${textMuted} text-sm`}>
                                        {formatDate(slot.date)} • {slot.startTime} – {slot.endTime}
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}

                    {/* Decline textarea */}
                    {declining && (
                        <div>
                            <label className="block text-sm font-semibold text-main mb-2">
                                {t('convocations.declineMessage')}
                            </label>
                            <textarea
                                value={declineMessage}
                                onChange={(e) => setDeclineMessage(e.target.value)}
                                placeholder={t('convocations.declineMessagePlaceholder')}
                                rows="3"
                                className={inputField}
                            />
                        </div>
                    )}
                </div>

                {/* Footer */}
                <div className="sticky bottom-0 bg-white border-t border-brand/20 px-6 py-4 flex items-center justify-end gap-3">
                    <button onClick={handleClose} disabled={loading} className={`${btnOutline} disabled:opacity-50`}>
                        {t('convocations.cancel')}
                    </button>

                    {!declining ? (
                        <>
                            <button
                                onClick={() => setDeclining(true)}
                                disabled={loading}
                                className={`${btnRefuse} disabled:opacity-50`}
                            >
                                {t('convocations.decline')}
                            </button>
                            <button
                                onClick={() => respond('ACCEPTED')}
                                disabled={loading}
                                className={`${btnAccept} disabled:opacity-50`}
                            >
                                {loading ? '...' : t('convocations.accept')}
                            </button>
                        </>
                    ) : (
                        <button
                            onClick={() => respond('DECLINED')}
                            disabled={loading}
                            className={`${btnRefuse} disabled:opacity-50`}
                        >
                            {loading ? '...' : t('convocations.confirm')}
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

const Row = ({ label, value, children }) => (
    <div className="flex gap-2 text-sm">
        <span className="font-semibold text-main shrink-0">{label}:</span>
        {children ?? <span className="text-main/70">{value}</span>}
    </div>
);

export default StudentInterviewResponseModal;
