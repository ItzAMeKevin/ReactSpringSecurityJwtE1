import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Document, Page, pdfjs } from "react-pdf";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";
import workerUrl from "pdfjs-dist/build/pdf.worker.min.mjs?url";
import { btnSmPrimary } from "../styles/classes.js";

pdfjs.GlobalWorkerOptions.workerSrc = workerUrl;

const MIN_ZOOM = 0.5;
const MAX_ZOOM = 2.0;
const ZOOM_STEP = 0.25;
const BASE_WIDTH = 700;
const VIEWPORT_MARGIN = 100;

const getAvailableWidth = () => window.innerWidth - VIEWPORT_MARGIN;


const CvPdfViewer = ({ file }) => {
    const { t } = useTranslation();
    const [availableWidth, setAvailableWidth] = useState(getAvailableWidth);
    const [numPages, setNumPages] = useState(0);
    const [zoom, setZoom] = useState(1);

    useEffect(() => {
        const handleResize = () => setAvailableWidth(getAvailableWidth());
        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize);
    }, []);

    const pageWidth = Math.min(BASE_WIDTH * zoom, availableWidth);
    const canZoomIn = zoom < MAX_ZOOM && BASE_WIDTH * (zoom + ZOOM_STEP) <= availableWidth;
    const canZoomOut = zoom > MIN_ZOOM;

    const changeZoom = (delta) =>
        setZoom((current) => Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, current + delta)));

    return (
        <div style={{ width: pageWidth }}>
            <div className="mb-2 flex items-center justify-end gap-2">
                <button
                    type="button"
                    className={btnSmPrimary}
                    onClick={() => changeZoom(-ZOOM_STEP)}
                    disabled={!canZoomOut}
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
                    disabled={!canZoomIn}
                    aria-label={t("cvViewer.zoomIn")}
                    title={t("cvViewer.zoomIn")}
                >
                    +
                </button>
            </div>
            <Document
                file={file}
                onLoadSuccess={({ numPages }) => setNumPages(numPages)}
                onLoadError={(error) => console.error("Erreur de chargement PDF :", error)}
            >
                {Array.from({ length: numPages }, (_, index) => (
                    <Page
                        key={index}
                        pageNumber={index + 1}
                        width={pageWidth}
                        className="mx-auto mb-2 w-fit"
                    />
                ))}
            </Document>
        </div>
    );
};

export default CvPdfViewer;
