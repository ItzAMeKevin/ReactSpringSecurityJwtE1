import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import fetcher from "../../utils/fetcher.js";
import ManagerCvListItem from "./ManagerCvListItem.jsx";

const PersonalSpaceManager = () => {
    const { t } = useTranslation();
    const [pendingCvs, setPendingCvs] = useState([]);
    const [status, setStatus] = useState("loading");

    useEffect(() => {
        const fetchPendingCvs = async () => {
            try {
                const response = await fetcher("/gestionnaire/cv/pending");
                if (!response.ok) {
                    setStatus("error");
                    return;
                }
                setPendingCvs(await response.json());
                setStatus("loaded");
            } catch (error) {
                console.error("Erreur lors du chargement des CVs en attente :", error);
                setStatus("error");
            }
        };

        void fetchPendingCvs();
    }, []);

    const removeFromList = ((cvId) => {
        setPendingCvs((current) => current.filter((cv) => cv.id !== cvId));
    });

    const handleAccept = (async (cvId) => {
        const response = await fetcher(`/gestionnaire/cv/${cvId}/accept`, {
            method: "PUT",
        });
        if (!response.ok) {
            throw new Error("Erreur lors de l'acceptation du CV");
        }
        removeFromList(cvId);
    });

    const handleDecline = (async (cvId, reviewPayload) => {
        const response = await fetcher(`/gestionnaire/cv/${cvId}/decline`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(reviewPayload),
        });
        if (!response.ok) {
            throw new Error("Erreur lors du refus du CV");
        }
        removeFromList(cvId);
    });

    const isLoaded = status === "loaded";

    return (
        <div className="relative min-h-screen overflow-hidden bg-[#f3ebe3]">
            <div className="flex flex-col items-center gap-4 pt-20 pb-20 px-4">
                <h1 className="text-2xl font-bold text-[#4b1113]">{t("gestionnaire.title")}</h1>

                {status === "loading" && (
                    <p className="text-[#4b1113]/70">{t("gestionnaire.loadingCvs")}</p>
                )}

                {status === "error" && (
                    <p className="text-red-700" role="alert">
                        {t("gestionnaire.errorLoadingCvs")}
                    </p>
                )}

                {isLoaded && pendingCvs.length === 0 && (
                    <p className="text-[#4b1113]/70">{t("gestionnaire.noCvsPending")}</p>
                )}

                {isLoaded && pendingCvs.length > 0 && (
                    <div className="w-full max-w-3xl flex flex-col gap-3">
                        {pendingCvs.map((cv) => (
                            <ManagerCvListItem
                                key={cv.id}
                                cv={cv}
                                onAccept={handleAccept}
                                onDecline={handleDecline}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default PersonalSpaceManager;
