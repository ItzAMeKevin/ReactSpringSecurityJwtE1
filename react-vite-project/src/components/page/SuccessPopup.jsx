const SuccessPopup = ({message, onClose}) => {
    return (
        <div
            className="fixed right-6 top-6 z-[1200] flex items-center gap-4 rounded border border-green-200 bg-green-50 px-4 py-3 text-green-800 shadow-lg"
            role="status"
            aria-live="polite"
        >
            <span>{message}</span>
            <button
                type="button"
                onClick={onClose}
                className="font-bold text-green-800 hover:text-green-950"
                aria-label="Close confirmation"
            >
                x
            </button>
        </div>
    );
};

export default SuccessPopup;
