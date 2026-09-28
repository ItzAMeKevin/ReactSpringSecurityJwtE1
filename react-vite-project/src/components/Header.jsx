import React from "react";
import './Header.css';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

function Header({user}) {
    const { t, i18n } = useTranslation();

    // Function to format role for display (remove ROLE_ prefix and capitalize)
    const formatRole = (roleString) => {
        if (!roleString) return '';
        // Remove ROLE_ prefix if present
        const roleName = roleString.replace('ROLE_', '');
        // Capitalize first letter, lowercase the rest
        return roleName.charAt(0).toUpperCase() + roleName.slice(1).toLowerCase();
    };

    const isManager = () => user?.role === 'ROLE_MANAGER';
    const isEmployer = () => user?.role === 'ROLE_EMPLOYER';
    const isStudent = () => user?.role === 'ROLE_STUDENT';

    return (
        <header className="header">
            <h1>{t("header.title")}</h1>
            <nav>
                <ul className="nav-links">
                    <li><Link to="/">{t("header.accueil")}</Link></li>
                    <li><Link to="/about">{t("header.about")}</Link></li>
                    {isStudent() && <li><Link to="/etudiant">{t("header.etudiant")}</Link></li>}
                    {isEmployer() && <li><Link to="/employeur">{t("header.employeur")}</Link></li>}
                    {isManager() && <li><Link to="/gestionnaire">{t("header.gestionnaire")}</Link></li>}
                    {!user?.isLoggedIn && <li><Link to="/inscription">{t("header.inscription")}</Link></li>}
                    <li>{user?.isLoggedIn ? <Link to="/logout">{t("header.logout")}</Link> : <Link to="/login">{t("header.login")}</Link>}</li>
                    {["fr", "en"].map((lang) => (
                        <li key={lang}>
                            <button
                                type="button"
                                onClick={() => i18n.changeLanguage(lang)}
                                className={i18n.language === lang ? "font-bold underline" : ""}
                                style={{ background: "none", border: "none", color: "white", cursor: "pointer", fontSize: "inherit" }}
                            >
                                {lang.toUpperCase()}
                            </button>
                        </li>
                    ))}
                </ul>
                {user?.isLoggedIn && (
                    <div className="user-info">
                        <p className="para-align">
                            {t("header.greeting")} <span className="user-name">{user.firstName} {user.lastName}</span>
                            {user.role && (
                                <span className="user-role"> - {formatRole(user.role.toString())}</span>
                            )}
                        </p>
                    </div>
                )}
            </nav>
        </header>
    );
}

export default Header;