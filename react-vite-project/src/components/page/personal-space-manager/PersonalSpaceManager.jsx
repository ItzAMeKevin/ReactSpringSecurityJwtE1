import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import fetcher from "../../../utils/fetcher.js";
import ManagerCvListItem from "./ManagerCvListItem.jsx";
import ManagerJobOfferListItem from "./ManagerJobOfferListItem.jsx";
import useHighlightWhenNotified from "../../../hooks/useHighlightWhenNotified.js";
import { pageWrapper, pageTitle, sectionTitle, textMuted, textError } from "../../../styles/classes.js";
import { getApiErrorKey } from "../../../utils/apiError.js";

const PersonalSpaceManager = () => {
    const { t } = useTranslation();
    const { isHighlighted, clearHighlight } = useHighlightWhenNotified();
    const [pendingCvs, setPendingCvs] = useState([]);
    const [status, setStatus] = useState("loading");
    const [pendingOffers, setPendingOffers] = useState([]);
    const [offersStatus, setOffersStatus] = useState("loading");

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
            throw new Error(await getApiErrorKey(response));
        }
        removeFromList(cvId);
    });

    const handleDecline = (async (cvId, reviewPayload) => {
        const response = await fetcher(`/gestionnaire/cv/${cvId}/decline`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(reviewPayload),
        });
        if (!response.ok) throw new Error(await getApiErrorKey(response));
        removeFromList(cvId);
    });

    const handleAcceptOffer = async (offerId) => {
        const response = await fetcher(`/gestionnaire/offres/${offerId}/accept`, { method: "PUT" });
        if (!response.ok) throw new Error(await getApiErrorKey(response));
        setPendingOffers((current) => current.filter((o) => o.id !== offerId));
    };

    const handleRefuseOffer = async (offerId) => {
        const response = await fetcher(`/gestionnaire/offres/${offerId}/refuse`, { method: "PUT" });
        if (!response.ok) throw new Error(await getApiErrorKey(response));
        setPendingOffers((current) => current.filter((o) => o.id !== offerId));
    };

    const isLoaded = status === "loaded";

    return (
        <div className={pageWrapper}>
            <div className="flex flex-col items-center gap-8 pt-20 pb-20 px-4">
                <h1 className={pageTitle}>{t("gestionnaire.title")}</h1>

                <section className="w-full max-w-3xl">
                    <h2 className={sectionTitle}>{t("gestionnaire.cvSection")}</h2>
                    {status === "loading" && (
                        <p className={textMuted}>{t("gestionnaire.loadingCvs")}</p>
                    )}
                    {status === "error" && (
                        <p className={textError} role="alert">{t("gestionnaire.errorLoadingCvs")}</p>
                    )}
                    {isLoaded && pendingCvs.length === 0 && (
                        <p className={textMuted}>{t("gestionnaire.noCvsPending")}</p>
                    )}
                    {isLoaded && pendingCvs.length > 0 && (
                        <div className="flex flex-col gap-3">
                            {pendingCvs.map((cv) => (
                                <ManagerCvListItem
                                    key={cv.id}
                                    cv={cv}
                                    isHighlighted={isHighlighted("cv", cv.id)}
                                    onClearHighlight={clearHighlight}
                                    onAccept={handleAccept}
                                    onDecline={handleDecline}
                                />
                            ))}
                        </div>
                    )}
                </section>

                <section className="w-full max-w-3xl">
                    <h2 className={sectionTitle}>{t("gestionnaire.offres.title")}</h2>
                    {offersStatus === "loading" && (
                        <p className={textMuted}>{t("gestionnaire.offres.loading")}</p>
                    )}
                    {offersStatus === "error" && (
                        <p className={textError} role="alert">{t("gestionnaire.offres.error")}</p>
                    )}
                    {offersStatus === "loaded" && pendingOffers.length === 0 && (
                        <p className={textMuted}>{t("gestionnaire.offres.none")}</p>
                    )}
                    {offersStatus === "loaded" && pendingOffers.length > 0 && (
                        <div className="flex flex-col gap-3">
                            {pendingOffers.map((offer) => (
                                <ManagerJobOfferListItem
                                    key={offer.id}
                                    offer={offer}
                                    isHighlighted={isHighlighted("offer", offer.id)}
                                    onClearHighlight={clearHighlight}
                                    onAccept={handleAcceptOffer}
                                    onRefuse={handleRefuseOffer}
                                />
                            ))}
                        </div>
                    )}
                </section>
            </div>
        </div>
    );
};

export default PersonalSpaceManager;
