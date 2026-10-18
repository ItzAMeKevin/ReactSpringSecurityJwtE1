import { render, screen } from '@testing-library/react';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key, options) => {
            if (key === 'inscription.programsList' && options?.returnObjects) {
                return [{ enumName: 'TECHNIQUES_INFORMATIQUE', name: 'Computer Science' }];
            }
            return key;
        }
    })
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
    });

    it('shows offer title when fetch succeeds', async () => {
        fetcher.mockResolvedValue({
            ok: true,
            status: 200,
            json: async () => [mockOffer]
        });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('Dev Web Intern')).toBeInTheDocument();
    });

    it('shows noOffers message when fetch returns empty list', async () => {
        fetcher.mockResolvedValue({
            ok: true,
            status: 200,
            json: async () => []
        });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('etudiantOffres.noOffers')).toBeInTheDocument();
    });

    it('shows error message when fetch fails', async () => {
        fetcher.mockResolvedValue({ ok: false, status: 500 });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('errors.generic')).toBeInTheDocument();
    });
});
