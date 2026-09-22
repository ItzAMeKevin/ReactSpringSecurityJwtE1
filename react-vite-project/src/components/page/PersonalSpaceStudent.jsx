import { useTranslation } from "react-i18next";
import { useState } from "react";
import { Document, Page, pdfjs } from "react-pdf";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";
import workerUrl from "pdfjs-dist/build/pdf.worker.min.mjs?url";

pdfjs.GlobalWorkerOptions.workerSrc = workerUrl;

const fileToBase64 = (file) =>
    new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result.split(",")[1]);
        reader.onerror = () => reject(reader.error);
        reader.readAsDataURL(file);
    });

const PersonalSpaceStudent = () => {
    const {t} = useTranslation();
    const [selectedFile, setSelectedFile] = useState(null);
    const [numPages, setNumPages] = useState(null);
    const [uploadState, setUploadState] = useState("idle");

    const handleInputChange = (event) => {
        const file = event.target.files?.[0];
        setSelectedFile(file ?? null);
        setNumPages(null);
        setUploadState("idle");
    };

    const onDocumentLoadSuccess = ({ numPages }) => {
        setNumPages(numPages);
    };

    const handleUpload = async () => {
        if (!selectedFile) return;
        setUploadState("uploading");
        try {
            const content = await fileToBase64(selectedFile);

            const response = await fetch("http://localhost:8080/student-cv", {
                method: "POST",
                header: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    fileName: selectedFile.name,
                    contentType: selectedFile.type,
                    content,
                }),
            })

            if (response.status === 201) {
                setUploadState("success");
            } else {
                setUploadState("error");
            }
        } catch (error) {
            console.error("Erreur lors du l'envoi du fichier :", error);
            setUploadState("error");
        }
    }

    return (
        <>
            <div className="relative min-h-screen overflow-hidden bg-[#f3ebe3]">
                <div className="flex flex-col items-center justify-center gap-4 pt-20">
                    <input type="file" accept=".pdf" onChange={handleInputChange} />
                    {selectedFile && (
                        <>
                            <p className="text-[#4b1113]">
                                Fichier sélectionné : {selectedFile.name}
                            </p>

                            <button
                                onClick={handleUpload}
                                disabled={uploadState === "uploading"}
                                className="bg-[#4b1113] text-white font-medium py-2 px-4 rounded-md hover:bg-[#3a0d0f] transition-colors disabled:opacity-60"
                            >
                                {uploadState === "uploading" ? "Envoi en cours..." : "Envoyer le CV"}
                            </button>

                            {uploadState === "success" && (
                                <p className="text-green-700">CV envoyé avec succès !</p>
                            )}
                            {uploadState === "error" && (
                                <p className="text-red-700">Une erreur est survenue, réessaie.</p>
                            )}
                        </>
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