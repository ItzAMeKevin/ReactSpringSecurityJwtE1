import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";
import ManagerJobOfferListItem from "@/dashboards/manager/components/ManagerJobOfferListItem.jsx";
import useHighlightWhenNotified from "@/hooks/useHighlightWhenNotified.js";

const ManagerOfferSection = () => {
    const { t } = useTranslation();
    const { offers, acceptOffer, refuseOffer } = useOutletContext();
    const { highlightProps } = useHighlightWhenNotified();

    return (
        <DashboardSection
            title={t("gestionnaire.offres.title")}
            status={offers.status}
            isEmpty={offers.items.length === 0}
            loadingText={t("gestionnaire.offres.loading")}
            errorText={t("gestionnaire.offres.error")}
            emptyText={t("gestionnaire.offres.none")}
        >
            {offers.items.map((offer) => (
                <ManagerJobOfferListItem
                    key={offer.id}
                    offer={offer}
                    {...highlightProps("offer", offer.id)}
                    onAccept={acceptOffer}
                    onRefuse={refuseOffer}
                />
            ))}
        </DashboardSection>
    );
};

export default ManagerOfferSection;
