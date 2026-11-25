import { useEffect } from "react";
import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";
import SessionFilter from "@/components/ui/SessionFilter.jsx";
import StudentJobOfferListItem from "@/dashboards/student/components/StudentJobOfferListItem.jsx";
import useSessionFilter from "@/hooks/useSessionFilter.js";
import useHighlightWhenNotified from "@/hooks/useHighlightWhenNotified.js";
import { getSessionCode } from "@/utils/session.js";
import { textMuted } from "@/styles/classes.js";

const StudentOfferSection = () => {
    const { t } = useTranslation();
    const { offers, acceptedCvs } = useOutletContext();
    const { sessions, selectedSession, setSelectedSession, filteredOffers } = useSessionFilter(offers.items);
    const { isHighlighted, highlightProps } = useHighlightWhenNotified();
    const highlightedOffer = offers.items.find((offer) => isHighlighted("offer", offer.id));
    const highlightedSession = highlightedOffer ? getSessionCode(highlightedOffer.startingDate) : null;

    useEffect(() => {
        if (highlightedSession) setSelectedSession(highlightedSession);
    }, [highlightedSession, setSelectedSession]);

    return (
        <DashboardSection
            title={t("etudiantOffres.title")}
            status={offers.status}
            isEmpty={offers.items.length === 0}
            loadingText={t("etudiantOffres.loading")}
            errorText={t("errors.generic")}
            emptyText={t("etudiantOffres.noOffers")}
        >
            {acceptedCvs.length === 0 && (
                <p className="rounded-md border border-amber-200 bg-amber-50 px-3 py-2 text-sm text-amber-900" role="status">
                    {t("etudiantOffres.noValidatedCv")}
                </p>
            )}
            <SessionFilter sessions={sessions} value={selectedSession} onChange={setSelectedSession} />
            {filteredOffers.length === 0 ? (
                <p className={`${textMuted} text-sm`}>{t("etudiantOffres.noOffersForSession")}</p>
            ) : (
                <ul className="grid w-full grid-cols-1 items-start gap-4 lg:grid-cols-2">
                    {filteredOffers.map((offer, i) => (
                        <StudentJobOfferListItem
                            key={offer.id ?? `fallback-${i}`}
                            offer={offer}
                            acceptedCvs={acceptedCvs}
                            {...highlightProps("offer", offer.id)}
                        />
                    ))}
                </ul>
            )}
        </DashboardSection>
    );
};

export default StudentOfferSection;
