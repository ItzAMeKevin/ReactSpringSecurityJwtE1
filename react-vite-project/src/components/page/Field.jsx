import { useTranslation } from "react-i18next";

export const labelClass = "form-label";

const Field = ({ label, name, type = "text", error }) => {
  const { t } = useTranslation();

  return (
    <div className="flex flex-col">
      <label className={labelClass}>{label}</label>
      <div className="relative">
        <input
          type={type}
          name={name}
          className={`${error ? "form-input-error" : "form-input"} pr-28`}
        />
        {error && (
          <span className="absolute inset-y-0 right-3 flex items-center text-red-800 text-xs font-semibold pointer-events-none">
            {t(error)}
          </span>
        )}
      </div>
    </div>
  );
};

export default Field;
