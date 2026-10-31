import {useEffect, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {fetchBlobUrl} from "../../../utils/downloadFile.js";
import fetcher from "../../../utils/fetcher.js";
import {
    cardItem,
    statusBadge,
    statusBadgeColor,
    btnAccept,
    btnRefuse,
    btnSmPrimary,
    textError,
    textMuted,
} from "../../../styles/classes.js";

const CandidateListItem = ({candidate, offerId, onStatusChange}) => {
    const {t} = useTranslation();
    const [statusError, setStatusError] = useState(null);
    const [cvError, setCvError] = useState(null);
    const [updating, setUpdating] = useState(false);
    const [pendingStatus, setPendingStatus] = useState(null);
    const [message, setMessage] = useState("");
    const blobUrlRef = useRef(null);

    useEffect(() => {
        return () => {
            if (blobUrlRef.current) URL.revokeObjectURL(blobUrlRef.current);
        };
    }, []);

    const openConfirm = (status) => {
        setStatusError(null);
        setMessage("");
        setPendingStatus(status);
    };

    const closeConfirm = () => {
        setPendingStatus(null);
        setMessage("");
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
            if (!res.ok) throw new Error();
            onStatusChange(candidate.cvId, pendingStatus);
            cancelConfirm();
        } catch {
            setStatusError(t("employeurCandidats.erreurStatut"));
        } finally {
            setUpdating(false);
        }
    };

    const handleViewCv = async () => {
        setCvError(null);
        const previewWindow = window.open("", "_blank");
        if (!previewWindow) {
            setCvError(t("employeurCandidats.popupBloque"));
            return;
        }

        try {
            const url = await fetchBlobUrl(`/employer/candidates/${candidate.cvId}/cv`);
            if (blobUrlRef.current) URL.revokeObjectURL(blobUrlRef.current);
            blobUrlRef.current = url;
            previewWindow.location.href = url;
        } catch {
            previewWindow.close();
            setCvError(t("employeurCandidats.erreurCv"));
        }
    };

    return (
        <li className={`${cardItem} p-4 flex flex-col gap-2`}>
            <div className="flex items-start justify-between gap-2">
                <div>
                    <p className="font-bold text-black">{candidate.firstName} {candidate.lastName}</p>
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
                    onClick={handleViewCv}
                    aria-label={`${t("employeurCandidats.voirCv")} — ${candidate.firstName} ${candidate.lastName}`}
                >
                    {t("employeurCandidats.voirCv")}
                </button>
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
            </div>

            {pendingStatus && (
                <div className="mt-2 flex flex-col gap-2 border-t pt-3" role="group"
                     aria-label={t("employeurCandidats.confirmation.titre")}>
                    <p className="text-sm font-semibold text-black">
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
                        className="border rounded p-2 text-sm"
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

            {cvError && <p className={`text-sm ${textError}`}>{cvError}</p>}
            {statusError && <p className={`text-sm ${textError}`}>{statusError}</p>}
        </li>
    );
};

export default CandidateListItem;
