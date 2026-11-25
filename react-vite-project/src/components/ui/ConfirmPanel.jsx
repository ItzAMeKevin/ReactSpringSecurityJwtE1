import { useTranslation } from "react-i18next";
import { btnSmPrimary } from "@/styles/classes.js";

const ConfirmPanel = ({ message, panelClass, confirmClass, isProcessing, onConfirm, onCancel }) => {
    const { t } = useTranslation();

    return (
        <div className={`mt-3 flex flex-col gap-2 rounded-md p-3 ${panelClass}`} role="group">
            <p className="text-sm font-semibold text-main">{message}</p>
            <div className="flex gap-2">
                <button type="button" onClick={onConfirm} disabled={isProcessing} className={confirmClass}>
                    {isProcessing ? t("confirmPanel.processing") : t("confirmPanel.confirm")}
                </button>
                <button type="button" onClick={onCancel} disabled={isProcessing} className={btnSmPrimary}>
                    {t("confirmPanel.cancel")}
                </button>
            </div>
        </div>
    );
};

export default ConfirmPanel;
