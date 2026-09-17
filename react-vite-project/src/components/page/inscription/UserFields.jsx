import { useTranslation } from "react-i18next";
import Field from "../Field";
import StudentFields from "../StudentFields";

const UserFields = ({ role, errors, programmes }) => {
  const { t } = useTranslation();

  return (
    <>
      <Field label={t("inscription.fields.nom")} name="nom" error={errors.nom} />
      <Field label={t("inscription.fields.prenom")} name="prenom" error={errors.prenom} />
      <Field label={t("inscription.fields.courriel")} name="courriel" type="email" error={errors.courriel} />
      <Field label={t("inscription.fields.motDePasse")} name="motDePasse" type="password" error={errors.motDePasse} />
      <Field label={t("inscription.fields.confirmation")} name="confirmation" type="password" error={errors.confirmation} />
      {(role === "student" || role === "manager") && (
        <Field label={t("inscription.fields.matricule")} name="matricule" error={errors.matricule} />
      )}
      {role === "student" && <StudentFields errors={errors} programmes={programmes} />}
    </>
  );
};

export default UserFields;
