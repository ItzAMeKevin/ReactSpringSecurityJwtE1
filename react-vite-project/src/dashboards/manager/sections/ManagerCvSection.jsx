import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";
import ManagerCvListItem from "@/dashboards/manager/components/ManagerCvListItem.jsx";
import useHighlightWhenNotified from "@/hooks/useHighlightWhenNotified.js";

const ManagerCvSection = () => {
    const { t } = useTranslation();
    const { cvs, acceptCv, declineCv } = useOutletContext();
    const { isHighlighted, clearHighlight } = useHighlightWhenNotified();

    return (
        <DashboardSection
            title={t("gestionnaire.cvSection")}
            status={cvs.status}
            isEmpty={cvs.items.length === 0}
            loadingText={t("gestionnaire.loadingCvs")}
            errorText={t("gestionnaire.errorLoadingCvs")}
            emptyText={t("gestionnaire.noCvsPending")}
        >
            {cvs.items.map((cv) => (
                <ManagerCvListItem
                    key={cv.id}
                    cv={cv}
                    isHighlighted={isHighlighted("cv", cv.id)}
                    onClearHighlight={clearHighlight}
                    onAccept={acceptCv}
                    onDecline={declineCv}
                />
            ))}
        </DashboardSection>
    );
};

export default ManagerCvSection;
