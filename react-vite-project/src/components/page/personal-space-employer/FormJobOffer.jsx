import {useState} from "react";
import {useTranslation} from "react-i18next";
import fetcher from "../../../utils/fetcher.js";
import Field from "../Field.jsx";
import { inputField, btnOutline, btnPrimary } from "../../../styles/classes.js";

const FormJobOffer = ({onSuccess, onCancel}) => {

    const {t} = useTranslation();
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState(null);
    const [fieldErrors, setFieldErrors] = useState({});

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
        if (startingDate && new Date(startingDate) < new Date()) {
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
            const res = await fetcher("/employer/addJobOffer", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(body),
            });
            if (!res.ok) throw new Error(`${res.status}`);
            onSuccess?.();
        } catch {
            setError(t("errors.generic"));
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} noValidate>
            {error && <p className="text-red-600 mb-4">{error}</p>}
            <div className="space-y-4">
                <Field label={t("personalSpaceEmployer.fields.title")} name="title" required placeholder={t("personalSpaceEmployer.placeholders.title")} error={fieldErrors.title} className={inputField}/>
                <Field label={t("personalSpaceEmployer.fields.description")} name="description" as="textarea" required placeholder={t("personalSpaceEmployer.placeholders.description")} className={`min-h-16 resize-y ${inputField}`} error={fieldErrors.description}/>
                <Field label={t("personalSpaceEmployer.fields.requirements")} name="requirements" required placeholder={t("personalSpaceEmployer.placeholders.requirements")} className={inputField} error={fieldErrors.requirements}/>
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <Field label={t("personalSpaceEmployer.fields.salary")} name="salary" type="number" min="0" step="0.01" required placeholder={t("personalSpaceEmployer.placeholders.salary")} suffix="/hr" className={inputField} error={fieldErrors.salary}/>
                    <Field label={t("personalSpaceEmployer.fields.startingDate")} name="startingDate" type="date" required placeholder={t("personalSpaceEmployer.placeholders.startingDate")} className={inputField} error={fieldErrors.startingDate}/>
                </div>
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <Field label={t("personalSpaceEmployer.fields.durationInWeeks")} name="durationInWeeks" type="number" min="1" required placeholder={t("personalSpaceEmployer.placeholders.durationInWeeks")} className={inputField} error={fieldErrors.durationInWeeks}/>
                    <Field label={t("personalSpaceEmployer.fields.programe")} name="programe" as="select" required className={inputField} error={fieldErrors.programe}>
                        <option value="">{t("personalSpaceEmployer.placeholders.programe")}</option>
                        {programs.map((p) => (
                            <option key={p.enumName} value={p.enumName}>{p.name}</option>
                        ))}
                    </Field>
                </div>
                <h2 className="text-brand font-bold mb-2">{t("personalSpaceEmployer.fields.address")}</h2>
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <Field label={t("personalSpaceEmployer.fields.city")} name="city" required placeholder={t("personalSpaceEmployer.placeholders.city")} className={inputField} error={fieldErrors.city}/>
                    <Field label={t("personalSpaceEmployer.fields.country")} name="country" required placeholder={t("personalSpaceEmployer.placeholders.country")} className={inputField} error={fieldErrors.country}/>
                    <Field label={t("personalSpaceEmployer.fields.postalCode")} name="postalCode" required placeholder={t("personalSpaceEmployer.placeholders.postalCode")} maxLength="7" className={inputField} error={fieldErrors.postalCode}/>
                    <Field label={t("personalSpaceEmployer.fields.civicNumber")} name="civicNumber" required inputMode="numeric" pattern="[0-9]+" placeholder={t("personalSpaceEmployer.placeholders.civicNumber")} className={inputField} error={fieldErrors.civicNumber}/>
                    <div className="sm:col-span-2">
                        <Field label={t("personalSpaceEmployer.fields.street")} name="street" required placeholder={t("personalSpaceEmployer.placeholders.street")} className={inputField} error={fieldErrors.street}/>
                    </div>
                </div>
                <div className="flex justify-between gap-2">
                    <button
                        type="button"
                        onClick={onCancel}
                        className={btnOutline}
                    >
                        {t("personalSpaceEmployer.close")}
                    </button>
                    <button
                        type="submit"
                        disabled={submitting}
                        className={`${btnPrimary} disabled:opacity-50`}
                    >
                        {submitting ? "..." : t("personalSpaceEmployer.submit")}
                    </button>
                </div>
            </div>
        </form>
    );
}

export default FormJobOffer;
