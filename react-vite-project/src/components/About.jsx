import { useTranslation } from "react-i18next";

function About() {
  const { t, i18n } = useTranslation();

  const roles = ["student", "employer", "manager"];

  return (
    <div className="auth-page flex flex-col items-center px-4 py-12">
      <div
        className="pointer-events-none absolute inset-0 opacity-20"
        style={{
          backgroundImage: "radial-gradient(var(--color-mint-dots) 1.6px, transparent 1.7px)",
          backgroundSize: "18px 18px",
        }}
      />

      <div className="relative z-10 w-full max-w-3xl flex flex-col gap-8">
        <div className="flex justify-end gap-2">
          {["fr", "en"].map((lang) => (
            <button
              key={lang}
              type="button"
              onClick={() => i18n.changeLanguage(lang)}
              className={`lang-btn ${i18n.language === lang ? "font-bold underline" : ""}`}
            >
              {lang.toUpperCase()}
            </button>
          ))}
        </div>

        <div className="auth-card text-center">
          <h1 className="text-4xl font-bold text-mint mb-1">ZeROSE</h1>
          <p className="text-sm text-slate/60 mb-6">{t("about.subtitle")}</p>
          <h2 className="auth-title mb-3">{t("about.missionTitle")}</h2>
          <p className="text-slate/80 leading-relaxed">{t("about.missionText")}</p>
        </div>

        <div>
          <h2 className="auth-title text-center mb-4">{t("about.whoTitle")}</h2>
          <div className="grid md:grid-cols-3 gap-4">
            {roles.map((key) => (
              <div key={key} className="auth-card flex flex-col gap-2">
                <div className="h-1 w-10 bg-mint rounded-full mb-1" />
                <h3 className="font-bold text-slate">{t(`about.roles.${key}.title`)}</h3>
                <p className="text-sm text-slate/70 leading-relaxed">{t(`about.roles.${key}.desc`)}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default About;
