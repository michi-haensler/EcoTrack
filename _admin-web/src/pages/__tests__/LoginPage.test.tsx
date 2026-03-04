/**
 * LoginPage Component-Tests (MS-01)
 *
 * Abgedeckte Szenarien:
 * - Render: Form-Elemente vorhanden, Labels, ARIA
 * - Happy Path: Login → Redirect zu /dashboard
 * - Fehler: Fehlermeldung anzeigen
 * - Loading: Button disabled + Spinner während des Requests
 * - update_password: Weiterleitung-Status-Meldung anzeigen
 */

import * as authMockApi from '@/services/authMockApi';
import { useAuthStore } from '@/stores/authStore';
import type { AuthResponse } from '@/types/auth';
import { PasswordChangeRequiredError } from '@/types/auth';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { LoginPage } from '../LoginPage';

// Zustand des Stores vor jedem Test vollständig zurücksetzen
beforeEach(() => {
  sessionStorage.clear();
  useAuthStore.persist.clearStorage();
  // Nur State-Properties zurücksetzen, Actions NICHT überschreiben (kein replace=true)
  useAuthStore.setState({
    session: null,
    loginStatus: 'idle',
    loginError: null,
    passwordChangeRequired: false,
    passwordChangeUrl: null,
  });
});

afterEach(() => {
  vi.restoreAllMocks();
});

function renderLoginPage() {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/dashboard" element={<div>Dashboard</div>} />
      </Routes>
    </MemoryRouter>,
  );
}

describe('LoginPage', () => {
  // ---------------------------------------------------------------------------
  // Render-Tests
  // ---------------------------------------------------------------------------

  it('should_renderLoginForm_with_allRequiredElements', () => {
    // Arrange & Act
    renderLoginPage();

    // Assert
    expect(screen.getByLabelText(/E-Mail-Adresse/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Passwort/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Anmelden/i })).toBeInTheDocument();
  });

  it('should_haveCorrectAriaAttributes', () => {
    // Arrange & Act
    renderLoginPage();

    // Assert — Barrierefreiheit
    const emailInput = screen.getByLabelText(/E-Mail-Adresse/i);
    const passwordInput = screen.getByLabelText(/Passwort/i);

    expect(emailInput).toHaveAttribute('type', 'email');
    expect(emailInput).toHaveAttribute('aria-required', 'true');
    expect(passwordInput).toHaveAttribute('type', 'password');
    expect(passwordInput).toHaveAttribute('aria-required', 'true');
  });

  // ---------------------------------------------------------------------------
  // Happy Path
  // ---------------------------------------------------------------------------

  it('should_navigateToDashboard_when_loginSucceeds', async () => {
    // Arrange
    const mockResponse: AuthResponse = {
      accessToken: 'fake-token',
      refreshToken: 'fake-refresh',
      expiresIn: 900,
      user: {
        userId: '123',
        ecoUserId: '123',
        email: 'admin@ecotrack.test',
        firstName: 'Test',
        lastName: 'Admin',
        role: 'ADMIN',
      },
    };
    vi.spyOn(authMockApi, 'adminLogin').mockResolvedValueOnce(mockResponse);
    const user = userEvent.setup();

    renderLoginPage();

    // Act
    await user.type(screen.getByLabelText(/E-Mail-Adresse/i), 'admin@ecotrack.test');
    await user.type(screen.getByLabelText(/Passwort/i), 'Admin123!');
    await user.click(screen.getByRole('button', { name: /Anmelden/i }));

    // Assert
    await waitFor(() => {
      expect(screen.getByText('Dashboard')).toBeInTheDocument();
    });
  });

  // ---------------------------------------------------------------------------
  // Fail-Pfade
  // ---------------------------------------------------------------------------

  it('should_showError_when_invalidCredentials', async () => {
    // Arrange
    vi.spyOn(authMockApi, 'adminLogin').mockRejectedValueOnce(
      Object.assign(new Error('E-Mail oder Passwort falsch'), { code: 'INVALID_CREDENTIALS', status: 401 }),
    );
    const user = userEvent.setup();
    renderLoginPage();

    // Act
    await user.type(screen.getByLabelText(/E-Mail-Adresse/i), 'admin@ecotrack.test');
    await user.type(screen.getByLabelText(/Passwort/i), 'wrongpassword');
    await user.click(screen.getByRole('button', { name: /Anmelden/i }));

    // Assert
    await waitFor(() => {
      expect(screen.getByRole('alert')).toBeInTheDocument();
      expect(screen.getByText(/E-Mail oder Passwort falsch/i)).toBeInTheDocument();
    });
  });

  it('should_disableButton_during_loading', async () => {
    // Arrange — langsame Antwort simulieren
    vi.spyOn(authMockApi, 'adminLogin').mockImplementationOnce(
      () => new Promise((resolve) => setTimeout(resolve, 5000)),
    );
    const user = userEvent.setup();
    renderLoginPage();

    // Act
    await user.type(screen.getByLabelText(/E-Mail-Adresse/i), 'admin@ecotrack.test');
    await user.type(screen.getByLabelText(/Passwort/i), 'Admin123!');
    await user.click(screen.getByRole('button', { name: /Anmelden/i }));

    // Assert — Button während Loading disabled und aria-busy=true
    await waitFor(() => {
      const button = screen.getByRole('button');
      expect(button).toBeDisabled();
      expect(button).toHaveAttribute('aria-busy', 'true');
    });
  });

  // ---------------------------------------------------------------------------
  // update_password-Flow
  // ---------------------------------------------------------------------------

  it('should_showPasswordChangeMessage_when_keycloakRequiresPasswordChange', async () => {
    // Arrange
    vi.spyOn(authMockApi, 'adminLogin').mockRejectedValueOnce(
      new PasswordChangeRequiredError('http://localhost:8180/realms/ecotrack/login-actions/required-action?execution=UPDATE_PASSWORD'),
    );
    // window.location.href mocken, damit der useEffect-Redirect nicht crasht
    const locationSpy = vi.spyOn(window, 'location', 'get').mockReturnValue({
      ...window.location,
      href: '',
    } as Location);

    const user = userEvent.setup();
    renderLoginPage();

    // Act
    await user.type(screen.getByLabelText(/E-Mail-Adresse/i), 'new-admin@ecotrack.test');
    await user.type(screen.getByLabelText(/Passwort/i), 'TempPass123!');
    await user.click(screen.getByRole('button', { name: /Anmelden/i }));

    // Assert — Banner "wird weitergeleitet" erscheint
    await waitFor(() => {
      expect(
        screen.getByRole('status'),
      ).toBeInTheDocument();
    });

    locationSpy.mockRestore();
  });
});
