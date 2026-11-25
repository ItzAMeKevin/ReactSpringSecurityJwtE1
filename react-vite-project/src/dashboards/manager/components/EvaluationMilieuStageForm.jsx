import { useState, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import fetcher from '@/utils/fetcher.js';
import { getApiErrorKey } from '@/utils/apiError.js';
import { btnPrimary, btnOutline, inputField, textError, sectionTitle } from '@/styles/classes.js';

const LIKERT_OPTIONS = ['TA', 'PA', 'PD', 'TD', 'NA'];

const CRITERIA = [
    { key: 'taches' },
    { key: 'accueil' },
    { key: 'encadrement', extra: 'hours' },
    { key: 'salaire', extra: 'salary' },
    { key: 'communication' },
    { key: 'equipement' },
    { key: 'volume' },
    { key: 'hygiene' },
    { key: 'climat' },
    { key: 'transport' },
];

const getInitialState = (internship) => ({
    nomEntreprise: internship?.companyName ?? '',
    personneContact: internship?.supervisorName ?? '',
    adresse: internship?.address ?? '',
    telephone: internship?.phone ?? '',
    ville: internship?.city ?? '',
    telecopieur: '',
    codePostal: internship?.postalCode ?? '',
    nomStagiaire: internship
        ? `${internship.studentFirstName ?? ''} ${internship.studentLastName ?? ''}`.trim()
        : '',
    dateStage: internship?.startDate ?? '',
    stageNumero: '',
    taches: '', accueil: '', encadrement: '',
    heuresMois1: '', heuresMois2: '', heuresMois3: '',
    salaire: '', salaireParHeure: '',
    communication: '', equipement: '', volume: '',
    hygiene: '', climat: '', transport: '',
    commentaires: '',
    stagePrivilegie: '',
    nbStagiaires: '',
    memeStagiaire: '',
    quartsVariables: '',
    quart1De: '', quart1A: '',
    quart2De: '', quart2A: '',
    quart3De: '', quart3A: '',
    signature: '',
    dateSignature: new Date().toISOString().split('T')[0],
});

const LikertRow = ({ criterionKey, value, onChange, children }) => {
    const { t } = useTranslation();
    return (
        <div className="border-b border-brand/10 py-3">
            <div className="flex flex-col gap-3 md:flex-row md:items-start">
                <p className="flex-1 text-sm text-main">
                    {t(`evaluationMilieu.form.evaluation.criteria.${criterionKey}`)}
                </p>
                <div className="flex shrink-0 gap-4">
                    {LIKERT_OPTIONS.map(opt => (
                        <label key={opt} className="flex cursor-pointer flex-col items-center gap-1">
                            <input
                                type="radio"
                                name={criterionKey}
                                value={opt}
                                checked={value === opt}
                                onChange={() => onChange(criterionKey, opt)}
                                className="accent-brand"
                            />
                            <span className="text-xs font-semibold text-main/60">
                                {t(`evaluationMilieu.form.evaluation.${opt}`)}
                            </span>
                        </label>
                    ))}
                </div>
            </div>
            {children}
        </div>
    );
};

const EvaluationMilieuStageForm = ({ isOpen, onClose, internship, onSuccess }) => {
    const { t } = useTranslation();
    const modalRef = useRef(null);
    const [form, setForm] = useState(() => getInitialState(internship));
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState(null);

    const setField = (key, value) => setForm(prev => ({ ...prev, [key]: value }));

    const isFormValid = () =>
        CRITERIA.every(c => form[c.key] !== '') && form.stageNumero && form.signature;

    const handleSubmit = async () => {
        setError(null);
        if (!isFormValid()) {
            setError(t('evaluationMilieu.form.errors.incomplete'));
            modalRef.current?.scrollTo({ top: 0, behavior: 'smooth' });
            return;
        }
        setSubmitting(true);
        try {
            const res = await fetcher(`/gestionnaire/milieux/${internship?.id}/evaluation`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(form),
            });
            if (!res.ok) throw new Error(await getApiErrorKey(res));
            onSuccess?.();
            handleClose();
        } catch (err) {
            setError(t(err.message?.startsWith('errors.') ? err.message : 'evaluationMilieu.form.errors.generic'));
        } finally {
            setSubmitting(false);
        }
    };

    const handleClose = () => {
        if (submitting) return;
        setError(null);
        setForm(getInitialState(internship));
        onClose();
    };

    if (!isOpen) return null;

    return (
        <div
            className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
            onClick={() => !submitting && handleClose()}
        >
            <div
                ref={modalRef}
                className="relative mx-4 max-h-[90vh] w-full max-w-4xl overflow-y-auto rounded-lg bg-white shadow-lg"
                onClick={e => e.stopPropagation()}
            >
                <div className="sticky top-0 z-10 flex items-center justify-between border-b border-brand/20 bg-white px-6 py-4">
                    <h2 className="text-xl font-bold text-main">
                        {t('evaluationMilieu.form.title')}
                    </h2>
                    <button
                        type="button"
                        onClick={handleClose}
                        disabled={submitting}
                        className="text-2xl font-bold text-main/50 hover:text-main disabled:opacity-50"
                        aria-label={t('evaluationMilieu.form.annuler')}
                    >
                        ✕
                    </button>
                </div>

                <div className="space-y-8 px-6 py-6">
                    {error && (
                        <p className={`${textError} rounded border border-red-200 bg-red-50 p-3 text-sm`} role="alert">
                            {error}
                        </p>
                    )}

                    <fieldset className="space-y-3">
                        <legend className={sectionTitle}>
                            {t('evaluationMilieu.form.entreprise.section')}
                        </legend>
                        <div className="grid grid-cols-2 gap-4">
                            {[
                                ['nomEntreprise', 'nom'],
                                ['personneContact', 'contact'],
                                ['adresse', 'adresse'],
                                ['telephone', 'telephone'],
                                ['ville', 'ville'],
                                ['telecopieur', 'telecopieur'],
                                ['codePostal', 'codePostal'],
                            ].map(([field, labelKey]) => (
                                <div key={field}>
                                    <label className="mb-1 block text-sm font-semibold text-main">
                                        {t(`evaluationMilieu.form.entreprise.${labelKey}`)}
                                    </label>
                                    <input
                                        type="text"
                                        value={form[field]}
                                        onChange={e => setField(field, e.target.value)}
                                        className={inputField}
                                    />
                                </div>
                            ))}
                        </div>
                    </fieldset>

                    <fieldset className="space-y-3">
                        <legend className={sectionTitle}>
                            {t('evaluationMilieu.form.stagiaire.section')}
                        </legend>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="mb-1 block text-sm font-semibold text-main">
                                    {t('evaluationMilieu.form.stagiaire.nom')}
                                </label>
                                <input
                                    type="text"
                                    value={form.nomStagiaire}
                                    onChange={e => setField('nomStagiaire', e.target.value)}
                                    className={inputField}
                                />
                            </div>
                            <div>
                                <label className="mb-1 block text-sm font-semibold text-main">
                                    {t('evaluationMilieu.form.stagiaire.date')}
                                </label>
                                <input
                                    type="date"
                                    value={form.dateStage}
                                    onChange={e => setField('dateStage', e.target.value)}
                                    className={inputField}
                                />
                            </div>
                        </div>
                        <div>
                            <p className="mb-2 text-sm font-semibold text-main">
                                {t('evaluationMilieu.form.stagiaire.numero')}
                            </p>
                            <div className="flex gap-6">
                                {['1', '2'].map(n => (
                                    <label key={n} className="flex cursor-pointer items-center gap-2">
                                        <input
                                            type="radio"
                                            name="stageNumero"
                                            value={n}
                                            checked={form.stageNumero === n}
                                            onChange={() => setField('stageNumero', n)}
                                            className="accent-brand"
                                        />
                                        <span className="text-sm">
                                            {t('evaluationMilieu.form.stagiaire.stageLabel', { n })}
                                        </span>
                                    </label>
                                ))}
                            </div>
                        </div>
                    </fieldset>

                    <fieldset>
                        <legend className={sectionTitle}>
                            {t('evaluationMilieu.form.evaluation.section')}
                        </legend>
                        {CRITERIA.map(c => (
                            <LikertRow key={c.key} criterionKey={c.key} value={form[c.key]} onChange={setField}>
                                {c.extra === 'hours' && (
                                    <div className="mt-2 grid grid-cols-3 gap-3">
                                        <p className="col-span-3 text-xs text-main/60">
                                            {t('evaluationMilieu.form.evaluation.criteria.heuresSemaine')}
                                        </p>
                                        {[['heuresMois1', 'mois1'], ['heuresMois2', 'mois2'], ['heuresMois3', 'mois3']].map(([field, labelKey]) => (
                                            <label key={field} className="flex flex-col gap-1">
                                                <span className="text-xs text-main/60">
                                                    {t(`evaluationMilieu.form.evaluation.criteria.${labelKey}`)}
                                                </span>
                                                <input
                                                    type="number"
                                                    min="0"
                                                    value={form[field]}
                                                    onChange={e => setField(field, e.target.value)}
                                                    className={`${inputField} text-sm`}
                                                />
                                            </label>
                                        ))}
                                    </div>
                                )}
                                {c.extra === 'salary' && (
                                    <div className="mt-2 flex items-center gap-2">
                                        <span className="text-xs text-main/60">
                                            {t('evaluationMilieu.form.evaluation.criteria.salaireParHeure')} :
                                        </span>
                                        <input
                                            type="text"
                                            value={form.salaireParHeure}
                                            onChange={e => setField('salaireParHeure', e.target.value)}
                                            placeholder="20.00"
                                            className={`${inputField} max-w-28 text-sm`}
                                        />
                                        <span className="text-xs text-main/60">/h</span>
                                    </div>
                                )}
                            </LikertRow>
                        ))}
                        <p className="mt-2 text-xs italic text-main/50">
                            {t('evaluationMilieu.form.evaluation.criteria.note')}
                        </p>
                    </fieldset>

                    <fieldset>
                        <legend className={sectionTitle}>
                            {t('evaluationMilieu.form.commentaires.section')}
                        </legend>
                        <textarea
                            value={form.commentaires}
                            onChange={e => setField('commentaires', e.target.value)}
                            rows={4}
                            placeholder={t('evaluationMilieu.form.commentaires.placeholder')}
                            className={inputField}
                        />
                    </fieldset>

                    <fieldset className="space-y-4">
                        <legend className={sectionTitle}>
                            {t('evaluationMilieu.form.observations.section')}
                        </legend>

                        <div>
                            <p className="mb-2 text-sm font-semibold text-main">
                                {t('evaluationMilieu.form.observations.aPrivilegier')}
                            </p>
                            <div className="flex gap-6">
                                {[['1', 'premier'], ['2', 'deuxieme']].map(([val, labelKey]) => (
                                    <label key={val} className="flex cursor-pointer items-center gap-2">
                                        <input
                                            type="radio"
                                            name="stagePrivilegie"
                                            value={val}
                                            checked={form.stagePrivilegie === val}
                                            onChange={() => setField('stagePrivilegie', val)}
                                            className="accent-brand"
                                        />
                                        <span className="text-sm">
                                            {t(`evaluationMilieu.form.observations.${labelKey}`)}
                                        </span>
                                    </label>
                                ))}
                            </div>
                        </div>

                        <div>
                            <p className="mb-2 text-sm font-semibold text-main">
                                {t('evaluationMilieu.form.observations.ouvertA')}
                            </p>
                            <div className="flex flex-wrap gap-4">
                                {[['1', 'unStagiaire'], ['2', 'deuxStagiaires'], ['3', 'troisStagiaires'], ['+', 'plusDeTrois']].map(([val, labelKey]) => (
                                    <label key={val} className="flex cursor-pointer items-center gap-2">
                                        <input
                                            type="radio"
                                            name="nbStagiaires"
                                            value={val}
                                            checked={form.nbStagiaires === val}
                                            onChange={() => setField('nbStagiaires', val)}
                                            className="accent-brand"
                                        />
                                        <span className="text-sm">
                                            {t(`evaluationMilieu.form.observations.${labelKey}`)}
                                        </span>
                                    </label>
                                ))}
                            </div>
                        </div>

                        {[['memeStagiaire', 'memeStagiaire'], ['quartsVariables', 'quartsVariables']].map(([field, labelKey]) => (
                            <div key={field}>
                                <p className="mb-2 text-sm font-semibold text-main">
                                    {t(`evaluationMilieu.form.observations.${labelKey}`)}
                                </p>
                                <div className="flex gap-6">
                                    {['oui', 'non'].map(val => (
                                        <label key={val} className="flex cursor-pointer items-center gap-2">
                                            <input
                                                type="radio"
                                                name={field}
                                                value={val}
                                                checked={form[field] === val}
                                                onChange={() => setField(field, val)}
                                                className="accent-brand"
                                            />
                                            <span className="text-sm">
                                                {t(`evaluationMilieu.form.observations.${val}`)}
                                            </span>
                                        </label>
                                    ))}
                                </div>
                            </div>
                        ))}

                        {form.quartsVariables === 'oui' && (
                            <div className="space-y-2">
                                {[[1, 'quart1De', 'quart1A'], [2, 'quart2De', 'quart2A'], [3, 'quart3De', 'quart3A']].map(([, deField, aField]) => (
                                    <div key={deField} className="flex items-center gap-3 text-sm">
                                        <span className="text-main/60">{t('evaluationMilieu.form.observations.de')}</span>
                                        <input
                                            type="time"
                                            value={form[deField]}
                                            onChange={e => setField(deField, e.target.value)}
                                            className={`${inputField} w-32`}
                                        />
                                        <span className="text-main/60">{t('evaluationMilieu.form.observations.a')}</span>
                                        <input
                                            type="time"
                                            value={form[aField]}
                                            onChange={e => setField(aField, e.target.value)}
                                            className={`${inputField} w-32`}
                                        />
                                    </div>
                                ))}
                            </div>
                        )}
                    </fieldset>

                    <fieldset>
                        <legend className={sectionTitle}>
                            {t('evaluationMilieu.form.signature.section')}
                        </legend>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="mb-1 block text-sm font-semibold text-main">
                                    {t('evaluationMilieu.form.signature.label')}
                                </label>
                                <input
                                    type="text"
                                    value={form.signature}
                                    onChange={e => setField('signature', e.target.value)}
                                    className={inputField}
                                />
                            </div>
                            <div>
                                <label className="mb-1 block text-sm font-semibold text-main">
                                    {t('evaluationMilieu.form.signature.date')}
                                </label>
                                <input
                                    type="date"
                                    value={form.dateSignature}
                                    onChange={e => setField('dateSignature', e.target.value)}
                                    className={inputField}
                                />
                            </div>
                        </div>
                    </fieldset>
                </div>

                <div className="sticky bottom-0 flex items-center justify-end gap-3 border-t border-brand/20 bg-white px-6 py-4">
                    <button
                        type="button"
                        onClick={handleClose}
                        disabled={submitting}
                        className={`${btnOutline} disabled:opacity-50`}
                    >
                        {t('evaluationMilieu.form.annuler')}
                    </button>
                    <button
                        type="button"
                        onClick={handleSubmit}
                        disabled={submitting}
                        className={`${btnPrimary} disabled:opacity-50`}
                    >
                        {submitting
                            ? t('evaluationMilieu.form.submitting')
                            : t('evaluationMilieu.form.soumettre')}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default EvaluationMilieuStageForm;
