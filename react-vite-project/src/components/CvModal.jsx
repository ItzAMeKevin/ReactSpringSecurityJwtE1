import Modal from "./Modal.jsx";
import CvPdfViewer from "./CvPdfViewer.jsx";
import { textError, textMuted } from "../styles/classes.js";

const CvModal = ({ title, file, error, loadingMessage, onClose }) => (
    <Modal title={title} onClose={onClose} widthClass="w-auto max-w-full">
        {error && <p className={`text-sm ${textError}`} role="alert">{error}</p>}
        {!error && file && <CvPdfViewer file={file} />}
        {!error && !file && <p className={`text-sm ${textMuted}`}>{loadingMessage}</p>}
    </Modal>
);

export default CvModal;
