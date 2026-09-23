import { useTranslation } from "react-i18next";
import Field from "../Field.jsx";

const ManagerFields = ({ errors }) => {
    const { t } = useTranslation();

    return (
        <Field label={t("inscription.fields.matricule")} name="matricule" error={errors.matricule} />
    );
};

export default ManagerFields;
