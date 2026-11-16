import { useTranslation } from "react-i18next";

const InterviewNotification = ({ notification, onClose }) => {
    const { t, i18n } = useTranslation();
    const { details: interviewDetails } = notification;

    if (!interviewDetails) {
        return (
            <div className="p-4 bg-blue-50 rounded-lg">
                <p className="text-sm font-medium text-black">
                    {t("notifications.types.INTERVIEW_SCHEDULED.title")}
                </p>
                <p className="text-sm text-gray-600 mt-1">
                    {t("notifications.types.INTERVIEW_SCHEDULED.message")}
                </p>
            </div>
        );
    }

    const { jobApplication, type, videoLink, adresse, slots } = interviewDetails;
    const { jobOffer } = jobApplication;
    const firstSlot = slots?.[0];
    const slotDate = firstSlot ? new Date(firstSlot.date) : null;

    const getModalityText = () => {
        if (type === "ONLINE") {
            return `${t("common.online")} - ${videoLink ? "Zoom/Teams" : ""}`.trim();
        }
        return `${t("common.inPerson")} - ${adresse?.city || ""}`.trim();
    };

    return (
        <div className="p-4 bg-blue-50 rounded-lg border border-blue-200 space-y-3">
            {/* Titre */}
            <div>
                <p className="text-sm font-bold text-black">
                    {t("notifications.types.INTERVIEW_SCHEDULED.title")}
                </p>
                <p className="text-xs text-gray-600 mt-1">
                    {jobOffer?.employer?.firstName} {jobOffer?.employer?.lastName} •{" "}
                    {jobOffer?.title}
                </p>
            </div>

            {/* Détails de l'entrevue */}
            <div className="bg-white rounded p-3 space-y-2">
                {slotDate && (
                    <div className="text-sm">
                        <span className="font-medium text-black">
                            {t("interview.date")}:
                        </span>
                        <span className="text-gray-700 ml-2">
                            {slotDate.toLocaleDateString(i18n.language)}
                        </span>
                    </div>
                )}

                {firstSlot && (
                    <div className="text-sm">
                        <span className="font-medium text-black">
                            {t("interview.time")}:
                        </span>
                        <span className="text-gray-700 ml-2">
                            {firstSlot.startTime} - {firstSlot.endTime}
                        </span>
                    </div>
                )}

                <div className="text-sm">
                    <span className="font-medium text-black">
                        {t("interview.type")}:
                    </span>
                    <span className="text-gray-700 ml-2">{getModalityText()}</span>
                </div>

                {type === "ONLINE" && videoLink && (
                    <div className="text-sm">
                        <a
                            href={videoLink}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-blue-600 hover:text-blue-800 underline"
                        >
                            {t("interview.joinLink")}
                        </a>
                    </div>
                )}
            </div>

            <div className="flex gap-2">
                <button
                    onClick={onClose}
                    className="flex-1 px-3 py-2 bg-green-500 hover:bg-green-600 text-white text-sm font-medium rounded transition-colors"
                >
                    {t("interview.accept")}
                </button>
                <button
                    onClick={onClose}
                    className="flex-1 px-3 py-2 bg-red-500 hover:bg-red-600 text-white text-sm font-medium rounded transition-colors"
                >
                    {t("interview.decline")}
                </button>
            </div>
        </div>
    );
};

export default InterviewNotification;
