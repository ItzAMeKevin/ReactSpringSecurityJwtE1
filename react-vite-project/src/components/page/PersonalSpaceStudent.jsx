import { useTranslation } from "react-i18next";
import { useState } from "react";
import { Document, Page, pdfjs } from "react-pdf";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";
import workerUrl from "pdfjs-dist/build/pdf.worker.min.mjs?url";
import fetcher from "../../utils/fetcher.js";

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

            const response = await fetcher("/student-cv", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
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
                    <input
                        type="file"
                        accept=".pdf"
                        onChange={handleInputChange}
                        className="block text-sm text-[#4b1113]
                           file:mr-4 file:py-2 file:px-4
                           file:rounded-md file:border-0
                           file:text-sm file:font-medium
                           file:bg-[#4b1113] file:text-white
                           hover:file:bg-[#3a0d0f]
                           file:transition-colors
                           file:cursor-pointer cursor-pointer"
                    />
                    {selectedFile && (
                        <>
                            <p className="text-[#4b1113]">
                                {t("televerser.fichierChoisi")} : {selectedFile.name}
                            </p>

                            <button
                                onClick={handleUpload}
                                disabled={uploadState === "uploading"}
                                className="bg-[#4b1113] text-white font-medium py-2 px-4 rounded-md hover:bg-[#3a0d0f] transition-colors disabled:opacity-60"
                            >
                                {uploadState === "uploading" ? t("televerser.envoieEnCours") : t("televerser.envoyerCv")}
                            </button>

                            {uploadState === "success" && (
                                <p className="text-green-700">{t("televerser.envoieSuccess")}</p>
                            )}
                            {uploadState === "error" && (
                                <p className="text-red-700">{t("televerser.envoieErreur")}</p>
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