import { useTranslation } from "react-i18next";
import Field, { labelClass } from "../Field.jsx";

const StudentFields = ({ errors, programmes }) => {
    const { t } = useTranslation();
    const liste = Array.isArray(programmes) ? programmes : [];
    return (
        <>
            <Field label={t("inscription.fields.matricule")} name="matricule" error={errors.matricule} />
            <div className="flex flex-col">
                <label className={labelClass}>{t("inscription.fields.programme")}</label>
                <div className="relative">
                    <select
                        id="programmes"
                        name="programme"
                        defaultValue=""
                        className={`${errors.programme ? "form-input-error" : "form-input"} pr-28`}
                    >
                        <option value="" disabled hidden>{t("inscription.fields.programmePlaceholder")}</option>
                        {liste.map((p) => (
                            <option value={p.enumName} key={p.enumName}>{p.name}</option>
                        ))}
                    </select>
                    {errors.programme && (
                        <span className="absolute inset-y-0 right-8 flex items-center text-red-800 text-xs font-semibold pointer-events-none">
                            {t(errors.programme)}
                        </span>
                    )}
                </div>
            </div>
        </>
    );
};

export default StudentFields;
