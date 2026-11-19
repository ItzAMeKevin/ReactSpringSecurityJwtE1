import { useCallback, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import useDismiss from "../hooks/useDismiss.js";
import LanguageSwitcher from "./LanguageSwitcher.jsx";

const UserMenu = ({ user }) => {
    const { t } = useTranslation();
    const [isOpen, setIsOpen] = useState(false);
    const menuRef = useRef(null);
    const close = useCallback(() => setIsOpen(false), []);
    useDismiss(menuRef, isOpen, close);

    const role = user.role?.replace("ROLE_", "");
    const companyName = role === "EMPLOYER" ? user.companyName : null;

    return (
        <div ref={menuRef} className="relative ml-2">
            <button
                type="button"
                onClick={() => setIsOpen((current) => !current)}
                aria-label={t("header.userMenu")}
                aria-expanded={isOpen}
                aria-haspopup="menu"
                className="inline-flex h-10 w-10 items-center justify-center rounded-full transition-colors hover:bg-white/20"
            >
                <svg className="h-7 w-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M5.121 17.804A13.937 13.937 0 0112 16c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 11-6 0 3 3 0 016 0zm6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                    />
                </svg>
            </button>
            {isOpen && (
                <div role="menu" className="absolute right-0 z-50 mt-2 w-64 rounded-lg border border-brand/20 bg-white text-main shadow-lg">
                    <div className="border-b border-brand/10 p-3">
                        <p className="text-sm">
                            {t("header.greeting")} <span className="font-bold">{user.firstName} {user.lastName}</span>
                        </p>
                        {companyName && <p className="text-sm font-semibold text-brand">{companyName}</p>}
                        {role && <p className="text-xs text-main/60">{t(`header.roles.${role}`)}</p>}
                    </div>
                    <div className="border-b border-brand/10 p-3 text-sm">
                        <LanguageSwitcher
                            activeClassName="rounded bg-brand px-2 py-1 font-bold text-white"
                            idleClassName="rounded px-2 py-1 hover:bg-mint-bg"
                        />
                    </div>
                    <Link to="/logout" onClick={close} role="menuitem" className="block rounded-b-lg px-3 py-2 text-sm hover:bg-mint-bg">
                        {t("header.logout")}
                    </Link>
                </div>
            )}
        </div>
    );
};

export default UserMenu;
