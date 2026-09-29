import React from "react";
import './Header.css';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

function Header({user}) {
    const { t } = useTranslation();

    // Function to format role for display (remove ROLE_ prefix and capitalize)
    const formatRole = (roleString) => {
        if (!roleString) return '';
        // Remove ROLE_ prefix if present
        const roleName = roleString.replace('ROLE_', '');
        // Capitalize first letter, lowercase the rest
        return roleName.charAt(0).toUpperCase() + roleName.slice(1).toLowerCase();
    };

    const isGestionnaire = () => {
        console.log(user)
        return user && user.role &&
            (user.role.toString() === 'GESTIONNAIRE');
    }
    const isPrepose = () => {
        console.log(user)
        return user && user.role &&
            (user.role.toString() === 'GESTIONNAIRE' || user.role.toString() === 'PREPOSE');
    }
    const isEmprunteur = () => {
        console.log(user)
        return user && user.role &&
            (user.role.toString() === 'GESTIONNAIRE' || user.role.toString() === 'EMPRUNTEUR');
    }

    return (
        <header className="header">
            <h1>{t("header.title")}</h1>
            <nav>
                <ul className="nav-links">
                    <li><Link to="/">{t("header.accueil")}</Link></li>
                    <li><Link to="/about">{t("header.about")}</Link></li>
                    {isEmprunteur() && <li><Link to="/emprunteur">{t("header.emprunteur")}</Link></li>}
                    {isPrepose() && <li><Link to="/prepose">{t("header.prepose")}</Link></li>}
                    {isGestionnaire() && <li><Link to="/gestionnaire">{t("header.gestionnaire")}</Link></li>}
                    <li>{user?.isLoggedIn ? <Link to="/logout">{t("header.logout")}</Link> : <Link to="/login">{t("header.login")}</Link>}</li>
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