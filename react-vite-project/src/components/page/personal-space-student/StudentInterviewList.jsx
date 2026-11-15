import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import fetcher from '../../../utils/fetcher.js';
import { textError, textMuted } from '../../../styles/classes.js';
import StudentInterviewListItem from './StudentInterviewListItem.jsx';

const StudentInterviewList = () => {
    const { t } = useTranslation();
    const [interviews, setInterviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            setLoading(true);
            setError(null);
            try {
                const res = await fetcher('/etudiant/interviews');
                if (!res.ok) throw new Error(`${res.status}`);
                const data = await res.json();
                if (!cancelled) setInterviews(Array.isArray(data) ? data : []);
            } catch (e) {
                console.error('Erreur chargement convocations:', e);
                if (!cancelled) setError(t('convocations.error'));
            } finally {
                if (!cancelled) setLoading(false);
            }
        };

        load();
        return () => { cancelled = true; };
    }, [t]);

    const handleRespond = (updatedInterview) => {
        setInterviews(prev =>
            prev.map(i => i.id === updatedInterview.id ? updatedInterview : i)
        );
    };

    if (loading) return <p className={`${textMuted} text-sm`}>{t('convocations.loading')}</p>;
    if (error) return <p className={textError}>{error}</p>;
    if (interviews.length === 0) return <p className={`${textMuted} text-sm`}>{t('convocations.empty')}</p>;

    return (
        <div className="flex flex-col gap-3 w-full">
            {interviews.map(interview => (
                <StudentInterviewListItem
                    key={interview.id}
                    interview={interview}
                    onRespond={handleRespond}
                />
            ))}
        </div>
    );
};

export default StudentInterviewList;
