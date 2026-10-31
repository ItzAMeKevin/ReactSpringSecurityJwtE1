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
    btnSmPrimary: 'btn-primary',
    btnRefuse: 'btn-refuse',
    btnSmSelectedSoft: 'btn-selected',
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
    scrollToFeedback: false,
    isDefault: false,
    onToggleDefault: vi.fn(),
};

describe('CvListItem — default CV button', () => {
    beforeEach(() => vi.clearAllMocks());

    it('shows default button for ACCEPTED CV', () => {
        render(<CvListItem {...baseProps} />);
        expect(screen.getByText('televerser.default')).toBeInTheDocument();
    });

    it('does not show default button for PENDING CV', () => {
        render(<CvListItem {...baseProps} cv={{ ...baseCv, status: 'PENDING' }} />);
        expect(screen.queryByText('televerser.default')).not.toBeInTheDocument();
        expect(screen.queryByText('televerser.defaultActif')).not.toBeInTheDocument();
    });

    it('does not show default button for DECLINED CV', () => {
        render(<CvListItem {...baseProps} cv={{ ...baseCv, status: 'DECLINED' }} />);
        expect(screen.queryByText('televerser.default')).not.toBeInTheDocument();
        expect(screen.queryByText('televerser.defaultActif')).not.toBeInTheDocument();
    });

    it('shows active label and is disabled when isDefault is true', () => {
        render(<CvListItem {...baseProps} isDefault={true} />);
        const btn = screen.getByText('televerser.defaultActif');
        expect(btn).toBeInTheDocument();
        expect(btn).toBeDisabled();
    });

    it('shows inactive label and is enabled when isDefault is false', () => {
        render(<CvListItem {...baseProps} isDefault={false} />);
        const btn = screen.getByText('televerser.default');
        expect(btn).not.toBeDisabled();
    });

    it('calls onToggleDefault when clicked and not already default', () => {
        render(<CvListItem {...baseProps} isDefault={false} />);
        fireEvent.click(screen.getByText('televerser.default'));
        expect(baseProps.onToggleDefault).toHaveBeenCalledTimes(1);
    });

    it('does not call onToggleDefault when already default', () => {
        render(<CvListItem {...baseProps} isDefault={true} />);
        fireEvent.click(screen.getByText('televerser.defaultActif'));
        expect(baseProps.onToggleDefault).not.toHaveBeenCalled();
    });

    it('uses btn-selected class when default, btn-primary when not', () => {
        const { rerender } = render(<CvListItem {...baseProps} isDefault={false} />);
        expect(screen.getByText('televerser.default').className).toContain('btn-primary');

        rerender(<CvListItem {...baseProps} isDefault={true} />);
        expect(screen.getByText('televerser.defaultActif').className).toContain('btn-selected');
    });
});

describe('CvListItem — general rendering', () => {
    beforeEach(() => vi.clearAllMocks());

    it('renders file name', () => {
        render(<CvListItem {...baseProps} />);
        expect(screen.getByText('cv.pdf')).toBeInTheDocument();
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

    it('scrolls to the card when scrollToFeedback is true', async () => {
        const scrollIntoView = vi.fn();
        Element.prototype.scrollIntoView = scrollIntoView;
        render(<CvListItem {...baseProps} cv={{ ...baseCv, status: 'DECLINED' }} scrollToFeedback={true} />);
        await screen.findByText('Ajoutez vos stages.');
        expect(scrollIntoView).toHaveBeenCalledTimes(1);
        delete Element.prototype.scrollIntoView;
    });
});
