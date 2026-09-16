import { useTranslation } from "react-i18next";
import Field, { labelClass } from "./Field";

const StudentFields = ({ errors, programmes }) => {
    const { t } = useTranslation();

    return (
        <>
            <Field label={t("inscription.fields.matricule")} name="matricule" error={errors.matricule} />
            <div className="flex flex-col">
                <label className={labelClass}>{t("inscription.fields.programme")}</label>
                <div className="relative">
                    <select
                        id="programmes"
                        name="programme"
                        className={`w-full rounded-md border px-3 py-2 pr-28 focus:outline-none focus:ring-2 focus:ring-[#4b1113] text-[#2b1a12] ${
                            errors.programme ? "bg-red-50 border-red-400" : "bg-white border-[#5c4432]"
                        }`}
                    >
                        <option value="" disabled hidden>{t("inscription.fields.programmePlaceholder")}</option>
                        {programmes.map((programme) => (
                            <option value={programme} key={programme}>{programme}</option>
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
