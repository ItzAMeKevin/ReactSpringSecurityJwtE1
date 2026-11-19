import { useMemo, useState } from "react";
import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "../../DashboardSection.jsx";
import SessionFilter from "../../SessionFilter.jsx";
import JobOfferCard from "./JobOfferCard.jsx";
import useSessionFilter from "../../../hooks/useSessionFilter.js";
import useHighlightWhenNotified from "../../../hooks/useHighlightWhenNotified.js";
import { textMuted } from "../../../styles/classes.js";

const EmployerOfferSection = ({ statuses, labelKey, emptyKey }) => {
    const { t } = useTranslation();
    const { offers, openForm, hireInOffer } = useOutletContext();
    const { isHighlighted, clearHighlight } = useHighlightWhenNotified();
    const [openCandidatesId, setOpenCandidatesId] = useState(null);

    const sectionOffers = useMemo(
        () => offers.items.filter((offer) => statuses.includes(offer.status)),
        [offers.items, statuses]
    );
    const { sessions, selectedSession, setSelectedSession, filteredOffers } = useSessionFilter(sectionOffers);

    return (
        <DashboardSection
            title={t(labelKey)}
            status={offers.status}
            isEmpty={sectionOffers.length === 0}
            loadingText={t("personalSpaceEmployer.loading")}
            errorText={t("errors.generic")}
            emptyText={t(emptyKey)}
        >
            <SessionFilter sessions={sessions} value={selectedSession} onChange={setSelectedSession} />
            {filteredOffers.length === 0 ? (
                <p className={textMuted}>{t("personalSpaceEmployer.noOffersForSession")}</p>
            ) : (
                <ul className="grid w-full grid-cols-1 gap-4 lg:grid-cols-2 lg:items-start">
                    {filteredOffers.map((offer, i) => (
                        <JobOfferCard
                            key={offer.id ?? i}
                            offer={offer}
                            isHighlighted={isHighlighted("offer", offer.id)}
                            onClearHighlight={clearHighlight}
                            showCandidates={openCandidatesId === offer.id}
                            onToggleCandidates={() => setOpenCandidatesId((current) => (current === offer.id ? null : offer.id))}
                            onEdit={() => openForm(offer)}
                            onHire={() => hireInOffer(offer.id)}
                        />
                    ))}
                </ul>
            )}
        </DashboardSection>
    );
};

export default EmployerOfferSection;
