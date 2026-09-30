import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { Document, Page } from "react-pdf";
import fetcher from "../../utils/fetcher.js";

const fileToBase64 = (file) =>
    new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result.split(",")[1]);
        reader.onerror = () => reject(reader.error);
        reader.readAsDataURL(file);
    });

const ManagerCvListItem = ({ cv, onAccept, onDecline }) => {
    const { t } = useTranslation();
    const containerRef = useRef(null);
    const reviewInputRef = useRef(null);

    const [isOpen, setIsOpen] = useState(false);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [previewError, setPreviewError] = useState(null);
    const [numPages, setNumPages] = useState(null);
    const [containerWidth, setContainerWidth] = useState(null);

    const [actionState, setActionState] = useState("idle");
    const [isDeclining, setIsDeclining] = useState(false);
    const [reviewFile, setReviewFile] = useState(null);
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
            const response = await fetcher(`/gestionnaire/cv/${cv.id}/content`);
            if (!response.ok) {
                setPreviewError(t("gestionnaire.preview.errorLoading"));
                return;
            }
            const blob = await response.blob();
            setPreviewUrl(URL.createObjectURL(blob));
        } catch (error) {
            console.error("Erreur lors du chargement de l'aperçu :", error);
            setPreviewError(t("gestionnaire.preview.errorLoading"));
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

    const handleReviewFileChange = (event) => {
        const file = event.target.files?.[0];
        if (!file) {
            setReviewFile(null);
            return;
        }
        if (file.type !== "application/pdf") {
            setReviewError(t("gestionnaire.decline.errorPdfOnly"));
            setReviewFile(null);
            event.target.value = "";
            return;
        }
        setReviewError(null);
        setReviewFile(file);
    };

    const handleConfirmDecline = async () => {
        if (!reviewFile) {
            setReviewError(t("gestionnaire.decline.errorNoFile"));
            return;
        }
        setActionState("processing");
        try {
            const content = await fileToBase64(reviewFile);
            await onDecline(cv.id, {
                fileName: reviewFile.name,
                contentType: reviewFile.type,
                content,
            });
        } catch (error) {
            console.error("Erreur lors du refus du CV :", error);
            setActionState("error");
        }
    };

    return (
        <div className="rounded-xl border border-[#4b1113]/20 bg-white/40 px-4 py-3">
            <div className="flex flex-wrap items-center justify-between gap-3">
                <div className="min-w-0">
                    <p className="text-[#4b1113] font-medium truncate">{cv.fileName}</p>
                    <p className="text-sm text-[#4b1113]/70 truncate">
                        {cv.studentFirstName} {cv.studentLastName}
                        {cv.studentMatricule ? ` · ${cv.studentMatricule}` : ""}
                    </p>
                </div>

                <div className="flex shrink-0 flex-wrap items-center gap-2">
                    <button
                        type="button"
                        onClick={handleToggle}
                        aria-expanded={isOpen}
                        className="bg-[#4b1113] text-white text-sm font-medium py-1.5 px-3 rounded-md hover:bg-[#3a0d0f] transition-colors"
                    >
                        {isOpen ? t("gestionnaire.masquer") : t("gestionnaire.consulter")}
                    </button>
                    <button
                        type="button"
                        onClick={handleAccept}
                        disabled={actionState === "processing" || isDeclining}
                        className="bg-[#B3FFD9] text-[#4b1113] text-sm font-bold py-1.5 px-3 rounded-md hover:bg-[#8DCCAD] transition-colors disabled:opacity-60"
                    >
                        {t("gestionnaire.accepter")}
                    </button>
                    <button
                        type="button"
                        onClick={() => setIsDeclining((current) => !current)}
                        disabled={actionState === "processing"}
                        className="bg-red-100 text-red-800 text-sm font-bold py-1.5 px-3 rounded-md hover:bg-red-200 transition-colors disabled:opacity-60"
                    >
                        {t("gestionnaire.refuser")}
                    </button>
                </div>
            </div>

            {isOpen && (
                <div ref={containerRef} className="mt-3 w-full overflow-hidden">
                    {previewError && <p className="text-red-700 text-sm" role="alert">{previewError}</p>}
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
                        <p className="text-sm text-[#4b1113]/70">{t("gestionnaire.preview.loading")}</p>
                    )}
                </div>
            )}

            {isDeclining && (
                <div className="mt-3 rounded-md border border-red-200 bg-red-50 p-3">
                    <p className="text-sm text-[#4b1113] mb-2">
                        {t("gestionnaire.decline.title")}
                    </p>
                    <input
                        ref={reviewInputRef}
                        type="file"
                        accept=".pdf"
                        onChange={handleReviewFileChange}
                        className="hidden"
                    />
                    <div className="flex flex-wrap items-center gap-2">
                        <button
                            type="button"
                            onClick={() => reviewInputRef.current?.click()}
                            className="bg-white border border-red-300 text-red-800 text-sm font-medium py-1.5 px-3 rounded-md hover:bg-red-100 transition-colors"
                        >
                            {t("gestionnaire.decline.chooseFile")}
                        </button>
                        {reviewFile && (
                            <span className="text-sm text-[#4b1113]">{reviewFile.name}</span>
                        )}
                        <button
                            type="button"
                            onClick={handleConfirmDecline}
                            disabled={actionState === "processing"}
                            className="bg-red-700 text-white text-sm font-bold py-1.5 px-3 rounded-md hover:bg-red-800 transition-colors disabled:opacity-60"
                        >
                            {actionState === "processing" ? t("gestionnaire.decline.processing") : t("gestionnaire.decline.confirmDecline")}
                        </button>
                    </div>
                    {reviewError && <p className="text-red-700 text-sm mt-2" role="alert">{reviewError}</p>}
                </div>
            )}

            {actionState === "error" && (
                <p className="text-red-700 text-sm mt-2" role="alert">
                    {t("gestionnaire.decline.errorProcessing")}
                </p>
            )}
        </div>
    );
};

export default ManagerCvListItem;
