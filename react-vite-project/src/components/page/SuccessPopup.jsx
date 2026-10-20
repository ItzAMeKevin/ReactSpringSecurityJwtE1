import { successCard } from "../../styles/classes.js";

const SuccessPopup = ({message, onClose}) => {
    return (
        <div
            className={`fixed right-6 top-6 z-[1200] flex items-center gap-4 rounded px-4 py-3 shadow-lg ${successCard}`}
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

export default SuccessPopup;
