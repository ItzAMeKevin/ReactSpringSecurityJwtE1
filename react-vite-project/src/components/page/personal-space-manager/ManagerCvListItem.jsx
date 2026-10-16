import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { Document, Page } from "react-pdf";
import { fetchBlobUrl, downloadFile } from "../../../utils/downloadFile.js";
import { cardItem, btnSmPrimary, btnAccept, btnRefuse, textError } from "../../../styles/classes.js";

const ManagerCvListItem = ({ cv, isHighlighted, onAccept, onDecline }) => {
    const { t } = useTranslation();
    const contentUrl = `/gestionnaire/cv/${cv.id}/content`;
    const containerRef = useRef(null);
    const reviewInputRef = useRef(null);

    const [isOpen, setIsOpen] = useState(false);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [previewError, setPreviewError] = useState(null);
    const [numPages, setNumPages] = useState(null);
    const [containerWidth, setContainerWidth] = useState(null);

    const [actionState, setActionState] = useState("idle");
    const [isDeclining, setIsDeclining] = useState(false);
    const [reviewFeedback, setReviewFeedback] = useState("");
    const [reviewError, setReviewError] = useState(null);

    useEffect(() => {
        if (!containerRef.current || !isOpen) return;
        const resizeObserver = new ResizeObserver((entries) => {
            setContainerWidth(entries[0].contentRect.width);
        });
        resizeObserver.observe(containerRef.current);
        return () => resizeObserver.disconnect();
    }, [isOpen]);

    useEffect(() => {
        return () => {
            if (previewUrl) URL.revokeObjectURL(previewUrl);
        };
    }, [previewUrl]);

    const handleToggle = async () => {
        if (isOpen) {
            setIsOpen(false);
            return;
        }
        setIsOpen(true);
        if (previewUrl) return;
        setPreviewError(null);

        try {
            const url = await fetchBlobUrl(contentUrl);
            setPreviewUrl(url);
        } catch (error) {
            console.error("Erreur lors du chargement de l'aperçu :", error);
            setPreviewError(t("gestionnaire.preview.errorLoading"));
        }
    };

    const handleDownload = async () => {
        try {
            await downloadFile(contentUrl, cv.fileName || "cv.pdf");
        } catch (error) {
            console.error("Erreur lors du téléchargement du CV :", error);
        }
    };

    const handleAccept = async () => {
        setActionState("processing");
        try {
            await onAccept(cv.id);
        } catch (error) {
            console.error("Erreur lors de l'acceptation du CV :", error);
            setActionState("error");
        }
    };

    const handleConfirmDecline = async () => {
        if (!reviewFeedback.trim()) {
            setReviewError(t("gestionnaire.decline.errorNoFeedback"));
            return;
        }
        setActionState("processing");
        try {
            await onDecline(cv.id, { feedback: reviewFeedback.trim() });
        } catch (error) {
            console.error("Erreur lors du refus du CV :", error);
            setActionState("error");
        }
    };

    return (
        <div className={`${cardItem} ${
            isHighlighted ? 'bg-yellow-100 border-yellow-400 shadow-lg shadow-yellow-200' : ''
        }`}>
            <div className="flex flex-wrap items-center justify-between gap-3">
                <div className="min-w-0">
                    <p className="text-main font-medium truncate">{cv.fileName}</p>
                    <p className="text-sm text-main/70 truncate">
                        {cv.studentFirstName} {cv.studentLastName}
                        {cv.studentMatricule ? ` · ${cv.studentMatricule}` : ""}
                    </p>
                </div>

                <div className="flex shrink-0 flex-wrap items-center gap-2">
                    <button
                        type="button"
                        onClick={handleDownload}
                        className={`${btnSmPrimary} flex items-center`}
                    >
                        <svg
                            width="19"
                            height="19"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                        >
                            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                            <polyline points="7 10 12 15 17 10" />
                            <line x1="12" y1="15" x2="12" y2="3" />
                        </svg>
                    </button>
                    <button
                        type="button"
                        onClick={handleToggle}
                        aria-expanded={isOpen}
                        className={btnSmPrimary}
                    >
                        {isOpen ? t("gestionnaire.masquer") : t("gestionnaire.consulter")}
                    </button>
                    <button
                        type="button"
                        onClick={handleAccept}
                        disabled={actionState === "processing" || isDeclining}
                        className={btnAccept}
                    >
                        {t("gestionnaire.accepter")}
                    </button>
                    <button
                        type="button"
                        onClick={() => setIsDeclining((current) => !current)}
                        disabled={actionState === "processing"}
                        className={btnRefuse}
                    >
                        {t("gestionnaire.refuser")}
                    </button>
                </div>
            </div>

            {isOpen && (
                <div ref={containerRef} className="mt-3 w-full overflow-hidden">
                    {previewError && <p className={`${textError} text-sm`} role="alert">{previewError}</p>}
                    {!previewError && previewUrl && containerWidth && (
                        <Document
                            file={previewUrl}
                            onLoadSuccess={({ numPages }) => setNumPages(numPages)}
                        >
                            {Array.from(new Array(numPages ?? 0), (_, index) => (
                                <Page
                                    key={index}
                                    pageNumber={index + 1}
                                    width={containerWidth}
                                    className="mb-2"
                                />
                            ))}
                        </Document>
                    )}
                    {!previewError && !previewUrl && (
                        <p className="text-sm text-main/70">{t("gestionnaire.preview.loading")}</p>
                    )}
                </div>
            )}

            {isDeclining && (
                <div className="mt-3 rounded-md border border-red-200 bg-red-50 p-3">
                    <p className="text-sm text-main mb-2">
                        {t("gestionnaire.decline.title")}
                    </p>
                    <textarea
                        value={reviewFeedback}
                        onChange={(e) => setReviewFeedback(e.target.value)}
                        placeholder={t("gestionnaire.decline.placeholder")}
                        rows={3}
                        className="w-full border border-red-300 rounded-md py-2 px-3 text-sm text-main bg-white resize-y focus:outline-none focus:ring-2 focus:ring-red-400 mb-2"
                    />
                    <div className="flex justify-end">
                        <button
                            type="button"
                            onClick={handleConfirmDecline}
                            disabled={actionState === "processing"}
                            className={btnRefuse}
                        >
                            {actionState === "processing" ? t("gestionnaire.decline.processing") : t("gestionnaire.decline.confirmDecline")}
                        </button>
                    </div>
                    {reviewError && <p className={`${textError} text-sm mt-2`} role="alert">{reviewError}</p>}
                </div>
            )}

            {actionState === "acceptError" && (
                <p className={`${textError} text-sm mt-2`} role="alert">
                    {t("gestionnaire.accept.errorProcessing")}
                </p>
            )}
            {actionState === "declineError" && (
                <p className={`${textError} text-sm mt-2`} role="alert">
                    {t("gestionnaire.decline.errorProcessing")}
                </p>
            )}
        </div>
    );
};

export default ManagerCvListItem;
