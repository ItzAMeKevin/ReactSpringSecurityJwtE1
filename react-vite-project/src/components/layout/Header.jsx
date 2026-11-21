import React from "react";
import '@/components/layout/Header.css';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import NotificationBell from "@/components/layout/NotificationBell.jsx";
import UserMenu from "@/components/layout/UserMenu.jsx";
import LanguageSwitcher from "@/components/layout/LanguageSwitcher.jsx";
import roseLogo from "@/assets/img/rose1.svg";

function Header({user}) {
    const { t } = useTranslation();
    const isLoggedIn = user?.isLoggedIn;

    return (
        <header className="header">
            <div className="brand">
                <img src={roseLogo} alt="Logo" className="logo" height={40} width={40}/>
                <h1 className="hidden md:block">{t("header.title")}</h1>
            </div>
            <nav className="flex items-center">
                <ul className="nav-links">
                    <li><Link to="/">{t("header.accueil")}</Link></li>
                    {!isLoggedIn && <li><Link to="/inscription">{t("header.inscription")}</Link></li>}
                    <li><Link to="/about">{t("header.about")}</Link></li>
                    {!isLoggedIn && <li><Link to="/login">{t("header.login")}</Link></li>}
                    {!isLoggedIn && <li><LanguageSwitcher activeClassName="font-bold underline" /></li>}
                    {isLoggedIn && <li><NotificationBell /></li>}
                </ul>
                {isLoggedIn && <UserMenu user={user} />}
            </nav>
        </header>
    );
}

export default Header;
