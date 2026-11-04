import { render, screen, fireEvent } from '@testing-library/react';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key) => key })
}));

vi.mock('react-pdf', () => ({
    Document: () => null,
    Page: () => null,
    pdfjs: { GlobalWorkerOptions: {} }
}));

vi.mock('../../../utils/fetcher.js', () => ({
    default: vi.fn(() => Promise.resolve({ ok: true, text: () => Promise.resolve('Ajoutez vos stages.') }))
}));
vi.mock('../../../utils/downloadFile.js', () => ({
    fetchBlobUrl: vi.fn(),
    downloadFile: vi.fn()
}));

vi.mock('../../../styles/classes.js', () => ({
    cardItem: 'card',
    cardHighlighted: 'card-highlighted',
    btnSmPrimary: 'btn-primary',
    btnRefuse: 'btn-refuse',
    statusBadge: 'badge',
    statusBadgeColor: () => '',
    textError: 'text-error',
    textMuted: 'text-muted',
}));

import fetcher from '../../../utils/fetcher.js';
import CvListItem from './CvListItem.jsx';

const baseCv = { id: 1, fileName: 'cv.pdf', status: 'ACCEPTED' };
const baseProps = {
    cv: baseCv,
    isOpen: false,
    onToggle: vi.fn(),
    isHighlighted: false,
    onClearHighlight: vi.fn(),
};

describe('CvListItem — general rendering', () => {
    beforeEach(() => vi.clearAllMocks());

    it('renders file name', () => {
        render(<CvListItem {...baseProps} />);
        expect(screen.getByText('cv.pdf')).toBeInTheDocument();
    });

    it.each(['ACCEPTED', 'PENDING', 'DECLINED'])('does not show a default CV button for %s CVs', (status) => {
        render(<CvListItem {...baseProps} cv={{ ...baseCv, status }} />);
        expect(screen.queryByText('televerser.default')).not.toBeInTheDocument();
        expect(screen.queryByText('televerser.defaultActif')).not.toBeInTheDocument();
    });

    it('renders status badge', () => {
        render(<CvListItem {...baseProps} />);
        expect(screen.getByText('televerser.statuts.ACCEPTED')).toBeInTheDocument();
    });

    it('toggles preview on button click', () => {
        render(<CvListItem {...baseProps} />);
        fireEvent.click(screen.getByText('televerser.afficher'));
        expect(baseProps.onToggle).toHaveBeenCalledTimes(1);
    });

    it('always shows feedback for DECLINED CVs, without a toggle button', async () => {
        render(<CvListItem {...baseProps} cv={{ ...baseCv, status: 'DECLINED' }} />);
        expect(await screen.findByText('Ajoutez vos stages.')).toBeInTheDocument();
        expect(fetcher).toHaveBeenCalledWith('/student-cv/1/review');
        expect(screen.queryByText('televerser.voirFeedback')).not.toBeInTheDocument();
        expect(screen.queryByText('televerser.masquerFeedback')).not.toBeInTheDocument();
    });

    it.each(['ACCEPTED', 'PENDING'])('does not load feedback for %s CVs', (status) => {
        render(<CvListItem {...baseProps} cv={{ ...baseCv, status }} />);
        expect(fetcher).not.toHaveBeenCalled();
        expect(screen.queryByText('televerser.feedbackTitre')).not.toBeInTheDocument();
    });

    it('highlights the card and clears the highlight when clicked', () => {
        const { container } = render(<CvListItem {...baseProps} isHighlighted={true} />);
        const card = container.firstChild;

        expect(card.className).toContain('card-highlighted');
        fireEvent.click(card);
        expect(baseProps.onClearHighlight).toHaveBeenCalledTimes(1);
    });

    it('does not highlight nor clear when the card is not highlighted', () => {
        const { container } = render(<CvListItem {...baseProps} />);
        const card = container.firstChild;

        expect(card.className).not.toContain('card-highlighted');
        fireEvent.click(card);
        expect(baseProps.onClearHighlight).not.toHaveBeenCalled();
    });
});
