import { useTranslation } from "react-i18next";
import { useState, useRef, useEffect } from "react";
import CvPdfViewer from "../../CvPdfViewer.jsx";
import scrollIntoViewOnMount from "../../../utils/scrollIntoViewOnMount.js";
import fetcher from "../../../utils/fetcher.js";
import { fetchBlobUrl } from "../../../utils/downloadFile.js";
import {
    cardItem,
    cardHighlighted,
    btnSmPrimary,
    btnRefuse,
    statusBadge,
    statusBadgeColor,
    textError,
    textMuted
} from "../../../styles/classes.js";

const CvListItem = ({
                        cv,
                        isOpen,
                        onToggle,
                        isHighlighted,
                        onClearHighlight,
                        onReplace,
                    }) => {
    const { t } = useTranslation();
    const isDeclined = cv.status === "DECLINED";
    const [previewUrl, setPreviewUrl] = useState(null);
    const [previewError, setPreviewError] = useState(false);
    const [feedback, setFeedback] = useState(null);
    const [feedbackError, setFeedbackError] = useState(false);
    const replaceInputRef = useRef(null);
    const [isReplacing, setIsReplacing] = useState(false);
    const [replaceError, setReplaceError] = useState(null);

    useEffect(() => {
        if (!isDeclined) return;
        let cancelled = false;
        setFeedbackError(false);
        fetcher(`/student-cv/${cv.id}/review`)
            .then(async (response) => {
                if (!response.ok) throw new Error(`Erreur HTTP ${response.status}`);
                const text = await response.text();
                if (!cancelled) setFeedback(text);
            })
            .catch((error) => {
                console.error("Erreur lors du chargement du feedback :", error);
                if (!cancelled) setFeedbackError(true);
            });
        return () => {
            cancelled = true;
        };
    }, [isDeclined, cv.id]);

    useEffect(() => {
        if (!isOpen || cv.file || previewUrl) return;
        let cancelled = false;
        setPreviewError(false);
        fetchBlobUrl(`/student-cv/${cv.id}/content`)
            .then((url) => {
                if (cancelled) {
                    URL.revokeObjectURL(url);
                } else {
                    setPreviewUrl(url);
                }
            })
            .catch((error) => {
                console.error("Erreur lors du chargement de l'aperçu :", error);
                if (!cancelled) setPreviewError(true);
            });
        return () => {
            cancelled = true;
        };
    }, [isOpen, cv.id, cv.file, previewUrl]);

    useEffect(() => {
        return () => {
            if (previewUrl) URL.revokeObjectURL(previewUrl);
        };
    }, [previewUrl]);

    const renderPreview = () => {
        if (cv.file) return <CvPdfViewer file={cv.file} />;
        if (previewUrl) return <CvPdfViewer file={previewUrl} />;
        return (
            <p className={`mt-3 text-sm ${previewError ? textError : textMuted}`}>
                {previewError ? t("televerser.apercuIndisponible") : t("televerser.chargementApercu")}
            </p>
        );
    };

    // The new version is a regular upload; the page hides this one if the file name is the same
    const handleReplaceFile = async (event) => {
        const file = event.target.files?.[0];
        event.target.value = "";
        if (!file) return;
        if (file.type !== "application/pdf") {
            setReplaceError("televerser.erreurTypeFichier");
            return;
        }
        setReplaceError(null);
        setIsReplacing(true);
        try {
            if (!(await onReplace(file))) setReplaceError("televerser.envoieErreur");
        } catch (error) {
            console.error("Erreur lors du remplacement du CV :", error);
            setReplaceError("televerser.envoieErreur");
        } finally {
            setIsReplacing(false);
        }
    };

    const renderFeedback = () => {
        if (feedbackError) {
            return <p className={`text-sm ${textError}`}>{t("televerser.erreurFeedback")}</p>;
        }
        if (feedback === null) {
            return <p className={`text-sm ${textMuted}`}>{t("televerser.chargementFeedback")}</p>;
        }
        return (
            <>
                <p className="text-sm font-semibold text-red-800">{t("televerser.feedbackTitre")}</p>
                <p className="text-sm text-black mt-1 whitespace-pre-line break-words">{feedback}</p>
            </>
        );
    };

    return (
        <div
            ref={isHighlighted ? scrollIntoViewOnMount : undefined}
            onClick={isHighlighted ? onClearHighlight : undefined}
            className={`${cardItem} border-2 ${isHighlighted ? cardHighlighted : ''}`}
        >
            <div className="flex items-center justify-between gap-2">
                <div className="min-w-0 flex items-center gap-2">
                    <span className="text-main font-medium truncate">{cv.fileName}</span>
                    {cv.status && (
                        <span className={`${statusBadge} ${statusBadgeColor(cv.status)}`}>
                            {t(`televerser.statuts.${cv.status}`, cv.status)}
                        </span>
                    )}
                </div>
                <div className="flex shrink-0 gap-2">
                    {isDeclined && (
                        <>
                            <input
                                ref={replaceInputRef}
                                type="file"
                                accept=".pdf"
                                onChange={handleReplaceFile}
                                className="hidden"
                            />
                            <button
                                type="button"
                                onClick={() => replaceInputRef.current?.click()}
                                disabled={isReplacing}
                                className={btnSmPrimary}
                            >
                                {isReplacing ? t("televerser.remplacementEnCours") : t("televerser.remplacerCv")}
                            </button>
                        </>
                    )}
                    <button
                        type="button"
                        onClick={onToggle}
                        aria-expanded={isOpen}
                        className={`${btnSmPrimary}`}
                    >
                        {isOpen ? t("televerser.masquer") : t("televerser.afficher")}
                    </button>
                </div>
            </div>

            {replaceError && (
                <p className={`mt-2 text-sm ${textError}`} role="alert">{t(replaceError)}</p>
            )}

            {isDeclined && (
                <div className="mt-3 rounded-md border border-red-200 bg-red-50 px-3 py-2">
                    {renderFeedback()}
                </div>
            )}

            <div className="w-full">
                {isOpen && renderPreview()}
            </div>
        </div>
    );
};

export default CvListItem;