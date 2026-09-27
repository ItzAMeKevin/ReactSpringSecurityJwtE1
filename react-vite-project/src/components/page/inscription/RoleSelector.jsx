import { useTranslation } from "react-i18next";

const RoleSelector = ({ role, setRole }) => {
  const { t } = useTranslation();

  return (
    <div className="flex flex-col mb-4">
      <label className="mb-1 font-medium text-[#2b1a12]">{t("inscription.roleLabel")}</label>
      <select
        id="role"
        value={role ?? ""}
        onChange={(e) => setRole(e.target.value)}
        className="
            rounded-md
            border
            border-[#5c4432]
            bg-white
            px-3
            py-2
            text-[#2b1a12]
            focus:outline-none
            focus:ring-2
           focus:ring-[#4b1113]"
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
