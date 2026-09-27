import { useTranslation } from "react-i18next";

const RoleSelector = ({ role, setRole }) => {
  const { t } = useTranslation();

  return (
    <div className="flex flex-col mb-4">
      <label className="form-label">{t("inscription.roleLabel")}</label>
      <select
        id="role"
        value={role ?? ""}
        onChange={(e) => setRole(e.target.value)}
        className="form-input"
      >
        <option value="" disabled hidden>{t("inscription.rolePlaceholder")}</option>
        <option value="student">{t("inscription.roles.student")}</option>
        <option value="manager">{t("inscription.roles.manager")}</option>
        <option value="employer">{t("inscription.roles.employer")}</option>
      </select>
    </div>
  );
};

export default RoleSelector;
