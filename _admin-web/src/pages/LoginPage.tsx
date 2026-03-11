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
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

export function LoginPage() {
  const navigate = useNavigate();
  const emailRef = useRef<HTMLInputElement>(null);

  const login = useAuthStore((s) => s.login);
  const changePassword = useAuthStore((s) => s.changePassword);
  const clearError = useAuthStore((s) => s.clearError);
  const loginStatus = useAuthStore((s) => s.loginStatus);
  const loginError = useAuthStore((s) => s.loginError);
  const passwordChangeRequired = useAuthStore((s) => s.passwordChangeRequired);
  const emailNotVerified = useAuthStore((s) => s.emailNotVerified);
  const authenticated = useAuthStore(isAuthenticated);

  // Password change form state
  const [pwCurrentPassword, setPwCurrentPassword] = useState('');
  const [pwNewPassword, setPwNewPassword] = useState('');
  const [pwConfirmPassword, setPwConfirmPassword] = useState('');
  const [pwError, setPwError] = useState<string | null>(null);
  const [pwSuccess, setPwSuccess] = useState<string | null>(null);
  const [pwLoading, setPwLoading] = useState(false);

  // Redirect wenn bereits eingeloggt
  useEffect(() => {
    if (authenticated) {
      navigate('/dashboard', { replace: true });
    }
  }, [authenticated, navigate]);

  // Passwortänderung über eigenen Endpoint
  async function handlePasswordChange(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setPwError(null);
    setPwSuccess(null);

    if (pwNewPassword !== pwConfirmPassword) {
      setPwError('Die Passwörter stimmen nicht überein.');
      return;
    }
    if (pwNewPassword.length < 8) {
      setPwError('Das neue Passwort muss mindestens 8 Zeichen lang sein.');
      return;
    }

    setPwLoading(true);
    try {
      const message = await changePassword(pwCurrentPassword, pwNewPassword);
      setPwSuccess(message);
      setPwCurrentPassword('');
      setPwNewPassword('');
      setPwConfirmPassword('');
    } catch (err) {
      setPwError(err instanceof Error ? err.message : 'Passwortänderung fehlgeschlagen.');
    } finally {
      setPwLoading(false);
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
            {loginError && !emailNotVerified && (
              <div
                role="alert"
                aria-live="assertive"
                className="mb-4 flex items-start gap-2 rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700"
              >
                <span aria-hidden="true" className="mt-0.5 shrink-0">⚠️</span>
                <span>{loginError}</span>
              </div>
            )}

            {/* E-Mail nicht verifiziert — Info-Block */}
            {emailNotVerified && (
              <div
                role="alert"
                aria-live="assertive"
                className="mb-4 rounded-lg bg-blue-50 border border-blue-200 px-4 py-3 text-sm text-blue-800"
              >
                <div className="flex items-start gap-2">
                  <span aria-hidden="true" className="mt-0.5 shrink-0">📧</span>
                  <span>
                    Deine E-Mail-Adresse wurde noch nicht verifiziert.
                    Bitte prüfe dein Postfach und klicke auf den Bestätigungslink.
                  </span>
                </div>
              </div>
            )}

            {/* Passwortänderungszwang — Inline-Formular */}
            {passwordChangeRequired && !pwSuccess && (
              <div
                role="status"
                aria-live="polite"
                className="mb-4 rounded-lg bg-yellow-50 border border-yellow-200 px-4 py-4 text-sm text-yellow-800"
              >
                <div className="flex items-start gap-2 mb-3">
                  <span aria-hidden="true" className="mt-0.5 shrink-0">🔑</span>
                  <span>
                    Dein Passwort muss geändert werden, bevor du dich anmelden kannst.
                  </span>
                </div>

                <form onSubmit={handlePasswordChange} noValidate aria-label="Passwort ändern">
                  <div className="mb-3">
                    <label htmlFor="pw-current" className="block text-xs font-medium text-yellow-700 mb-1">
                      Aktuelles Passwort
                    </label>
                    <input
                      id="pw-current"
                      type="password"
                      autoComplete="current-password"
                      required
                      disabled={pwLoading}
                      value={pwCurrentPassword}
                      onChange={(e) => setPwCurrentPassword(e.target.value)}
                      className="w-full px-3 py-2 rounded-lg border border-yellow-300 text-gray-900 placeholder-gray-400
                                 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-yellow-500
                                 disabled:bg-gray-50 disabled:text-gray-400 text-sm transition"
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="pw-new" className="block text-xs font-medium text-yellow-700 mb-1">
                      Neues Passwort (min. 8 Zeichen)
                    </label>
                    <input
                      id="pw-new"
                      type="password"
                      autoComplete="new-password"
                      required
                      disabled={pwLoading}
                      value={pwNewPassword}
                      onChange={(e) => setPwNewPassword(e.target.value)}
                      className="w-full px-3 py-2 rounded-lg border border-yellow-300 text-gray-900 placeholder-gray-400
                                 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-yellow-500
                                 disabled:bg-gray-50 disabled:text-gray-400 text-sm transition"
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="pw-confirm" className="block text-xs font-medium text-yellow-700 mb-1">
                      Neues Passwort bestätigen
                    </label>
                    <input
                      id="pw-confirm"
                      type="password"
                      autoComplete="new-password"
                      required
                      disabled={pwLoading}
                      value={pwConfirmPassword}
                      onChange={(e) => setPwConfirmPassword(e.target.value)}
                      className="w-full px-3 py-2 rounded-lg border border-yellow-300 text-gray-900 placeholder-gray-400
                                 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-yellow-500
                                 disabled:bg-gray-50 disabled:text-gray-400 text-sm transition"
                    />
                  </div>

                  {pwError && (
                    <div role="alert" className="mb-3 text-xs text-red-600">
                      {pwError}
                    </div>
                  )}

                  <button
                    type="submit"
                    disabled={pwLoading}
                    className="w-full py-2 px-3 bg-yellow-600 hover:bg-yellow-700 text-white text-sm font-medium rounded-lg
                               disabled:opacity-60 disabled:cursor-not-allowed transition"
                  >
                    {pwLoading ? 'Wird geändert…' : 'Passwort ändern'}
                  </button>
                </form>
              </div>
            )}

            {/* Passwort erfolgreich geändert */}
            {pwSuccess && (
              <div
                role="status"
                aria-live="polite"
                className="mb-4 rounded-lg bg-green-50 border border-green-200 px-4 py-3 text-sm text-green-700"
              >
                <div className="flex items-start gap-2">
                  <span aria-hidden="true" className="mt-0.5 shrink-0">✅</span>
                  <span>{pwSuccess}</span>
                </div>
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
