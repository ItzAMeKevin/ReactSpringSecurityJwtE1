import { useTranslation } from "react-i18next";
import CandidatureTrackingItem from "./CandidatureTrackingItem.jsx";

const CandidatureTrackingList = ({ candidatures }) => {
    const { t } = useTranslation();

    return (
        <div className="overflow-x-auto rounded-lg border border-brand/20 bg-white/40">
            <table className="w-full">
                <thead>
                    <tr className="border-b border-brand/20 bg-brand/5">
                        <th className="px-4 py-3 text-left font-semibold text-main">
                            {t("candidatureTracking.table.student")}
                        </th>
                        <th className="px-4 py-3 text-left font-semibold text-main">
                            {t("candidatureTracking.table.company")}
                        </th>
                        <th className="px-4 py-3 text-left font-semibold text-main">
                            {t("candidatureTracking.table.position")}
                        </th>
                        <th className="px-4 py-3 text-left font-semibold text-main">
                            {t("candidatureTracking.table.status")}
                        </th>
                        <th className="px-4 py-3 text-left font-semibold text-main">
                            {t("candidatureTracking.table.lastModified")}
                        </th>
                        <th className="px-4 py-3 text-center font-semibold text-main">
                            {t("candidatureTracking.table.actions")}
                        </th>
                    </tr>
                </thead>
                <tbody>
                    {candidatures.map((candidature, index) => (
                        <CandidatureTrackingItem 
                            key={candidature.id || index} 
                            candidature={candidature} 
                        />
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default CandidatureTrackingList;
