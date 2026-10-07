import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import fetcher from "../../utils/fetcher.js";
import ManagerCvListItem from "./ManagerCvListItem.jsx";
import ManagerJobOfferListItem from "./ManagerJobOfferListItem.jsx";
import { useManagerNotificationsContext } from "../../context/ManagerNotificationsContext.jsx";

const PersonalSpaceManager = () => {
    const { t } = useTranslation();
    const location = useLocation();
    const { removeNotificationsByCvId, removeNotificationsByOfferId } = useManagerNotificationsContext();
    const [pendingCvs, setPendingCvs] = useState([]);
    const [status, setStatus] = useState("loading");
    const [highlightedCvId, setHighlightedCvId] = useState(null);
    const [pendingOffers, setPendingOffers] = useState([]);
    const [offersStatus, setOffersStatus] = useState("loading");
    const [highlightedOfferId, setHighlightedOfferId] = useState(null);

    useEffect(() => {
        if (location.state?.highlightCvId) {
            setHighlightedCvId(location.state.highlightCvId);
            // Remove highlight after 3 seconds
            const timer = setTimeout(() => {
                setHighlightedCvId(null);
            }, 3000);
            return () => clearTimeout(timer);
        }
    }, [location.state?.highlightCvId]);

    useEffect(() => {
        if (location.state?.highlightOfferId) {
            setHighlightedOfferId(location.state.highlightOfferId);
            const timer = setTimeout(() => setHighlightedOfferId(null), 3000);
            return () => clearTimeout(timer);
        }
    }, [location.state?.highlightOfferId]);

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

    useEffect(() => {
        const fetchPendingOffers = async () => {
            try {
                const response = await fetcher("/gestionnaire/offres/pending");
                if (!response.ok) {
                    setOffersStatus("error");
                    return;
                }
                setPendingOffers(await response.json());
                setOffersStatus("loaded");
            } catch (error) {
                console.error("Erreur lors du chargement des offres en attente :", error);
                setOffersStatus("error");
            }
        };
        void fetchPendingOffers();
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
        removeNotificationsByCvId(cvId);
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
        removeNotificationsByCvId(cvId);
    });

    const handleAcceptOffer = async (offerId) => {
        const response = await fetcher(`/gestionnaire/offres/${offerId}/accept`, { method: "PUT" });
        if (!response.ok) throw new Error("Erreur lors de l'acceptation de l'offre");
        setPendingOffers((current) => current.filter((o) => o.id !== offerId));
        removeNotificationsByOfferId(offerId);
    };

    const handleRefuseOffer = async (offerId) => {
        const response = await fetcher(`/gestionnaire/offres/${offerId}/refuse`, { method: "PUT" });
        if (!response.ok) throw new Error("Erreur lors du refus de l'offre");
        setPendingOffers((current) => current.filter((o) => o.id !== offerId));
        removeNotificationsByOfferId(offerId);
    };

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
                                isHighlighted={highlightedCvId === cv.id}
                                onAccept={handleAccept}
                                onDecline={handleDecline}
                            />
                        ))}
                    </div>
                )}
                <h2 className="text-xl font-bold text-[#4b1113] mt-8">{t("gestionnaire.offres.title")}</h2>

                {offersStatus === "loading" && (
                    <p className="text-[#4b1113]/70">{t("gestionnaire.offres.loading")}</p>
                )}
                {offersStatus === "error" && (
                    <p className="text-red-700" role="alert">{t("gestionnaire.offres.error")}</p>
                )}
                {offersStatus === "loaded" && pendingOffers.length === 0 && (
                    <p className="text-[#4b1113]/70">{t("gestionnaire.offres.none")}</p>
                )}
                {offersStatus === "loaded" && pendingOffers.length > 0 && (
                    <div className="w-full max-w-3xl flex flex-col gap-3">
                        {pendingOffers.map((offer) => (
                            <ManagerJobOfferListItem
                                key={offer.id}
                                offer={offer}
                                isHighlighted={highlightedOfferId === offer.id}
                                onAccept={handleAcceptOffer}
                                onRefuse={handleRefuseOffer}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default PersonalSpaceManager;
