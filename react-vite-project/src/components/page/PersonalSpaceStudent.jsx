import { useTranslation } from "react-i18next";
import { useState } from "react";
import { Document, Page, pdfjs } from "react-pdf";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";
import workerUrl from "pdfjs-dist/build/pdf.worker.min.mjs?url";

pdfjs.GlobalWorkerOptions.workerSrc = workerUrl;

const PersonalSpaceStudent = () => {
    const {t} = useTranslation();
    const [selectedFile, setSelectedFile] = useState(null);
    const [numPages, setNumPages] = useState(null);

    const handleInputChange = (event) => {
        const file = event.target.files?.[0];
        setSelectedFile(file ?? null);
        setNumPages(null);
    };

    const onDocumentLoadSuccess = ({ numPages }) => {
        setNumPages(numPages);
    };

    return (
        <>
            <div className="relative min-h-screen overflow-hidden bg-[#f3ebe3]">
                <div className="flex flex-col items-center justify-center gap-4 pt-20">
                    <input type="file" accept=".pdf" onChange={handleInputChange} />
                    {selectedFile && (
                        <p className="text-[#4b1113]">
                            Fichier sélectionné : {selectedFile.name}
                        </p>
                    )}

                    {selectedFile && (
                        <Document
                            file={selectedFile}
                            onLoadSuccess={onDocumentLoadSuccess}
                            onLoadError={(error) => console.error("Erreur de chargement PDF :", error)}
                        >
                            {Array.from(new Array(numPages ?? 0), (_, index) => (
                                <Page key={index} pageNumber={index + 1} className="mb-2" />
                            ))}
                        </Document>
                    )}
                </div>
            </div>
        </>
    )
}

export default PersonalSpaceStudent