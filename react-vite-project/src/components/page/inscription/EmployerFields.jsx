import { useTranslation } from "react-i18next";
import Field from "../Field.jsx";

const EmployerFields = ({ errors}) => {
    const { t } = useTranslation();

    return (
        <>
            <Field label={t("inscription.fields.nomEntreprise")} name="nomEntreprise" error={errors.nomEntreprise} />
            <Field label={t("inscription.fields.adresse")} name="adresse" error={errors.adresse} />
            <Field label={t("inscription.fields.codePostal")} name="codePostal" error={errors.codePostal} />
            <Field label={t("inscription.fields.ville")} name="ville" error={errors.ville} />
            <Field label={t("inscription.fields.telephone")} name="telephone" error={errors.telephone} />
            <Field label={t("inscription.fields.identifiant")} name="identifiant" error={errors.identifiant} />
        </>
    )
}

export default EmployerFields;
