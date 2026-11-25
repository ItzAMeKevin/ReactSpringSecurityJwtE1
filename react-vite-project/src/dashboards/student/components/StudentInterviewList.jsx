import StudentInterviewListItem from './StudentInterviewListItem.jsx';
import useHighlightWhenNotified from '@/hooks/useHighlightWhenNotified.js';

const StudentInterviewList = ({ interviews }) => {
    const { isHighlighted, clearHighlight } = useHighlightWhenNotified();

    const handleRespond = (updatedInterview) => {
        interviews.setItems(prev =>
            prev.map(i => i.id === updatedInterview.id ? updatedInterview : i)
        );
    };

    return (
        <div className="flex flex-col gap-3 w-full">
            {interviews.items.map(interview => (
                <StudentInterviewListItem
                    key={interview.id}
                    interview={interview}
                    isHighlighted={isHighlighted('interview', interview.id)}
                    onClearHighlight={clearHighlight}
                    onRespond={handleRespond}
                />
            ))}
        </div>
    );
};

export default StudentInterviewList;
