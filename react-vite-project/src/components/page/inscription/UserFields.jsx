import { useTranslation } from "react-i18next";
import Field from "../Field";
import StudentFields from "./StudentFields.jsx";
import ManagerFields from "./ManagerFields.jsx";
import EmployerFields from "./EmployerFields.jsx";

const UserFields = ({ role, errors, programmes }) => {
  const { t } = useTranslation();

  return (
    <>
      {role !== "employer" && (
        <>
            <Field label={t("inscription.fields.nom")} name="nom" error={errors.nom} />
            <Field label={t("inscription.fields.prenom")} name="prenom" error={errors.prenom} />
        </>
      )}
      <Field label={t("inscription.fields.courriel")} name="courriel" type="email" error={errors.courriel} />
      <Field label={t("inscription.fields.motDePasse")} name="motDePasse" type="password" error={errors.motDePasse} />
      <Field label={t("inscription.fields.confirmation")} name="confirmation" type="password" error={errors.confirmation} />
      {role === "student" && <StudentFields errors={errors} programmes={programmes} />}
      {role === "manager" && <ManagerFields errors={errors} />}
      {role === "employer" && <EmployerFields errors={errors} />}
    </>
  );
};

export default UserFields;
