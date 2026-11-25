import { useCallback, useEffect, useRef, useState } from "react";
import { Link, NavLink, Outlet, resolvePath, useLocation, useResolvedPath } from "react-router-dom";
import { useTranslation } from "react-i18next";
import useDismiss from "@/hooks/useDismiss.js";
import useSharedNotifications from "@/hooks/useSharedNotifications.js";
import LanguageSwitcher from "@/components/layout/LanguageSwitcher.jsx";
import { pageTitle } from "@/styles/classes.js";

const linkBase = "flex items-center justify-between gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors";
const linkActive = "bg-selected-bg text-selected-text border-l-4 border-selected-border";
const linkIdle = "text-main hover:bg-white/60";
const countBase = "min-w-7 rounded-full px-2 py-0.5 text-center text-xs font-bold";
const newBadge = "min-w-6 rounded-full bg-yellow-400 px-1.5 py-0.5 text-center text-xs font-bold text-yellow-950";
const iconButton = "inline-flex h-10 w-10 items-center justify-center rounded-md text-main transition-colors";

const Icon = ({ path }) => (
    <svg className="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={path} />
    </svg>
);

const DashboardLayout = ({ title, items, actions, context }) => {
    const { t } = useTranslation();
    const { pathname } = useLocation();
    const [isOpen, setIsOpen] = useState(false);
    const drawerRef = useRef(null);
    const close = useCallback(() => setIsOpen(false), []);
    useDismiss(drawerRef, isOpen, close);
    const { notifications } = useSharedNotifications();
    const basePath = useResolvedPath(".").pathname;
    const countNew = (to) => {
        const path = resolvePath(to, basePath).pathname;
        return notifications.filter((notification) => notification.target?.path === path).length;
    };

    useEffect(() => close(), [pathname, close]);

    return (
        <div className="flex flex-1 bg-mint-bg">
            {isOpen && <div className="fixed inset-0 top-[60px] z-40 bg-black/40 md:hidden" aria-hidden="true" />}
            <aside
                ref={drawerRef}
                className={`fixed top-[60px] bottom-0 left-0 z-50 w-3/4 max-w-72 shrink-0 overflow-y-auto border-r border-brand/20 bg-white transition-transform duration-300 md:static md:z-auto md:w-64 md:max-w-none md:translate-x-0 md:overflow-visible md:bg-white/80 ${isOpen ? "translate-x-0" : "-translate-x-full"}`}
            >
                <div className="flex h-full flex-col gap-4 p-4 md:sticky md:top-[60px] md:h-[calc(100vh-60px)] md:max-h-full">
                    <div className="flex items-start justify-between gap-2">
                        <h1 className={pageTitle}>{title}</h1>
                        <button type="button" onClick={close} aria-label={t("dashboard.closeMenu")} className={`${iconButton} hover:bg-mint-bg md:hidden`}>
                            <Icon path="M6 18L18 6M6 6l12 12" />
                        </button>
                    </div>
                    {actions}
                    <nav className="flex min-h-0 flex-1 flex-col gap-1 overflow-y-auto">
                        {items.map((item) => (
                            <NavLink
                                key={item.to}
                                to={item.to}
                                className={({ isActive }) => `${linkBase} ${isActive ? linkActive : linkIdle}`}
                            >
                                {({ isActive }) => {
                                    const newCount = countNew(item.to);
                                    return (
                                        <>
                                            <span>{item.label}</span>
                                            <span className="flex items-center gap-1">
                                                {newCount > 0 && (
                                                    <span className={newBadge} aria-label={t("dashboard.newItems", { count: newCount })}>
                                                        {newCount}
                                                    </span>
                                                )}
                                                {item.count != null && (
                                                    <span className={`${countBase} ${isActive ? "bg-brand text-white" : "bg-brand/10 text-brand"}`}>
                                                        {item.count}
                                                    </span>
                                                )}
                                            </span>
                                        </>
                                    );
                                }}
                            </NavLink>
                        ))}
                    </nav>
                    <div className="mt-auto flex flex-col gap-3 border-t border-brand/20 pt-4">
                        <div className="px-3 text-sm">
                            <LanguageSwitcher
                                activeClassName="rounded bg-brand px-2 py-1 font-bold text-white"
                                idleClassName="rounded px-2 py-1 text-main hover:bg-mint-bg"
                            />
                        </div>
                        <Link to="/logout" className={`${linkBase} ${linkIdle}`}>
                            {t("header.logout")}
                        </Link>
                    </div>
                </div>
            </aside>
            <main className="min-w-0 flex-1 px-4 pb-20 md:px-8">
                <div className="sticky top-[60px] z-30 -mx-4 bg-mint-bg/95 px-4 py-2 md:hidden">
                    <button
                        type="button"
                        onClick={() => setIsOpen(true)}
                        aria-label={t("dashboard.openMenu")}
                        aria-expanded={isOpen}
                        className={`${iconButton} border border-brand/20 bg-white hover:bg-selected-bg`}
                    >
                        <Icon path="M4 6h16M4 12h16M4 18h16" />
                    </button>
                </div>
                <div className="mx-auto w-full max-w-5xl pt-4 md:pt-10">
                    <Outlet context={context} />
                </div>
            </main>
        </div>
    );
};

export default DashboardLayout;
