import { successCard, dangerCard } from "@/styles/classes.js";

const VARIANT_CLASSES = { success: successCard, danger: dangerCard };

const ToastPopup = ({ message, onClose, variant = "success" }) => {
    return (
        <div
            className={`fixed right-6 top-[76px] z-[1200] flex items-center gap-4 rounded px-4 py-3 shadow-lg ${VARIANT_CLASSES[variant]}`}
            role="status"
            aria-live="polite"
        >
            <span>{message}</span>
            <button
                type="button"
                onClick={onClose}
                className="font-bold hover:opacity-70"
                aria-label="Close confirmation"
            >
                x
            </button>
        </div>
    );
};

export default ToastPopup;
