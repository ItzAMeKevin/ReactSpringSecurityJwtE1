import { useTranslation } from "react-i18next";
import { useEffect, useRef, useState } from "react";
import { pdfjs } from "react-pdf";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";
import workerUrl from "pdfjs-dist/build/pdf.worker.min.mjs?url";
import fetcher from "../../../utils/fetcher.js";
import CvListItem from "./CvListItem.jsx";
import { btnAcceptLg, pageWrapper, pageInner, textError, textMuted, textSuccess } from "../../../styles/classes.js";

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
    const [fileError, setFileError] = useState(null);
    const [selectedFile, setSelectedFile] = useState(null);
    const [uploadState, setUploadState] = useState("idle");
    const [cvList, setCvList] = useState([]);
    const [openCvId, setOpenCvId] = useState(null);
    const [isLoadingList, setIsLoadingList] = useState(true);
    const [listError, setListError] = useState(null);

    useEffect(() => {
        const loadCvList = async () => {
            try {
                const response = await fetcher("/student-cv");
                if (!response.ok) {
                    setListError(t("televerser.erreurChargement"));
                    return;
                }
                setCvList(await response.json());
            } catch (error) {
                console.error("Erreur lors du chargement des CVs :", error);
                setListError(t("televerser.erreurChargement"));
            } finally {
                setIsLoadingList(false);
            }
        };
        void loadCvList();
    }, [t]);

    const handleInputChange = (event) => {
        const file = event.target.files?.[0];
        if (!file) {
            setSelectedFile(null);
            return;
        }

        if (file.type !== "application/pdf") {
            setFileError(t("televerser.erreurTypeFichier"));
            setSelectedFile(null);
            event.target.value = "";
            return;
        }

        setFileError(null);
        setSelectedFile(file);
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
        <div className={pageWrapper}>
            <div className={pageInner}>
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
                    className={btnAcceptLg}
                >
                    {t("televerser.choisirFichier")}
                </button>
                {fileError && (
                    <p className={`${textError} text-sm`}>{fileError}</p>
                )}

                {selectedFile && (
                    <>
                        <p className="font-medium text-lg">
                            {t("televerser.fichierChoisi")} : {selectedFile.name}
                        </p>

                        <button
                            onClick={handleUpload}
                            disabled={uploadState === "uploading"}
                            className={btnAcceptLg}
                        >
                            {uploadState === "uploading"
                                ? t("televerser.envoieEnCours")
                                : t("televerser.envoyerCv")}
                        </button>
                    </>
                )}

                {uploadState === "success" && (
                    <p className={textSuccess}>{t("televerser.envoieSuccess")}</p>
                )}
                {uploadState === "error" && (
                    <p className={textError}>{t("televerser.envoieErreur")}</p>
                )}

                <div className="w-full mt-10">
                    <h2 className="text-main font-semibold mb-3">
                        {t("televerser.mesCvs")}
                    </h2>
                    {isLoadingList && (
                        <p className={`${textMuted} text-sm`}>{t("televerser.chargement")}</p>
                    )}
                    {listError && (
                        <p className={`${textError} text-sm`} role="alert">{listError}</p>
                    )}
                    {!isLoadingList && !listError && cvList.length === 0 && (
                        <p className={`${textMuted} text-sm`}>{t("televerser.aucunCv")}</p>
                    )}
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
            </div>
        </div>
    );
}

export default PersonalSpaceStudent