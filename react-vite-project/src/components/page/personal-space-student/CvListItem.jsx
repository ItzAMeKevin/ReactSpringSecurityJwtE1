import { useTranslation } from "react-i18next";
import { useState, useRef, useEffect } from "react";
import { Document, Page } from "react-pdf";
import fetcher from "../../../utils/fetcher.js";
import { fetchBlobUrl } from "../../../utils/downloadFile.js";
import {
    cardItem,
    btnSmPrimary,
    btnRefuse,
    statusBadge,
    statusBadgeColor,
    textError,
    textMuted,
    btnSmSelectedSoft
} from "../../../styles/classes.js";

const CvPreview = ({ file }) => {
    const [numPages, setNumPages] = useState(0);
    const [containerWidth, setContainerWidth] = useState(null);
    const containerRef = useRef(null);

    useEffect(() => {
        const observer = new ResizeObserver(([entry]) => {
            setContainerWidth(entry.contentRect.width);
        });
        observer.observe(containerRef.current);
        return () => observer.disconnect();
    }, []);

    return (
        <div ref={containerRef} className="mt-3 w-full overflow-hidden">
            {containerWidth && (
                <Document
                    file={file}
                    onLoadSuccess={({ numPages }) => setNumPages(numPages)}
                    onLoadError={(error) => console.error("Erreur de chargement PDF :", error)}
                >
                    {Array.from({ length: numPages }, (_, index) => (
                        <Page
                            key={index}
                            pageNumber={index + 1}
                            width={containerWidth}
                            className="mb-2"
                        />
                    ))}
                </Document>
            )}
        </div>
    );
};

const CvListItem = ({
                        cv,
                        isOpen,
                        onToggle,
                        isFeedbackOpen,
                        onToggleFeedback,
                        isDefault,
                        hasDefault,
                        onToggleDefault,
                    }) => {
    const { t } = useTranslation();
    const isAccepted = cv.status === "ACCEPTED";
    const isDeclined = cv.status === "DECLINED";
    const cardRef = useRef(null);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [previewError, setPreviewError] = useState(false);
    const [feedback, setFeedback] = useState(null);
    const [feedbackError, setFeedbackError] = useState(false);

    useEffect(() => {
        if (!isFeedbackOpen) return;
        cardRef.current?.scrollIntoView({ behavior: "smooth", block: "nearest" });
        if (feedback !== null) return;
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
    }, [isFeedbackOpen, cv.id, feedback]);

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
        if (cv.file) return <CvPreview file={cv.file} />;
        if (previewUrl) return <CvPreview file={previewUrl} />;
        return (
            <p className={`mt-3 text-sm ${previewError ? textError : textMuted}`}>
                {previewError ? t("televerser.apercuIndisponible") : t("televerser.chargementApercu")}
            </p>
        );
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
        <div ref={cardRef} className={`${cardItem} border-2`}>
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
                    {isAccepted &&(
                        <button
                            type="button"
                            disabled={isDefault}
                            onClick={onToggleDefault}
                            className={isDefault ? btnSmSelectedSoft : btnSmPrimary}
                        >
                            {isDefault
                                ? t("televerser.defaultActif")
                                : t("televerser.default")}
                        </button>


                    )}
                    {isDeclined && (
                        <button
                            type="button"
                            onClick={onToggleFeedback}
                            aria-expanded={isFeedbackOpen}
                            className={btnSmPrimary}
                        >
                            {isFeedbackOpen ? t("televerser.masquerFeedback") : t("televerser.voirFeedback")}
                        </button>
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

            {isDeclined && isFeedbackOpen && (
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