import {useState} from "react";
import {useTranslation} from "react-i18next";
import fetcher from "../../../utils/fetcher.js";
import Field from "../Field.jsx";

const inputClass = "w-full border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]";

const getToday = () => new Date().toLocaleDateString("en-CA");

const FormJobOffer = ({offer, onSuccess}) => {

    const {t} = useTranslation();
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState(null);
    const [fieldErrors, setFieldErrors] = useState({});

    const isEdit = Boolean(offer);
    const programs = t("inscription.programsList", {returnObjects: true});

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        const fd = new FormData(e.target);
        const requiredFields = [
            "title", "description", "requirements", "salary", "startingDate",
            "durationInWeeks", "programe", "city", "country", "postalCode",
            "civicNumber", "street",
        ];
        const validationErrors = {};

        requiredFields.forEach((field) => {
            if (!String(fd.get(field) ?? "").trim()) validationErrors[field] = "errors.required";
        });

        const duration = Number(fd.get("durationInWeeks"));
        if (fd.get("durationInWeeks") && (!Number.isInteger(duration) || duration < 1)) {
            validationErrors.durationInWeeks = "errors.positiveNumber";
        }

        const salary = String(fd.get("salary") ?? "").trim();
        if (salary && !/^\d+(\.\d{1,2})?$/.test(salary)) {
            validationErrors.salary = "errors.numberOnly";
        }

        const postalCode = String(fd.get("postalCode") ?? "").trim();
        if (postalCode && !/^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/.test(postalCode)) {
            validationErrors.postalCode = "errors.postalCodeFormat";
        }

        const civicNumber = String(fd.get("civicNumber") ?? "").trim();
        if (civicNumber && !/^\d+$/.test(civicNumber)) {
            validationErrors.civicNumber = "errors.digitsOnly";
        }

        const startingDate = fd.get("startingDate");
        if (startingDate && startingDate < getToday()) {
            validationErrors.startingDate = "errors.startingDatePast";
        }

        setFieldErrors(validationErrors);
        if (Object.keys(validationErrors).length > 0) return;

        setSubmitting(true);
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
            const res = await fetcher(
                isEdit ? `/employer/updateJobOffer/${offer.id}` : "/employer/addJobOffer",
                {
                    method: isEdit ? "PUT" : "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(body),
                },
            );
            if (res.status === 409) {
                setError(t("errors.offerNotEditable"));
                return;
            }
            if (!res.ok) throw new Error(`${res.status}`);
            onSuccess?.();
        } catch {
            setError(t("errors.generic"));
        } finally {
            setSubmitting(false);
        }
    };

    const location = offer?.location;

    return (
        <form onSubmit={handleSubmit} noValidate>
            {error && <p className="text-red-600 mb-4" role="alert">{error}</p>}
            <div className="space-y-4">
                <div>
                    <Field label={t("personalSpaceEmployer.fields.title")} name="title" required placeholder={t("personalSpaceEmployer.placeholders.title")} defaultValue={offer?.title} error={fieldErrors.title} className={inputClass}/>
                </div>
                <div>
                    <Field label={t("personalSpaceEmployer.fields.description")} name="description" as="textarea" required placeholder={t("personalSpaceEmployer.placeholders.description")} defaultValue={offer?.description} className={`min-h-24 resize-y ${inputClass}`} error={fieldErrors.description}/>
                </div>
                <div>
                    <Field label={t("personalSpaceEmployer.fields.requirements")} name="requirements" as="textarea" required placeholder={t("personalSpaceEmployer.placeholders.requirements")} defaultValue={offer?.prerequisites} className={`min-h-24 resize-y ${inputClass}`} error={fieldErrors.requirements}/>
                </div>
                <div>
                    <Field label={t("personalSpaceEmployer.fields.salary")} name="salary" type="number" min="0" step="0.01" required placeholder={t("personalSpaceEmployer.placeholders.salary")} defaultValue={offer?.salary} suffix="/hr" className={inputClass} error={fieldErrors.salary}/>
                </div>
                <div>
                    <Field label={t("personalSpaceEmployer.fields.startingDate")} name="startingDate" type="date" min={getToday()} required placeholder={t("personalSpaceEmployer.placeholders.startingDate")} defaultValue={offer?.startingDate} className={inputClass} error={fieldErrors.startingDate}/>
                </div>
                <div>
                    <Field label={t("personalSpaceEmployer.fields.durationInWeeks")} name="durationInWeeks" type="number" min="1" required placeholder={t("personalSpaceEmployer.placeholders.durationInWeeks")} defaultValue={offer?.durationInWeeks} className={inputClass} error={fieldErrors.durationInWeeks}/>
                </div>
                <div>
                    <Field label={t("personalSpaceEmployer.fields.programe")} name="programe" as="select" required defaultValue={offer?.programe ?? ""} className={inputClass} error={fieldErrors.programe}>
                        <option value="">{t("personalSpaceEmployer.placeholders.programe")}</option>
                        {programs.map((p) => (
                            <option key={p.enumName} value={p.enumName}>{p.name}</option>
                        ))}
                    </Field>
                </div>
                <h2 className="text-[#4b1113] font-bold mb-2">{t("personalSpaceEmployer.fields.address")}</h2>
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <div>
                        <Field label={t("personalSpaceEmployer.fields.city")} name="city" required placeholder={t("personalSpaceEmployer.placeholders.city")} defaultValue={location?.ville} className={inputClass} error={fieldErrors.city}/>
                    </div>
                    <div>
                        <Field label={t("personalSpaceEmployer.fields.country")} name="country" required placeholder={t("personalSpaceEmployer.placeholders.country")} defaultValue={location?.pays} className={inputClass} error={fieldErrors.country}/>
                    </div>
                    <div>
                        <Field label={t("personalSpaceEmployer.fields.postalCode")} name="postalCode" required placeholder={t("personalSpaceEmployer.placeholders.postalCode")} maxLength="7" defaultValue={location?.codePostal} className={inputClass} error={fieldErrors.postalCode}/>
                    </div>
                    <div>
                        <Field label={t("personalSpaceEmployer.fields.civicNumber")} name="civicNumber" required inputMode="numeric" pattern="[0-9]+" placeholder={t("personalSpaceEmployer.placeholders.civicNumber")} defaultValue={location?.numeroCivic} className={inputClass} error={fieldErrors.civicNumber}/>
                    </div>
                    <div className="sm:col-span-2">
                        <Field label={t("personalSpaceEmployer.fields.street")} name="street" required placeholder={t("personalSpaceEmployer.placeholders.street")} defaultValue={location?.rue} className={inputClass} error={fieldErrors.street}/>
                    </div>
                </div>
                <button
                    type="submit"
                    disabled={submitting}
                    className="bg-[#4b1113] text-white px-4 py-2 rounded hover:bg-[#6b1c1f] transition-colors duration-300 disabled:opacity-50"
                >
                    {submitting ? "..." : t(isEdit ? "personalSpaceEmployer.resubmit" : "personalSpaceEmployer.submit")}
                </button>
            </div>
        </form>
    );
}

export default FormJobOffer;
