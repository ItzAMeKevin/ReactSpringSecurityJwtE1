import {useState} from "react";
import {useTranslation} from "react-i18next";
import fetcher from "../../../utils/fetcher.js";

const inputClass = "w-full border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]";
const labelClass = "block text-[#4b1113] font-bold mb-2";

const FormJobOffer = ({onSuccess}) => {

    const {t} = useTranslation();
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState(null);

    const programs = t("inscription.programsList", {returnObjects: true});

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitting(true);
        setError(null);

        const fd = new FormData(e.target);
        const body = {
            title: fd.get("title"),
            description: fd.get("description"),
            prerequisites: fd.get("requirements"),
            salary: fd.get("salary"),
            startingDate: fd.get("startingDate"),
            durationInWeeks: parseInt(fd.get("durationInWeeks"), 10),
            programe: fd.get("programe"),
            adresse: {
                pays: fd.get("country"),
                ville: fd.get("city"),
                rue: fd.get("street"),
                numeroCivic: fd.get("civicNumber"),
                codePostal: fd.get("postalCode"),
            },
        };

        try {
            const res = await fetcher("/employer/addJobOffer", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(body),
            });
            if (!res.ok) throw new Error(`${res.status}`);
            if (onSuccess) onSuccess();
        } catch {
            setError(t("errors.generic"));
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            {error && <p className="text-red-600 mb-4">{error}</p>}
            <div className="space-y-4">
                <div>
                    <label htmlFor="title" className={labelClass}>{t("personalSpaceEmployer.fields.title")}</label>
                    <input type="text" id="title" name="title" required className={inputClass}/>
                </div>
                <div>
                    <label htmlFor="description" className={labelClass}>{t("personalSpaceEmployer.fields.description")}</label>
                    <textarea id="description" name="description" required className={`min-h-24 resize-y ${inputClass}`}/>
                </div>
                <div>
                    <label htmlFor="requirements" className={labelClass}>{t("personalSpaceEmployer.fields.requirements")}</label>
                    <textarea id="requirements" name="requirements" required className={`min-h-24 resize-y ${inputClass}`}/>
                </div>
                <div>
                    <label htmlFor="salary" className={labelClass}>{t("personalSpaceEmployer.fields.salary")}</label>
                    <input type="text" id="salary" name="salary" className={inputClass}/>
                </div>
                <div>
                    <label htmlFor="startingDate" className={labelClass}>{t("personalSpaceEmployer.fields.startingDate")}</label>
                    <input type="date" id="startingDate" name="startingDate" required className={inputClass}/>
                </div>
                <div>
                    <label htmlFor="durationInWeeks" className={labelClass}>{t("personalSpaceEmployer.fields.durationInWeeks")}</label>
                    <input type="number" id="durationInWeeks" name="durationInWeeks" min="1" required className={inputClass}/>
                </div>
                <div>
                    <label htmlFor="programe" className={labelClass}>{t("personalSpaceEmployer.fields.programe")}</label>
                    <select id="programe" name="programe" required className={inputClass}>
                        <option value="">{t("inscription.rolePlaceholder")}</option>
                        {programs.map((p) => (
                            <option key={p.enumName} value={p.enumName}>{p.name}</option>
                        ))}
                    </select>
                </div>
                <h2 className="text-[#4b1113] font-bold mb-2">{t("personalSpaceEmployer.fields.address")}</h2>
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <div>
                        <label htmlFor="city" className={labelClass}>{t("personalSpaceEmployer.fields.city")}</label>
                        <input type="text" id="city" name="city" required className={inputClass}/>
                    </div>
                    <div>
                        <label htmlFor="country" className={labelClass}>{t("personalSpaceEmployer.fields.country")}</label>
                        <input type="text" id="country" name="country" className={inputClass}/>
                    </div>
                    <div>
                        <label htmlFor="postalCode" className={labelClass}>{t("personalSpaceEmployer.fields.postalCode")}</label>
                        <input type="text" id="postalCode" name="postalCode" required className={inputClass}/>
                    </div>
                    <div>
                        <label htmlFor="civicNumber" className={labelClass}>{t("personalSpaceEmployer.fields.civicNumber")}</label>
                        <input type="text" id="civicNumber" name="civicNumber" required className={inputClass}/>
                    </div>
                    <div className="sm:col-span-2">
                        <label htmlFor="street" className={labelClass}>{t("personalSpaceEmployer.fields.street")}</label>
                        <input type="text" id="street" name="street" required className={inputClass}/>
                    </div>
                </div>
                <button
                    type="submit"
                    disabled={submitting}
                    className="bg-[#4b1113] text-white px-4 py-2 rounded hover:bg-[#6b1c1f] transition-colors duration-300 disabled:opacity-50"
                >
                    {submitting ? "..." : t("personalSpaceEmployer.submit")}
                </button>
            </div>
        </form>
    );
}

export default FormJobOffer;
