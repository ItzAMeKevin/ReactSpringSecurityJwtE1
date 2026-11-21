import { useTranslation } from "react-i18next";

const LANGUAGES = ["fr", "en"];

const LanguageSwitcher = ({ activeClassName, idleClassName = "" }) => {
    const { i18n } = useTranslation();

    return (
        <div className="flex items-center gap-2">
            {LANGUAGES.map((lang) => (
                <button
                    key={lang}
                    type="button"
                    onClick={() => i18n.changeLanguage(lang)}
                    className={i18n.language === lang ? activeClassName : idleClassName}
                >
                    {lang.toUpperCase()}
                </button>
            ))}
        </div>
    );
};

export default LanguageSwitcher;
