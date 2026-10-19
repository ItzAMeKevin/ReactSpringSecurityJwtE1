import { render, screen, fireEvent } from '@testing-library/react';
import { vi, describe, it, expect, beforeEach } from 'vitest';

const t = (key, options) => {
    if (key === 'inscription.programsList' && options?.returnObjects) {
        return [{ enumName: 'TECHNIQUES_INFORMATIQUE', name: 'Computer Science' }];
    }
    return key;
};

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t })
}));

vi.mock('../../../utils/fetcher.js', () => ({
    default: vi.fn()
}));

vi.mock('../../../styles/classes.js', () => ({
    cardItem: 'card',
    statusBadge: 'badge',
    statusBadgeColor: () => '',
    textError: 'text-error',
    textMuted: 'text-muted'
}));

import fetcher from '../../../utils/fetcher.js';
import StudentJobOfferList from './StudentJobOfferList.jsx';

const mockOffer = {
    id: 1,
    title: 'Dev Web Intern',
    programe: 'TECHNIQUES_INFORMATIQUE',
    description: 'A great internship',
    prerequisites: 'Java and Spring',
    companyName: 'Acme Corp',
    salary: '20$/h',
    startingDate: '2026-09-01',
    durationInWeeks: 12,
    status: 'ACCEPTED',
    location: { numeroCivic: '123', rue: 'Main St', ville: 'Montreal', pays: 'Canada' }
};

describe('StudentJobOfferList', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('shows loading text initially', () => {
        fetcher.mockReturnValue(new Promise(() => {}));
        render(<StudentJobOfferList />);
        expect(screen.getByText('etudiantOffres.loading')).toBeInTheDocument();
    });

    it('shows noAcceptedCv message when fetch returns 403', async () => {
        fetcher.mockResolvedValue({ ok: false, status: 403 });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('etudiantOffres.noAcceptedCv')).toBeInTheDocument();
        expect(fetcher).toHaveBeenCalledWith('/etudiant/offres');
    });

    it('shows offer summary when collapsed', async () => {
        fetcher.mockResolvedValue({
            ok: true,
            status: 200,
            json: async () => [mockOffer]
        });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('Dev Web Intern')).toBeInTheDocument();
        expect(screen.getByText('Computer Science')).toBeInTheDocument();
        expect(screen.getByText('personalSpaceEmployer.statuts.ACCEPTED')).toBeInTheDocument();
        expect(screen.getByText('Acme Corp')).toBeInTheDocument();
        expect(screen.getByText('2026-09-01 · 12 etudiantOffres.weeks')).toBeInTheDocument();
        expect(screen.getByText('A great internship')).toBeInTheDocument();
        expect(screen.getByText('etudiantOffres.readMore')).toHaveAttribute('aria-expanded', 'false');
        expect(screen.queryByText('Java and Spring')).not.toBeInTheDocument();
        expect(fetcher).toHaveBeenCalledWith('/etudiant/offres');
    });

    it('shows full offer details after clicking read more', async () => {
        fetcher.mockResolvedValue({
            ok: true,
            status: 200,
            json: async () => [mockOffer]
        });
        render(<StudentJobOfferList />);
        fireEvent.click(await screen.findByText('etudiantOffres.readMore'));

        expect(screen.getByText('Java and Spring')).toBeInTheDocument();
        expect(screen.getByText('20$/h')).toBeInTheDocument();
        expect(screen.getByText('2026-09-01')).toBeInTheDocument();
        expect(screen.getByText('12 etudiantOffres.weeks')).toBeInTheDocument();
        expect(screen.getByText('123 Main St, Montreal, Canada')).toBeInTheDocument();
    });

    it('toggles details with read more / show less', async () => {
        fetcher.mockResolvedValue({
            ok: true,
            status: 200,
            json: async () => [mockOffer]
        });
        render(<StudentJobOfferList />);

        fireEvent.click(await screen.findByText('etudiantOffres.readMore'));
        expect(screen.getByText('Java and Spring')).toBeInTheDocument();
        expect(screen.getByText('etudiantOffres.showLess')).toHaveAttribute('aria-expanded', 'true');

        fireEvent.click(screen.getByText('etudiantOffres.showLess'));
        expect(screen.queryByText('Java and Spring')).not.toBeInTheDocument();
        expect(screen.getByText('etudiantOffres.readMore')).toHaveAttribute('aria-expanded', 'false');
    });

    it('expands only the clicked offer', async () => {
        fetcher.mockResolvedValue({
            ok: true,
            status: 200,
            json: async () => [
                mockOffer,
                { ...mockOffer, id: 2, title: 'Second Offer', prerequisites: 'Python' }
            ]
        });
        render(<StudentJobOfferList />);
        const buttons = await screen.findAllByText('etudiantOffres.readMore');
        expect(buttons).toHaveLength(2);

        fireEvent.click(buttons[0]);
        expect(screen.getByText('Java and Spring')).toBeInTheDocument();
        expect(screen.queryByText('Python')).not.toBeInTheDocument();
    });

    it('shows noOffers message when fetch returns empty list', async () => {
        fetcher.mockResolvedValue({
            ok: true,
            status: 200,
            json: async () => []
        });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('etudiantOffres.noOffers')).toBeInTheDocument();
        expect(fetcher).toHaveBeenCalledWith('/etudiant/offres');
    });

    it('shows error message when fetch fails', async () => {
        fetcher.mockResolvedValue({ ok: false, status: 500 });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('errors.generic')).toBeInTheDocument();
        expect(fetcher).toHaveBeenCalledWith('/etudiant/offres');
    });

    it('shows error when response is not an array', async () => {
        fetcher.mockResolvedValue({
            ok: true,
            status: 200,
            json: async () => ({ message: 'not an array' })
        });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('errors.generic')).toBeInTheDocument();
    });

    it('renders safely when offer has no status or location', async () => {
        fetcher.mockResolvedValue({
            ok: true,
            status: 200,
            json: async () => [{
                id: 2,
                title: 'No Status Offer',
                programe: 'TECHNIQUES_INFORMATIQUE',
                description: 'Test',
                salary: '15$/h',
                startingDate: '2026-01-01',
                durationInWeeks: 8,
                status: null,
                location: null
            }]
        });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('No Status Offer')).toBeInTheDocument();

        fireEvent.click(screen.getByText('etudiantOffres.readMore'));
        expect(screen.getByText('—')).toBeInTheDocument();
    });
});