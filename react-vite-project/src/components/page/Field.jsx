import { useTranslation } from "react-i18next";

export const labelClass = "form-label";

const Field = ({
  label,
  name,
  type = "text",
  error,
  placeholder,
  required,
  inputMode,
  pattern,
  min,
  step,
  maxLength,
  as = "input",
  suffix,
  className,
  defaultValue,
  children,
}) => {
  const { t } = useTranslation();
  const controlClassName = `${className || (error ? "form-input-error" : "form-input")} ${suffix ? "pr-12" : ""}`;
  const controlProps = {
    id: name,
    name,
    placeholder,
    required,
    inputMode,
    pattern,
    min,
    step,
    maxLength,
    defaultValue,
    className: controlClassName,
  };

  const control = as === "textarea"
    ? <textarea {...controlProps}>{children}</textarea>
    : as === "select"
      ? <select {...controlProps}>{children}</select>
      : <input {...controlProps} type={type} />;

  return (
    <div className="flex flex-col">
      <label htmlFor={name} className={labelClass}>{label}</label>
      <div className={suffix ? "relative" : ""}>
        {control}
        {suffix && (
          <span className="pointer-events-none absolute inset-y-0 right-3 flex items-center text-gray-500">
            {suffix}
          </span>
        )}
      </div>
      {error && (
        <p className="mt-1 text-sm text-red-600" role="alert">{t(error)}</p>
      )}
    </div>
  );
};

export default Field;
