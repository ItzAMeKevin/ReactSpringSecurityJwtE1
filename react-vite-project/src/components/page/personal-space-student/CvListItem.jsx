import { useTranslation } from "react-i18next";
import { useState, useRef, useEffect } from "react";
import { Document, Page } from "react-pdf";

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

const CvListItem = ({ cv, isOpen, onToggle }) => {
    const { t } = useTranslation();
    const statusLabel = (status) => t(`televerser.status.${status}`, {defaultValue: status});
    const statusStyles = {
        PENDING: "bg-yellow-100 text-yellow-800",
        ACCEPTED: "bg-green-100 text-green-800",
        REFUSED: "bg-red-100 text-red-800",
    };

    return (
        <div className="rounded-xl border border-[#4b1113]/20 bg-white/40 px-4 py-3">
            <div className="flex items-center justify-between gap-4">
                <span className="text-[#4b1113] font-medium truncate">{cv.fileName}</span>
                <span className={`w-fit rounded px-2 py-0.5 text-xs font-bold ${statusStyles[cv.status] ?? "bg-gray-100 text-gray-800"}`}>
                    {statusLabel(cv.status)}
                </span>
                <button
                    type="button"
                    onClick={onToggle}
                    aria-expanded={isOpen}
                    className="shrink-0 bg-[#4b1113] text-white text-sm font-medium py-1.5 px-3 rounded-md hover:bg-[#3a0d0f] transition-colors"
                >
                    {isOpen ? t("televerser.masquer") : t("televerser.afficher")}
                </button>
            </div>

            {isOpen && (
                cv.file ? (
                    <CvPreview file={cv.file} />
                ) : (
                    <p className="mt-3 text-sm text-[#4b1113]/70">
                        {t("televerser.apercuIndisponible")}
                    </p>
                )
            )}
        </div>
    );
};

export default CvListItem;