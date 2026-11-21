import { useTranslation } from "react-i18next";
import { cardItem, statusBadge, textMuted } from "@/styles/classes.js";

const formatSlotDate = (date, language) =>
    new Date(`${date}T00:00:00`).toLocaleDateString(language, { weekday: "long", day: "numeric", month: "long", year: "numeric" });

const formatTime = (time) => time?.slice(0, 5);

const formatAddress = (address) =>
    address ? `${address.numeroCivic} ${address.rue}, ${address.ville}, ${address.codePostal}, ${address.pays}` : null;

const InterviewCard = ({ interview }) => {
    const { t, i18n } = useTranslation();
    const isOnline = interview.type === "ONLINE";

    return (
        <div className={cardItem}>
            <div className="flex flex-wrap items-start justify-between gap-2">
                <div className="min-w-0">
                    <p className="font-bold text-main">{interview.studentFirstName} {interview.studentLastName}</p>
                    <p className={`text-sm ${textMuted} break-all`}>{interview.studentEmail}</p>
                </div>
                <span className={`${statusBadge} ${isOnline ? "bg-blue-100 text-blue-800" : "bg-purple-100 text-purple-800"}`}>
                    {t(isOnline ? "scheduleInterview.form.typeOnline" : "scheduleInterview.form.typeInPerson")}
                </span>
            </div>
            <p className="mt-2 text-sm">
                <span className="font-semibold">{t("employerInterviews.offer")} :</span> {interview.offerTitle}
            </p>
            <p className="mt-1 break-all text-sm">
                <span className="font-semibold">{t(isOnline ? "scheduleInterview.form.videoLink" : "scheduleInterview.form.address")} :</span>{" "}
                {isOnline ? (
                    <a href={interview.videoLink} target="_blank" rel="noreferrer" className="text-brand underline">{interview.videoLink}</a>
                ) : (
                    formatAddress(interview.address)
                )}
            </p>
            {interview.notes && <p className={`mt-1 whitespace-pre-line text-sm ${textMuted}`}>{interview.notes}</p>}
            <p className="mt-3 text-sm font-semibold">{t("scheduleInterview.slots.title")}</p>
            <ul className="mt-1 flex flex-col gap-1 text-sm">
                {interview.slots.map((slot) => (
                    <li key={`${slot.date}-${slot.startTime}`} className="rounded bg-mint-bg px-2 py-1">
                        {formatSlotDate(slot.date, i18n.language)} · {formatTime(slot.startTime)} – {formatTime(slot.endTime)}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default InterviewCard;
