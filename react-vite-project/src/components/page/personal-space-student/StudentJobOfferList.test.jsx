import { render, screen, fireEvent, act } from '@testing-library/react';
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
    applied: false,
    location: { numeroCivic: '123', rue: 'Main St', ville: 'Montreal', pays: 'Canada' }
};

const validatedCv = { id: 3, fileName: 'cv.pdf', status: 'ACCEPTED' };

const jsonRes = (data, status = 200) => ({
    ok: status >= 200 && status < 300,
    status,
    json: async () => data
});

const mockApi = ({
                     offers = [mockOffer],
                     offersStatus = 200,
                     cvs = [validatedCv],
                     cvsStatus = 200,
                     apply = () => Promise.resolve(jsonRes('ok'))
                 } = {}) => {
    fetcher.mockImplementation((url) => {
        if (url === '/etudiant/offres') {
            return Promise.resolve(jsonRes(offersStatus === 200 ? offers : null, offersStatus));
        }
        if (url === '/student-cv') {
            return Promise.resolve(jsonRes(cvsStatus === 200 ? cvs : null, cvsStatus));
        }
        if (url.includes('/appliquer')) {
            return apply(url);
        }
        return Promise.reject(new Error(`unexpected url ${url}`));
    });
};

const applyCalls = () => fetcher.mock.calls.filter(([url]) => url.includes('/appliquer'));

const expand = async () => {
    fireEvent.click(await screen.findByText('etudiantOffres.readMore'));
};

describe('StudentJobOfferList', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        vi.spyOn(console, 'error').mockImplementation(() => {});
    });

    it('shows loading text initially', () => {
        fetcher.mockReturnValue(new Promise(() => {}));
        render(<StudentJobOfferList />);
        expect(screen.getByText('etudiantOffres.loading')).toBeInTheDocument();
    });

    it('shows noAcceptedCv message when fetch returns 403', async () => {
        mockApi({ offersStatus: 403 });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('etudiantOffres.noAcceptedCv')).toBeInTheDocument();
        expect(fetcher).toHaveBeenCalledWith('/etudiant/offres');
    });

    it('shows offer summary when collapsed', async () => {
        mockApi();
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
        mockApi();
        render(<StudentJobOfferList />);
        await expand();

        expect(screen.getByText('Java and Spring')).toBeInTheDocument();
        expect(screen.getByText('20$/h')).toBeInTheDocument();
        expect(screen.getByText('2026-09-01')).toBeInTheDocument();
        expect(screen.getByText('12 etudiantOffres.weeks')).toBeInTheDocument();
        expect(screen.getByText('123 Main St, Montreal, Canada')).toBeInTheDocument();
    });

    it('toggles details with read more / show less', async () => {
        mockApi();
        render(<StudentJobOfferList />);

        await expand();
        expect(screen.getByText('Java and Spring')).toBeInTheDocument();
        expect(screen.getByText('etudiantOffres.showLess')).toHaveAttribute('aria-expanded', 'true');

        fireEvent.click(screen.getByText('etudiantOffres.showLess'));
        expect(screen.queryByText('Java and Spring')).not.toBeInTheDocument();
        expect(screen.getByText('etudiantOffres.readMore')).toHaveAttribute('aria-expanded', 'false');
    });

    it('expands only the clicked offer', async () => {
        mockApi({
            offers: [mockOffer, { ...mockOffer, id: 2, title: 'Second Offer', prerequisites: 'Python' }]
        });
        render(<StudentJobOfferList />);
        const buttons = await screen.findAllByText('etudiantOffres.readMore');
        expect(buttons).toHaveLength(2);

        fireEvent.click(buttons[0]);
        expect(screen.getByText('Java and Spring')).toBeInTheDocument();
        expect(screen.queryByText('Python')).not.toBeInTheDocument();
    });

    it('shows noOffers message when fetch returns empty list', async () => {
        mockApi({ offers: [] });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('etudiantOffres.noOffers')).toBeInTheDocument();
        expect(fetcher).toHaveBeenCalledWith('/etudiant/offres');
    });

    it('shows error message when fetch fails', async () => {
        mockApi({ offersStatus: 500 });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('errors.generic')).toBeInTheDocument();
        expect(fetcher).toHaveBeenCalledWith('/etudiant/offres');
    });

    it('shows error when response is not an array', async () => {
        mockApi({ offers: { message: 'not an array' } });
        render(<StudentJobOfferList />);
        expect(await screen.findByText('errors.generic')).toBeInTheDocument();
    });

    it('renders safely when offer has no status or location', async () => {
        mockApi({
            offers: [{
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

describe('Postuler à une offre', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        vi.spyOn(console, 'error').mockImplementation(() => {});
    });

    it('envoie la candidature avec le CV validé et affiche la confirmation', async () => {
        mockApi();
        render(<StudentJobOfferList />);
        await expand();

        fireEvent.click(screen.getByRole('button', { name: 'etudiantOffres.apply' }));

        expect(await screen.findByText('etudiantOffres.applySuccess')).toBeInTheDocument();
        expect(fetcher).toHaveBeenCalledWith(
            '/student-cv/1/appliquer?offerID=1',
            expect.objectContaining({
                method: 'POST',
                body: JSON.stringify(validatedCv)
            })
        );
        expect(screen.getAllByText('etudiantOffres.applied').length).toBeGreaterThanOrEqual(1);
        expect(screen.getByRole('button', { name: 'etudiantOffres.applied' })).toBeDisabled();
    });

    it("affiche l'état d'envoi en cours puis le succès", async () => {
        let resolveApply;
        mockApi({ apply: () => new Promise((resolve) => { resolveApply = resolve; }) });
        render(<StudentJobOfferList />);
        await expand();

        fireEvent.click(screen.getByRole('button', { name: 'etudiantOffres.apply' }));

        const sendingButton = screen.getByRole('button', { name: 'etudiantOffres.applying' });
        expect(sendingButton).toBeDisabled();

        await act(async () => {
            resolveApply(jsonRes('ok'));
        });
        expect(await screen.findByText('etudiantOffres.applySuccess')).toBeInTheDocument();
    });

    it("n'envoie pas deux fois la candidature après un succès", async () => {
        mockApi();
        render(<StudentJobOfferList />);
        await expand();

        fireEvent.click(screen.getByRole('button', { name: 'etudiantOffres.apply' }));
        await screen.findByText('etudiantOffres.applySuccess');

        fireEvent.click(screen.getByRole('button', { name: 'etudiantOffres.applied' }));
        expect(applyCalls()).toHaveLength(1);
    });

    it("affiche une erreur quand le serveur répond 500 et laisse réessayer", async () => {
        mockApi({ apply: () => Promise.resolve(jsonRes(null, 500)) });
        render(<StudentJobOfferList />);
        await expand();

        fireEvent.click(screen.getByRole('button', { name: 'etudiantOffres.apply' }));

        expect(await screen.findByText('etudiantOffres.applyError')).toBeInTheDocument();
        expect(screen.queryByText('etudiantOffres.applySuccess')).not.toBeInTheDocument();
        expect(screen.getByRole('button', { name: 'etudiantOffres.apply' })).toBeEnabled();
    });

    it("affiche une erreur quand l'appel réseau échoue", async () => {
        mockApi({ apply: () => Promise.reject(new Error('network down')) });
        render(<StudentJobOfferList />);
        await expand();

        fireEvent.click(screen.getByRole('button', { name: 'etudiantOffres.apply' }));

        expect(await screen.findByText('etudiantOffres.applyError')).toBeInTheDocument();
    });

    it('affiche « Candidature envoyée » et bloque le bouton si offer.applied est vrai', async () => {
        mockApi({ offers: [{ ...mockOffer, applied: true }] });
        render(<StudentJobOfferList />);

        expect(await screen.findByText('etudiantOffres.applied')).toBeInTheDocument();

        fireEvent.click(screen.getByText('etudiantOffres.readMore'));
        const button = screen.getByRole('button', { name: 'etudiantOffres.applied' });
        expect(button).toBeDisabled();

        fireEvent.click(button);
        expect(applyCalls()).toHaveLength(0);
    });

    it("désactive « Postuler » et explique pourquoi quand aucun CV n'est validé", async () => {
        mockApi({ cvs: [{ id: 3, fileName: 'cv.pdf', status: 'PENDING' }] });
        render(<StudentJobOfferList />);
        await expand();

        const button = screen.getByRole('button', { name: 'etudiantOffres.apply' });
        expect(button).toBeDisabled();
        expect(screen.getByText('etudiantOffres.noValidatedCv')).toBeInTheDocument();

        fireEvent.click(button);
        expect(applyCalls()).toHaveLength(0);
    });

    it('désactive « Postuler » si la liste des CV ne peut pas être chargée', async () => {
        mockApi({ cvsStatus: 500 });
        render(<StudentJobOfferList />);
        await expand();

        expect(screen.getByText('Dev Web Intern')).toBeInTheDocument();
        expect(screen.getByRole('button', { name: 'etudiantOffres.apply' })).toBeDisabled();
    });

    it('désactive « Postuler » si la réponse des CV n\'est pas un tableau', async () => {
        mockApi({ cvs: { message: 'oops' } });
        render(<StudentJobOfferList />);
        await expand();

        expect(screen.getByRole('button', { name: 'etudiantOffres.apply' })).toBeDisabled();
    });

    it('utilise le CV validé le plus récent (premier de la liste)', async () => {
        mockApi({
            cvs: [
                { id: 5, status: 'PENDING' },
                { id: 4, status: 'ACCEPTED' },
                { id: 3, status: 'ACCEPTED' }
            ]
        });
        render(<StudentJobOfferList />);
        await expand();

        fireEvent.click(screen.getByRole('button', { name: 'etudiantOffres.apply' }));
        await screen.findByText('etudiantOffres.applySuccess');

        const [, options] = applyCalls()[0];
        expect(JSON.parse(options.body).id).toBe(4);
    });

    it('postule à la bonne offre quand il y en a plusieurs', async () => {
        mockApi({ offers: [mockOffer, { ...mockOffer, id: 2, title: 'Second Offer' }] });
        render(<StudentJobOfferList />);

        const readMoreButtons = await screen.findAllByText('etudiantOffres.readMore');
        fireEvent.click(readMoreButtons[1]);

        fireEvent.click(screen.getByRole('button', { name: 'etudiantOffres.apply' }));
        await screen.findByText('etudiantOffres.applySuccess');

        expect(applyCalls()[0][0]).toBe('/student-cv/2/appliquer?offerID=2');
    });
});