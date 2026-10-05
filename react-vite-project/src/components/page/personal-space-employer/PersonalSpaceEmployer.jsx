import ListJobOffer from "./ListJobOffer.jsx";
import {useTranslation} from "react-i18next";
import {useState} from "react";
import {
  Dialog,
  DialogHeader,
  DialogBody,
  DialogFooter,
} from "@material-tailwind/react";

const PersonalSpaceEmployer = () => {
    
    const {t} = useTranslation();

    const [openForm, setOpenForm] = useState(false);

    const handleOpenForm = () => setOpenForm(!openForm);
    
    return (
        <>
            <div className="relative min-h-screen overflow-hidden bg-[#f3ebe3]">
                <div className="flex flex-col items-center gap-4 pt-20 pb-20 px-4">
                    <h1 className="text-2xl font-bold text-[#4b1113]">{t("personalSpaceEmployer.title")}</h1>
                    <button className="bg-[#4b1113] text-white px-4 py-2 rounded hover:bg-[#6b1c1f] transition-colors duration-300" onClick={handleOpenForm}>
                        t("personalSpaceEmployer.createJobOffer")
                    </button>
                    <Dialog open={openForm} handler={handleOpenForm} className="w-full max-w-md p-6 bg-white rounded shadow-lg">
                        <DialogHeader className="text-lg font-bold text-[#4b1113]">{t("personalSpaceEmployer.createJobOffer")}</DialogHeader>
                        <DialogBody className="text-sm text-[#4b1113]/70">{t("personalSpaceEmployer.createJobOfferDescription")}</DialogBody>
                        <DialogFooter
                            className="flex justify-end gap-2"
                        >
                            <button className="bg-[#4b1113] text-white px-4 py-2 rounded hover:bg-[#6b1c1f] transition-colors duration-300" onClick={handleOpenForm}>
                                {t("personalSpaceEmployer.close")}
                            </button>
                        </DialogFooter>
                    </Dialog>
                    <h2 className="text-xl font-bold text-[#4b1113]">t("personalSpaceEmployer.jobOffers")</h2>  
                    <ListJobOffer /> 
                </div>
            </div>
        </>
    );
}

export default PersonalSpaceEmployer;
