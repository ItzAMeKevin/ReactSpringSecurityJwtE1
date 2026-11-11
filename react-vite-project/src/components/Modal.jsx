import { useEffect, useId } from "react";
import { createPortal } from "react-dom";
import { useTranslation } from "react-i18next";
import { btnSmPrimary } from "../styles/classes.js";

const Modal = ({ title, onClose, children, widthClass = "w-full max-w-3xl" }) => {
    const { t } = useTranslation();
    const titleId = useId();

    useEffect(() => {
        const handleEscape = (event) => {
            if (event.key === "Escape") onClose();
        };
        document.addEventListener("keydown", handleEscape);
        return () => document.removeEventListener("keydown", handleEscape);
    }, [onClose]);

    return createPortal(
        <div
            className="fixed inset-0 z-[1000] flex items-center justify-center bg-black/50 p-4"
            role="presentation"
            onMouseDown={(event) => {
                if (event.target === event.currentTarget) onClose();
            }}
        >
            <div
                className={`flex max-h-[calc(100vh-2rem)] ${widthClass} flex-col overflow-hidden rounded bg-white shadow-2xl`}
                role="dialog"
                aria-modal="true"
                aria-labelledby={titleId}
            >
                <div className="flex items-center justify-between gap-2 border-b px-6 py-3">
                    <h2 id={titleId} className="text-lg font-bold text-black">{title}</h2>
                    <button type="button" className={btnSmPrimary} onClick={onClose}>
                        {t("modal.close")}
                    </button>
                </div>
                <div className="overflow-y-auto p-6 [scrollbar-gutter:stable]">{children}</div>
            </div>
        </div>,
        document.body
    );
};

export default Modal;
