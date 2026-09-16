import { useTranslation } from "react-i18next";

export const labelClass = "mb-1 font-medium text-[#2b1a12]";

const Field = ({ label, name, type = "text", error }) => {
  const { t } = useTranslation();

  return (
    <div className="flex flex-col">
      <label className={labelClass}>{label}</label>
      <div className="relative">
        <input
          type={type}
          name={name}
          className={`w-full rounded-md border px-3 py-2 pr-28 focus:outline-none focus:ring-2 focus:ring-[#4b1113] text-[#2b1a12] ${
            error ? "bg-red-50 border-red-400" : "bg-white border-[#5c4432]"
          }`}
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
