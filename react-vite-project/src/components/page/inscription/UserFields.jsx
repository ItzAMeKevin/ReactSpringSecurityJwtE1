import Field from "../Field";
import StudentFields from "../StudentFields";

const UserFields = ({ role, errors, programmes }) => (
  <>
    <Field label="Nom" name="nom" error={errors.nom} />
    <Field label="Prenom" name="prenom" error={errors.prenom} />
    <Field label="Courriel" name="courriel" type="email" error={errors.courriel} />
    <Field label="Mot de passe" name="motDePasse" type="password" error={errors.motDePasse} />
    <Field label="Confirmation de mot de passe" name="confirmation" type="password" error={errors.confirmation} />
    {role === "student" && <StudentFields errors={errors} programmes={programmes} />}
  </>
);

export default UserFields;
