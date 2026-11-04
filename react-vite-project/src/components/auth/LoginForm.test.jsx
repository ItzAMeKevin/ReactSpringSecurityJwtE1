import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key) => key, i18n: { language: 'fr', changeLanguage: vi.fn() } })
}));

vi.mock('../../utils/fetcher', () => ({ default: vi.fn() }));

import fetcher from '../../utils/fetcher';
import LoginForm from './LoginForm.jsx';

const renderLogin = (state) =>
    render(
        <MemoryRouter initialEntries={[{ pathname: '/login', state }]}>
            <Routes>
                <Route path="/login" element={<LoginForm user={null} setUser={vi.fn()} setError={vi.fn()} />} />
            </Routes>
        </MemoryRouter>
    );

describe('LoginForm — inscription success message', () => {
    beforeEach(() => vi.clearAllMocks());

    it('shows the success message after a new account is created', () => {
        renderLogin({ inscriptionSuccess: true });
        expect(screen.getByText('login.inscriptionSuccess')).toBeInTheDocument();
    });

    it('does not show the success message on a normal visit', () => {
        renderLogin(null);
        expect(screen.queryByText('login.inscriptionSuccess')).not.toBeInTheDocument();
    });

    it('hides the success message when the user tries to log in', async () => {
        fetcher.mockResolvedValue({ ok: false, json: () => Promise.resolve({}) });
        renderLogin({ inscriptionSuccess: true });

        fireEvent.change(screen.getByLabelText('login.email'), { target: { name: 'email', value: 'a@b.com' } });
        fireEvent.submit(document.getElementById('login-form'));

        expect(await screen.findByText('errors.authenticationFailed')).toBeInTheDocument();
        expect(screen.queryByText('login.inscriptionSuccess')).not.toBeInTheDocument();
    });
});
