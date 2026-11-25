import StudentInterviewListItem from './StudentInterviewListItem.jsx';
import useHighlightWhenNotified from '@/hooks/useHighlightWhenNotified.js';

const StudentInterviewList = ({ interviews }) => {
    const { highlightProps } = useHighlightWhenNotified();

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
                    {...highlightProps('interview', interview.id)}
                    onRespond={handleRespond}
                />
            ))}
        </div>
    );
};

export default StudentInterviewList;
