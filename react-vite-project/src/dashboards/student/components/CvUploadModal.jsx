import { useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import Modal from "@/components/ui/Modal.jsx";
import { btnAcceptLg, btnOutline, textError } from "@/styles/classes.js";

const CvUploadModal = ({ onUpload, onUploaded, onClose }) => {
    const { t } = useTranslation();
    const inputRef = useRef(null);
    const [file, setFile] = useState(null);
    const [errorKey, setErrorKey] = useState(null);
    const [isUploading, setIsUploading] = useState(false);

    const handleChange = (event) => {
        const selected = event.target.files?.[0];
        event.target.value = "";
        if (!selected) return;
        if (selected.type !== "application/pdf") {
            setFile(null);
            setErrorKey("televerser.erreurTypeFichier");
            return;
        }
        setErrorKey(null);
        setFile(selected);
    };

    const handleUpload = async () => {
        setIsUploading(true);
        setErrorKey(null);
        try {
            onUploaded(await onUpload(file));
        } catch (error) {
            console.error("Erreur lors de l'envoi du fichier :", error);
            setErrorKey("televerser.envoieErreur");
            setIsUploading(false);
        }
    };

    return (
        <Modal title={t("televerser.choisirFichier")} onClose={onClose} widthClass="w-full max-w-lg">
            <div className="flex flex-col items-center gap-4">
                <input ref={inputRef} type="file" accept=".pdf" onChange={handleChange} className="hidden" />
                <button type="button" onClick={() => inputRef.current?.click()} className={btnOutline}>
                    {t(file ? "televerser.changerFichier" : "televerser.selectionnerFichier")}
                </button>
                {file && <p className="font-medium">{t("televerser.fichierChoisi")} : {file.name}</p>}
                {errorKey && <p className={`${textError} text-sm`} role="alert">{t(errorKey)}</p>}
                <button type="button" onClick={handleUpload} disabled={!file || isUploading} className={btnAcceptLg}>
                    {t(isUploading ? "televerser.envoieEnCours" : "televerser.envoyerCv")}
                </button>
            </div>
        </Modal>
    );
};

export default CvUploadModal;
