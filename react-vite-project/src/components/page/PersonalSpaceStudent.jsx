import { useTranslation } from "react-i18next";

const PersonalSpaceStudent = () => {
    const {t} = useTranslation();
    const handleFileUpload = (event) => {
        console.log("File uploaded");
    };

    return (
        <>
            <div className="relative min-h-screen overflow-hidden bg-[#f3ebe3]">
                <div className="flex h-screen items-center justify-center">
                    <button onClick={handleFileUpload} className="bg-[#4b1113] text-white font-medium py-2 px-4 rounded-md hover:bg-[#3a0d0f] transition-colors">
                        {t("televerser")}
                    </button>
                </div>
            </div>
        </>
    )
}

export default PersonalSpaceStudent