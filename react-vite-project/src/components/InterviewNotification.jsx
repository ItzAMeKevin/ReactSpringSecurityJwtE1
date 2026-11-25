import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { btnSmPrimary } from "@/styles/classes.js";

const InterviewNotification = ({ onClose }) => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const handleGoToConvocations = () => {
        onClose();
        navigate("/etudiant/convocations");
    };

    return (
        <div className="p-4 bg-blue-50 rounded-lg">
            <p className="text-sm font-medium text-black">
                {t("notifications.types.INTERVIEW_SCHEDULED.title")}
            </p>
            <p className="text-sm text-gray-600 mt-1">
                {t("notifications.types.INTERVIEW_SCHEDULED.message")}
            </p>
            <button onClick={handleGoToConvocations} className={`mt-2 w-full ${btnSmPrimary}`}>
                {t("convocations.title")}
            </button>
        </div>
    );
};

export default InterviewNotification;
