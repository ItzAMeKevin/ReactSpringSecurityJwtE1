import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {fetchBlobUrl} from "../../../utils/downloadFile.js";
import CvModal from "../../CvModal.jsx";
import fetcher from "../../../utils/fetcher.js";
import ScheduleInterviewModal from "./ScheduleInterviewModal.jsx";
import { getApiErrorKey } from "../../../utils/apiError.js";
import {
    cardItem,
    statusBadge,
    statusBadgeColor,
    btnAccept,
    btnRefuse,
    btnSmPrimary,
    textError,
    textMuted,
    inputField,
} from "../../../styles/classes.js";

const CandidateListItem = ({candidate, offerId, onStatusChange, onHire, remainingPositions}) => {
    const {t} = useTranslation();
    const [statusError, setStatusError] = useState(null);
    const [cvError, setCvError] = useState(null);
    const [updating, setUpdating] = useState(false);
    const [pendingStatus, setPendingStatus] = useState(null);
    const [message, setMessage] = useState("");
    const [isCvOpen, setIsCvOpen] = useState(false);
    const [cvUrl, setCvUrl] = useState(null);
    const [isScheduleModalOpen, setIsScheduleModalOpen] = useState(false);
    const blobUrlRef = useRef(null);
    const [hireError, setHireError] = useState(null);
    const [hiring, setHiring] = useState(false);
    const [isScheduleModalOpen, setIsScheduleModalOpen] = useState(false);

    useEffect(() => {
        return () => {
            if (cvUrl) URL.revokeObjectURL(cvUrl);
        };
    }, [cvUrl]);

    const openConfirm = (status) => {
        setStatusError(null);
        setMessage("");
        setPendingStatus(status);
    };

    const cancelConfirm = () => {
        setPendingStatus(null);
        setMessage("");
    };

    const handleConfirm = async () => {
        setUpdating(true);
        setStatusError(null);
        try {
            const res = await fetcher(`/employer/${offerId}/candidates/${candidate.cvId}/status`, {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({status: pendingStatus, message: message.trim() || null}),
            });
            if (!res.ok) throw new Error(await getApiErrorKey(res));
            onStatusChange(candidate.cvId, pendingStatus);
            cancelConfirm();
        } catch (error) {
            setStatusError(error.message?.startsWith("errors.")
                ? t(error.message)
                : t("employeurCandidats.erreurStatut"));
        } finally {
            setUpdating(false);
        }
    };

    const handleHire = async () => {
        setHiring(true);
        setHireError(null);
        try {
            const res = await fetcher(`/employer/${offerId}/candidates/${candidate.cvId}/hire`, {
                method: "PUT",
            });
            if (res.status === 409) {
                setHireError(t("employeurCandidats.hireConflict"));
                return;
            }
            if (!res.ok) throw new Error();
            onStatusChange(candidate.cvId, "HIRED");
            onHire?.();
        } catch {
            setHireError(t("employeurCandidats.hireError"));
        } finally {
            setHiring(false);
        }
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
                    <p className="font-bold text-main">{candidate.firstName} {candidate.lastName}</p>
                    <p className={`text-sm ${textMuted}`}>{candidate.email}</p>
                    <p className={`text-sm ${textMuted}`}>
                        {new Date(candidate.appliedAt).toLocaleDateString()}
                    </p>
                </div>
                <span className={`${statusBadge} ${statusBadgeColor(candidate.status)}`}>
                      {t(`employeurCandidats.statuts.${candidate.status}`, candidate.status)}
                  </span>
            </div>

            <div className="flex flex-wrap gap-2 mt-1">
                <button
                    type="button"
                    className={btnSmPrimary}
                    onClick={handleOpenCv}
                    aria-haspopup="dialog"
                    aria-label={`${t("cvViewer.show")} — ${candidate.firstName} ${candidate.lastName}`}
                >
                    {t("cvViewer.show")}
                </button>
                {candidate.status === "ACCEPTED" && !pendingStatus && (
                    <button
                        type="button"
                        className={btnSmPrimary}
                        onClick={() => setIsScheduleModalOpen(true)}
                        aria-label={`${t("employeurCandidats.scheduleInterview")} — ${candidate.firstName} ${candidate.lastName}`}
                    >
                        {t("employeurCandidats.scheduleInterview")}
                    </button>
                )}
                {candidate.status === "PENDING" && !pendingStatus && (
                    <>
                        <button type="button" className={btnAccept} onClick={() => openConfirm("ACCEPTED")}>
                            {t("employeurCandidats.accepter")}
                        </button>
                        <button type="button" className={btnRefuse} onClick={() => openConfirm("DECLINED")}>
                            {t("employeurCandidats.refuser")}
                        </button>
                    </>
                )}
                {candidate.status === "ACCEPTED" && (
                    <button
                        type="button"
                        className={btnSmPrimary}
                        onClick={handleHire}
                        disabled={hiring || (remainingPositions != null && remainingPositions <= 0)}
                    >
                        {hiring ? "…" : t("employeurCandidats.embaucher")}
                    </button>
                )}
            </div>

            {isCvOpen && (
                <CvModal
                    title={`${candidate.firstName} ${candidate.lastName}`}
                    file={cvUrl}
                    error={cvError}
                    loadingMessage={t("cvViewer.loading")}
                    onClose={() => setIsCvOpen(false)}
                />
            )}

            {pendingStatus && (
                <div className="mt-2 flex flex-col gap-2 border-t pt-3" role="group"
                     aria-label={t("employeurCandidats.confirmation.titre")}>
                    <p className="text-sm font-semibold text-main">
                        {t(pendingStatus === "ACCEPTED"
                                ? "employeurCandidats.confirmation.accepter"
                                : "employeurCandidats.confirmation.refuser",
                            {nom: `${candidate.firstName} ${candidate.lastName}`})}
                    </p>
                    <label className={`text-sm ${textMuted}`} htmlFor={`msg-${candidate.cvId}`}>
                        {t("employeurCandidats.confirmation.motifLabel")}
                    </label>
                    <textarea
                        id={`msg-${candidate.cvId}`}
                        className={inputField}
                        rows={3}
                        maxLength={500}
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        disabled={updating}
                    />
                    <div className="flex gap-2">
                        <button type="button"
                                className={pendingStatus === "ACCEPTED" ? btnAccept : btnRefuse}
                                disabled={updating}
                                onClick={handleConfirm}>
                            {t("employeurCandidats.confirmation.confirmer")}
                        </button>
                        <button type="button" className={btnSmPrimary} disabled={updating} onClick={cancelConfirm}>
                            {t("employeurCandidats.confirmation.annuler")}
                        </button>
                    </div>
                </div>
            )}

            {statusError && <p className={`text-sm ${textError}`}>{statusError}</p>}

            <ScheduleInterviewModal
                isOpen={isScheduleModalOpen}
                onClose={() => setIsScheduleModalOpen(false)}
                candidate={candidate}
                candidatureId={candidate.candidatureId}
                onSuccess={() => {
                    setIsScheduleModalOpen(false);
                }}
            />
            {hireError && <p className={`text-sm ${textError}`}>{hireError}</p>}
        </li>
    );
};

export default CandidateListItem;
