import { useTranslation } from "react-i18next";
import { useState, useRef, useEffect } from "react";
import { Document, Page } from "react-pdf";
import { fetchBlobUrl } from "../../../utils/downloadFile.js";
import { cardItem, btnSmPrimary, textError, textMuted } from "../../../styles/classes.js";

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

const STATUS_STYLES = {
    PENDING: "bg-yellow-100 text-yellow-800",
    ACCEPTED: "bg-green-100 text-green-800",
    DECLINED: "bg-red-100 text-red-800",
};

const CvListItem = ({ cv, isOpen, onToggle }) => {
    const { t } = useTranslation();
    const statusStyle = STATUS_STYLES[cv.status] ?? "bg-gray-100 text-gray-700";
    const [previewUrl, setPreviewUrl] = useState(null);
    const [previewError, setPreviewError] = useState(false);

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

    return (
        <div className={`${cardItem} border-2`}>
            <div className="flex items-center justify-between gap-2">
                <div className="min-w-0 flex items-center gap-2">
                    <span className="text-main font-medium truncate">{cv.fileName}</span>
                    {cv.status && (
                        <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${statusStyle}`}>
                            {t(`televerser.statuts.${cv.status}`, cv.status)}
                        </span>
                    )}
                </div>
                <button
                    type="button"
                    onClick={onToggle}
                    aria-expanded={isOpen}
                    className={`${btnSmPrimary}`}
                >
                    {isOpen ? t("televerser.masquer") : t("televerser.afficher")}
                </button>
            </div>

            <div className="w-full">
                {isOpen && renderPreview()}
            </div>
        </div>
    );
};

export default CvListItem;