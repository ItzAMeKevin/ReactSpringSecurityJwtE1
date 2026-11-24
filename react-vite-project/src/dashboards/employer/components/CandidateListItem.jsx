import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { fetchBlobUrl } from "@/utils/downloadFile.js";
import sendPut from "@/utils/sendPut.js";
import { translateErrorKey } from "@/utils/apiError.js";
import CvModal from "@/components/cv/CvModal.jsx";
import ConfirmPanel from "@/components/ui/ConfirmPanel.jsx";
import ScheduleInterviewModal from "@/dashboards/employer/components/ScheduleInterviewModal.jsx";
import {
    cardItem,
    statusBadge,
    statusBadgeColor,
    btnAccept,
    btnRefuse,
    btnSmPrimary,
    textError,
    textMuted,
    successCard,
    dangerCard,
} from "@/styles/classes.js";

const OPEN_STATUSES = ["PENDING", "PENDING_INTERVIEW", "INTERVIEW_SCHEDULED", "INTERVIEW_COMPLETED"];

const ACTIONS = {
    hire: {
        endpoint: "hire",
        nextStatus: "HIRE_PENDING",
        labelKey: "employeurCandidats.embaucher",
        confirmKey: "employeurCandidats.confirmation.embaucher",
        button: btnAccept,
        panel: successCard,
    },
    reject: {
        endpoint: "reject",
        nextStatus: "DECLINED",
        labelKey: "employeurCandidats.nePasRetenir",
        confirmKey: "employeurCandidats.confirmation.nePasRetenir",
        button: btnRefuse,
        panel: dangerCard,
    },
};

const CandidateListItem = ({ candidate, offerId, onStatusChange, onHire, onInterviewScheduled, remainingPositions }) => {
    const { t } = useTranslation();
    const [action, setAction] = useState(null);
    const [isProcessing, setIsProcessing] = useState(false);
    const [errorKey, setErrorKey] = useState(null);
    const [isCvOpen, setIsCvOpen] = useState(false);
    const [cvUrl, setCvUrl] = useState(null);
    const [cvError, setCvError] = useState(null);
    const [isScheduleModalOpen, setIsScheduleModalOpen] = useState(false);

    const fullName = `${candidate.firstName} ${candidate.lastName}`;
    const isOpen = OPEN_STATUSES.includes(candidate.status);
    const hasNoPositionLeft = remainingPositions != null && remainingPositions <= 0;
    const current = action && ACTIONS[action];

    useEffect(() => {
        return () => {
            if (cvUrl) URL.revokeObjectURL(cvUrl);
        };
    }, [cvUrl]);

    const openAction = (kind) => {
        setErrorKey(null);
        setAction(kind);
    };

    const handleConfirm = async () => {
        setIsProcessing(true);
        setErrorKey(null);
        try {
            await sendPut(`/employer/${offerId}/candidates/${candidate.cvId}/${current.endpoint}`);
            onStatusChange(candidate.cvId, current.nextStatus);
            if (action === "hire") onHire?.();
            setAction(null);
        } catch (error) {
            setErrorKey(error.message);
        } finally {
            setIsProcessing(false);
        }
    };

    const handleInterviewScheduled = () => {
        setIsScheduleModalOpen(false);
        onStatusChange(candidate.cvId, "INTERVIEW_SCHEDULED");
        onInterviewScheduled?.();
    };

    const handleOpenCv = async () => {
        setIsCvOpen(true);
        if (cvUrl) return;
        setCvError(null);

        try {
            const url = await fetchBlobUrl(`/employer/candidates/${candidate.cvId}/cv`);
            setCvUrl(url);
        } catch {
            setCvError(t("employeurCandidats.erreurCv"));
        }
    };

    return (
        <li className={`${cardItem} p-4 flex flex-col gap-2`}>
            <div className="flex items-start justify-between gap-2">
                <div>
                    <p className="font-bold text-main">{fullName}</p>
                    <p className={`text-sm ${textMuted}`}>{candidate.email}</p>
                    <p className={`text-sm ${textMuted}`}>
                        {new Date(candidate.appliedAt).toLocaleDateString()}
                    </p>
                </div>
                <span className={`${statusBadge} ${statusBadgeColor(candidate.status)}`}>
                    {t([`employeurCandidats.statuts.${candidate.status}`, `applicationStatuses.${candidate.status}`], candidate.status)}
                </span>
            </div>

            <div className="flex flex-wrap gap-2 mt-1">
                <button
                    type="button"
                    className={btnSmPrimary}
                    onClick={handleOpenCv}
                    aria-haspopup="dialog"
                    aria-label={`${t("cvViewer.show")} — ${fullName}`}
                >
                    {t("cvViewer.show")}
                </button>
                {candidate.status === "PENDING" && candidate.candidatureId && (
                    <button
                        type="button"
                        className={btnSmPrimary}
                        onClick={() => setIsScheduleModalOpen(true)}
                        disabled={action !== null}
                        aria-label={`${t("employeurCandidats.scheduleInterview")} — ${fullName}`}
                    >
                        {t("employeurCandidats.scheduleInterview")}
                    </button>
                )}
                {isOpen && (
                    <>
                        <button
                            type="button"
                            className={ACTIONS.hire.button}
                            onClick={() => openAction("hire")}
                            disabled={action !== null || hasNoPositionLeft}
                        >
                            {t(ACTIONS.hire.labelKey)}
                        </button>
                        <button
                            type="button"
                            className={ACTIONS.reject.button}
                            onClick={() => openAction("reject")}
                            disabled={action !== null}
                        >
                            {t(ACTIONS.reject.labelKey)}
                        </button>
                    </>
                )}
            </div>

            {current && (
                <ConfirmPanel
                    message={t(current.confirmKey, { nom: fullName })}
                    panelClass={current.panel}
                    confirmClass={current.button}
                    isProcessing={isProcessing}
                    onConfirm={handleConfirm}
                    onCancel={() => setAction(null)}
                />
            )}

            {errorKey && (
                <p className={`text-sm ${textError}`} role="alert">
                    {translateErrorKey(errorKey, t, "employeurCandidats.erreurStatut")}
                </p>
            )}

            {isCvOpen && (
                <CvModal
                    title={fullName}
                    file={cvUrl}
                    error={cvError}
                    loadingMessage={t("cvViewer.loading")}
                    onClose={() => setIsCvOpen(false)}
                />
            )}

            <ScheduleInterviewModal
                isOpen={isScheduleModalOpen}
                onClose={() => setIsScheduleModalOpen(false)}
                candidate={candidate}
                candidatureId={candidate.candidatureId}
                onSuccess={handleInterviewScheduled}
            />
        </li>
    );
};

export default CandidateListItem;
