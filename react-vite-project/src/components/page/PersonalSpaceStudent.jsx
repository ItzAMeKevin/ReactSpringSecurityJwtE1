import { useTranslation } from "react-i18next";
import { useState } from "react";

const PersonalSpaceStudent = () => {
    const {t} = useTranslation();
    const [selectedFile, setSelectedFile] = useState(null);

    const handleInputChange = (event) => {
        const file = event.target.file?.[0];
        setSelectedFile(file ?? null);
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
                </div>
            </div>
        </>
    )
}

export default PersonalSpaceStudent