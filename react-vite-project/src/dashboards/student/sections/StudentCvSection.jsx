import { useState } from "react";
import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import DashboardSection from "@/components/layout/DashboardSection.jsx";
import CvListItem from "@/dashboards/student/components/CvListItem.jsx";
import useHighlightWhenNotified from "@/hooks/useHighlightWhenNotified.js";

const StudentCvSection = ({ status, labelKey, emptyKey }) => {
    const { t } = useTranslation();
    const { cvs, visibleCvs, uploadCv } = useOutletContext();
    const { highlightProps } = useHighlightWhenNotified();
    const [openCvId, setOpenCvId] = useState(null);

    const sectionCvs = visibleCvs.filter((cv) => cv.status === status);

    return (
        <DashboardSection
            title={t(labelKey)}
            status={cvs.status}
            isEmpty={sectionCvs.length === 0}
            loadingText={t("televerser.chargement")}
            errorText={t("televerser.erreurChargement")}
            emptyText={t(emptyKey)}
        >
            {sectionCvs.map((cv) => (
                <CvListItem
                    key={cv.id}
                    cv={cv}
                    isOpen={openCvId === cv.id}
                    onToggle={() => setOpenCvId((current) => (current === cv.id ? null : cv.id))}
                    {...highlightProps("cv", cv.id)}
                    onReplace={uploadCv}
                />
            ))}
        </DashboardSection>
    );
};

export default StudentCvSection;
