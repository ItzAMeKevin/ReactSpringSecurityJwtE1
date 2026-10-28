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
    const blobUrlRef = useRef(null);

    useEffect(() => {
        return () => {
            if (blobUrlRef.current) URL.revokeObjectURL(blobUrlRef.current);
        };
    }, []);

    const handleStatusChange = async (newStatus) => {
        setUpdating(true);
        setStatusError(null);
        try {
            const res = await fetcher(`/employer/${offerId}/candidates/${candidate.cvId}/status`, {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({status: newStatus}),
            });
            if (!res.ok) throw new Error();
            onStatusChange(candidate.cvId, newStatus);
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
                {candidate.status === "PENDING" && (
                    <>
                        <button
                            type="button"
                            className={btnAccept}
                            disabled={updating}
                            onClick={() => handleStatusChange("ACCEPTED")}
                        >
                            {t("employeurCandidats.accepter")}
                        </button>
                        <button
                            type="button"
                            className={btnRefuse}
                            disabled={updating}
                            onClick={() => handleStatusChange("DECLINED")}
                        >
                            {t("employeurCandidats.refuser")}
                        </button>
                    </>
                )}
            </div>

            {cvError && <p className={`text-sm ${textError}`}>{cvError}</p>}
            {statusError && <p className={`text-sm ${textError}`}>{statusError}</p>}
        </li>
    );
};

export default CandidateListItem;
