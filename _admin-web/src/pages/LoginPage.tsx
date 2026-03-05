/**
 * LoginPage — MS-01 Admin-Web
 *
 * Abgedeckte Akzeptanzkriterien:
 * - Login mit E-Mail + Passwort
 * - Loading-State während des API-Calls
 * - Fehlerzustand (401 / INVALID_CREDENTIALS)
 * - update_password → Redirect zur Keycloak-UI
 * - Barrierefreiheit: aria-labels, form-labels, Tastaturnavigation
 */

import { isAuthenticated, useAuthStore } from '@/stores/authStore';
import { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

export function LoginPage() {
  const navigate = useNavigate();
  const emailRef = useRef<HTMLInputElement>(null);

  const login = useAuthStore((s) => s.login);
  const clearError = useAuthStore((s) => s.clearError);
  const loginStatus = useAuthStore((s) => s.loginStatus);
  const loginError = useAuthStore((s) => s.loginError);
  const passwordChangeRequired = useAuthStore((s) => s.passwordChangeRequired);
  const passwordChangeUrl = useAuthStore((s) => s.passwordChangeUrl);
  const authenticated = useAuthStore(isAuthenticated);

  // Redirect wenn bereits eingeloggt
  useEffect(() => {
    if (authenticated) {
      navigate('/dashboard', { replace: true });
    }
  }, [authenticated, navigate]);

  // Passwortänderung über Keycloak (manueller Button statt Auto-Redirect)
  function handlePasswordChangeRedirect() {
    if (passwordChangeUrl) {
      window.open(passwordChangeUrl, '_blank');
    }
  }

  // Fokus auf E-Mail-Feld beim ersten Laden
  useEffect(() => {
    emailRef.current?.focus();
  }, []);

  const isLoading = loginStatus === 'loading';

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    clearError();

    const formData = new FormData(e.currentTarget);
    const email = formData.get('email') as string;
    const password = formData.get('password') as string;

    await login({ email: email.trim(), password });
  }

  return (
    <main className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        {/* Logo / Branding */}
        <div className="text-center mb-8">
          <div
            className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-primary-600 text-white text-2xl mb-4"
            aria-hidden="true"
          >
            🌿
          </div>
          <h1 className="text-2xl font-bold text-gray-900">EcoTrack Admin</h1>
          <p className="text-sm text-gray-500 mt-1">
            Anmeldung für Lehrer &amp; Administratoren
          </p>
        </div>

        {/* Login-Karte */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-8">
          <form onSubmit={handleSubmit} noValidate aria-label="Anmeldeformular">
            {/* E-Mail */}
            <div className="mb-5">
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700 mb-1.5"
              >
                E-Mail-Adresse
              </label>
              <input
                ref={emailRef}
                id="email"
                name="email"
                type="email"
                autoComplete="email"
                required
                disabled={isLoading}
                placeholder="admin@schule.at"
                className="w-full px-3.5 py-2.5 rounded-lg border border-gray-300 text-gray-900 placeholder-gray-400
                           focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500
                           disabled:bg-gray-50 disabled:text-gray-400 transition"
                aria-required="true"
              />
            </div>

            {/* Passwort */}
            <div className="mb-6">
              <div className="flex items-center justify-between mb-1.5">
                <label
                  htmlFor="password"
                  className="block text-sm font-medium text-gray-700"
                >
                  Passwort
                </label>
              </div>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                required
                disabled={isLoading}
                placeholder="••••••••"
                className="w-full px-3.5 py-2.5 rounded-lg border border-gray-300 text-gray-900 placeholder-gray-400
                           focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500
                           disabled:bg-gray-50 disabled:text-gray-400 transition"
                aria-required="true"
              />
            </div>

            {/* Fehlermeldung */}
            {loginError && (
              <div
                role="alert"
                aria-live="assertive"
                className="mb-4 flex items-start gap-2 rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700"
              >
                <span aria-hidden="true" className="mt-0.5 shrink-0">⚠️</span>
                <span>{loginError}</span>
              </div>
            )}

            {/* Passwortänderungszwang — Info + manueller Button */}
            {passwordChangeRequired && (
              <div
                role="status"
                aria-live="polite"
                className="mb-4 rounded-lg bg-yellow-50 border border-yellow-200 px-4 py-3 text-sm text-yellow-800"
              >
                <div className="flex items-start gap-2 mb-2">
                  <span aria-hidden="true" className="mt-0.5 shrink-0">🔑</span>
                  <span>
                    Dein Passwort muss geändert werden. Klicke auf den Button, um
                    die Keycloak-Passwortänderung zu öffnen.
                    <br />
                    <span className="text-xs text-yellow-600">
                      (Keycloak muss unter localhost:8180 laufen)
                    </span>
                  </span>
                </div>
                <button
                  type="button"
                  onClick={handlePasswordChangeRedirect}
                  className="w-full py-2 px-3 bg-yellow-600 hover:bg-yellow-700 text-white text-sm font-medium rounded-lg transition"
                >
                  Passwort ändern (Keycloak öffnen)
                </button>
              </div>
            )}

            {/* Submit-Button */}
            <button
              type="submit"
              disabled={isLoading || passwordChangeRequired}
              className="w-full py-2.5 px-4 bg-primary-600 hover:bg-primary-700 text-white font-medium rounded-lg
                         focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500
                         disabled:opacity-60 disabled:cursor-not-allowed transition"
              aria-busy={isLoading}
            >
              {isLoading ? (
                <span className="flex items-center justify-center gap-2">
                  <span
                    className="inline-block w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
                    aria-hidden="true"
                  />
                  Wird angemeldet…
                </span>
              ) : (
                'Anmelden'
              )}
            </button>
          </form>
        </div>

        {/* Hinweis für Initiative */}
        <p className="text-center text-xs text-gray-400 mt-6">
          EcoTrack — Nachhaltigkeits-App für Schulen
        </p>
      </div>
    </main>
  );
}
