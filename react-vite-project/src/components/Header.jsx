import React from "react";
import './Header.css';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import NotificationBell from "./NotificationBell.jsx";
import ManagerNotificationBell from "./ManagerNotificationBell.jsx";
import roseLogo from "../utils/img/rose1.svg";

function Header({user}) {
    const { t, i18n } = useTranslation();

    const isManager = () => user?.role === 'ROLE_MANAGER';
    const isEmployer = () => user?.role === 'ROLE_EMPLOYER';
    const isStudent = () => user?.role === 'ROLE_STUDENT';

    return (
        <header className="header">
            <div className="brand">
                <img src={roseLogo} alt="Logo" className="logo" height={40} width={40}/>
                <h1>{t("header.title")}</h1>
            </div>
            <nav style={{ display: "flex", alignItems: "center" }}>
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
                                style={{ background: "none", border: "none", color: "inherit", cursor: "pointer", fontSize: "inherit" }}
                            >
                                {lang.toUpperCase()}
                            </button>
                        </li>
                    ))}
                    {user?.isLoggedIn && isStudent() && <li><NotificationBell /></li>}
                    {user?.isLoggedIn && isManager() && <li><ManagerNotificationBell /></li>}
                </ul>
                {user?.isLoggedIn && (
                    <p className="user-greeting">
                        {t("header.greeting")} <span className="user-name">{user.firstName} {user.lastName}</span>
                        {user.role && (
                            <span style={{ opacity: 0.6 }}> - {t(`header.roles.${user.role.replace('ROLE_', '')}`)}</span>
                        )}
                    </p>
                )}
            </nav>
        </header>
    );
}

export default Header;
