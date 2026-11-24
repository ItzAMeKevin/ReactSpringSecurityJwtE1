import { useId } from "react";
import { useTranslation } from "react-i18next";
import { inputField } from "@/styles/classes.js";

const formatSessionLabel = (code, t) => t(`sessionFilter.sessions.${code[0]}`, { year: code.slice(1) });

const SessionFilter = ({ sessions, value, onChange }) => {
    const { t } = useTranslation();
    const selectId = useId();

    return (
        <div className="flex items-center gap-3 w-full max-w-4xl">
            <label htmlFor={selectId} className="text-sm font-medium text-main shrink-0">
                {t("sessionFilter.label")}
            </label>
            <select
                id={selectId}
                value={value}
                onChange={(e) => onChange(e.target.value)}
                className={`${inputField} max-w-xs`}
            >
                {sessions.map((code) => (
                    <option key={code} value={code}>{formatSessionLabel(code, t)}</option>
                ))}
            </select>
        </div>
    );
};

export default SessionFilter;
