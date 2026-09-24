import { useTranslation } from "react-i18next";
import {useRef, useState} from "react";
import { Document, Page, pdfjs } from "react-pdf";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";
import workerUrl from "pdfjs-dist/build/pdf.worker.min.mjs?url";
import fetcher from "../../../utils/fetcher.js";
import CvListItem from "./CvListItem.jsx";

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
    const inputRef = useRef(null);
    const [selectedFile, setSelectedFile] = useState(null);
    const [uploadState, setUploadState] = useState("idle");
    const [cvList, setCvList] = useState([]);
    const [openCvId, setOpenCvId] = useState(null);

    // TODO: une fois que le backend expose GET /student-cv (liste des CV
    // associés au compte étudiant connecté), charger la liste au montage :
    //
    // useEffect(() => {
    //     fetcher("/student-cv")
    //         .then((res) => res.json())
    //         .then((data) => setCvList(data));
    // }, []);

    const handleInputChange = (event) => {
        const file = event.target.files?.[0];
        setSelectedFile(file ?? null);
        setUploadState("idle");
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
            });

            if (response.status === 201) {
                const cvMeta = await response.json();
                setCvList((prev) => [{ ...cvMeta, file: selectedFile }, ...prev]);
                setUploadState("success");
                setSelectedFile(null);
            } else {
                setUploadState("error");
            }
        } catch (error) {
            console.error("Erreur lors de l'envoi du fichier :", error);
            setUploadState("error");
        }
    };

    return (
        <div className="relative min-h-screen overflow-hidden bg-[#f3ebe3]">
            <div className="flex flex-col items-center gap-4 pt-20 pb-20 px-4">
                <input
                    ref={inputRef}
                    type="file"
                    accept=".pdf"
                    onChange={handleInputChange}
                    className="hidden"
                />
                <button
                    type="button"
                    onClick={() => inputRef.current?.click()}
                    className="bg-[#B3FFD9] font-bold text-lg py-2 px-4 rounded-md hover:bg-[#8DCCAD] transition-colors"
                >
                    {t("televerser.choisirFichier")}
                </button>

                {selectedFile && (
                    <>
                        <p className="font-medium text-lg">
                            {t("televerser.fichierChoisi")} : {selectedFile.name}
                        </p>

                        <button
                            onClick={handleUpload}
                            disabled={uploadState === "uploading"}
                            className="bg-[#B3FFD9] text-lg font-bold py-2 px-4 rounded-md hover:bg-[#8DCCAD] transition-colors disabled:opacity-60"
                        >
                            {uploadState === "uploading"
                                ? t("televerser.envoieEnCours")
                                : t("televerser.envoyerCv")}
                        </button>
                    </>
                )}

                {uploadState === "success" && (
                    <p className="text-green-700">{t("televerser.envoieSuccess")}</p>
                )}
                {uploadState === "error" && (
                    <p className="text-red-700">{t("televerser.envoieErreur")}</p>
                )}

                {cvList.length > 0 && (
                    <div className="w-full mt-10">
                        <h2 className="text-[#4b1113] font-semibold mb-3">
                            {t("televerser.mesCvs")}
                        </h2>
                        <div className="flex flex-col gap-3">
                            {cvList.map((cv) => (
                                <CvListItem
                                    key={cv.id}
                                    cv={cv}
                                    isOpen={openCvId === cv.id}
                                    onToggle={() =>
                                        setOpenCvId((current) => (current === cv.id ? null : cv.id))
                                    }
                                />
                            ))}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default PersonalSpaceStudent