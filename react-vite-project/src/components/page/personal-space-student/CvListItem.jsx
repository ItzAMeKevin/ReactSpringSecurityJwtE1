import { useTranslation } from "react-i18next";
import { useState, useRef, useEffect } from "react";
import { Document, Page } from "react-pdf";

const CvListItem = ({ cv, isOpen, onToggle }) => {
    const { t } = useTranslation();
    const [numPages, setNumPages] = useState(null);
    const [containerWidth, setContainerWidth] = useState(null);
    const containerRef = useRef(null);

    useEffect(() => {
        if (!containerRef.current) return;

        const resizeObserver = new ResizeObserver((entries) => {
            setContainerWidth(entries[0].contentRect.width);
        });
        resizeObserver.observe(containerRef.current);

        return () => resizeObserver.disconnect();
    }, [isOpen]);

    return (
        <div className="rounded-xl border border-[#4b1113]/20 bg-white/40 px-4 py-3">
            <div className="flex items-center justify-between gap-4">
                <span className="text-[#4b1113] font-medium truncate">{cv.fileName}</span>
                <button
                    onClick={onToggle}
                    className="shrink-0 bg-[#4b1113] text-white text-sm font-medium py-1.5 px-3 rounded-md hover:bg-[#3a0d0f] transition-colors"
                >
                    {isOpen ? t("televerser.masquer", "Masquer") : t("televerser.afficher", "Afficher")}
                </button>
            </div>

            {isOpen && (
                cv.file ? (
                    <div ref={containerRef} className="mt-3 w-full overflow-hidden">
                        {containerWidth && (
                            <Document
                                file={cv.file}
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
                    </div>
                ) : (
                    <p className="mt-3 text-sm text-[#4b1113]/70">
                        {t(
                            "televerser.apercuIndisponible",
                            "Aperçu bientôt disponible (en attente de la route de récupération du fichier)."
                        )}
                    </p>
                )
            )}
        </div>
    );
};

export default CvListItem;