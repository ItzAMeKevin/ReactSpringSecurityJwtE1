import { useTranslation } from "react-i18next";
import UserFields from "./UserFields";

const InscriptionForm = ({ role, errors, programmes, handleSubmit, handleChange }) => {
  const { t } = useTranslation();

  return (
    <form onSubmit={handleSubmit} onChange={handleChange}>
      <div className="flex flex-col gap-4">
        <UserFields role={role} errors={errors} programmes={programmes} />
      </div>
      <div>
        <button type="submit" className="btn-primary mt-6">
          {t("inscription.submit")}
        </button>
      </div>
    </form>
  );
};

export default InscriptionForm;
