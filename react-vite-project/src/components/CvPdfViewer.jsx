import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { Document, Page, pdfjs } from "react-pdf";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";
import workerUrl from "pdfjs-dist/build/pdf.worker.min.mjs?url";
import { btnSmPrimary } from "../styles/classes.js";

pdfjs.GlobalWorkerOptions.workerSrc = workerUrl;

const MIN_ZOOM = 0.5;
const MAX_ZOOM = 2.5;
const ZOOM_STEP = 0.25;

const CvPdfViewer = ({ file }) => {
    const { t } = useTranslation();
    const containerRef = useRef(null);
    const [containerWidth, setContainerWidth] = useState(null);
    const [numPages, setNumPages] = useState(0);
    const [zoom, setZoom] = useState(1);

    useEffect(() => {
        const observer = new ResizeObserver(([entry]) => {
            setContainerWidth(entry.contentRect.width);
        });
        observer.observe(containerRef.current);
        return () => observer.disconnect();
    }, []);

    const changeZoom = (delta) =>
        setZoom((current) => Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, current + delta)));

    return (
        <div className="mt-3 w-full">
            <div className="mb-2 flex items-center justify-end gap-2">
                <button
                    type="button"
                    className={btnSmPrimary}
                    onClick={() => changeZoom(-ZOOM_STEP)}
                    disabled={zoom <= MIN_ZOOM}
                    aria-label={t("cvViewer.zoomOut")}
                    title={t("cvViewer.zoomOut")}
                >
                    −
                </button>
                <button
                    type="button"
                    className={btnSmPrimary}
                    onClick={() => setZoom(1)}
                    aria-label={t("cvViewer.resetZoom")}
                    title={t("cvViewer.resetZoom")}
                >
                    {Math.round(zoom * 100)} %
                </button>
                <button
                    type="button"
                    className={btnSmPrimary}
                    onClick={() => changeZoom(ZOOM_STEP)}
                    disabled={zoom >= MAX_ZOOM}
                    aria-label={t("cvViewer.zoomIn")}
                    title={t("cvViewer.zoomIn")}
                >
                    +
                </button>
            </div>
            <div ref={containerRef} className="w-full overflow-auto">
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
                                width={containerWidth * zoom}
                                className="mb-2"
                            />
                        ))}
                    </Document>
                )}
            </div>
        </div>
    );
};

export default CvPdfViewer;
