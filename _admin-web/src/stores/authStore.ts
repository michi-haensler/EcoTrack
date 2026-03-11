/**
 * Auth-Store (Zustand) — MS-01 Admin-Web
 *
 * Verwaltet die Authentifizierungssession:
 * - Login / Logout
 * - Token-Persistenz im sessionStorage
 * - PasswordChangeRequired-Handling
 *
 * Verwendet die echte Backend-API (oder Mock via VITE_USE_MOCK_API=true).
 */

import * as realApi from '@/services/authApi';
import * as mockApi from '@/services/authMockApi';
import type { AdminLoginRequest, AuthSession } from '@/types/auth';
import { EmailNotVerifiedError, InsufficientRoleError, PasswordChangeRequiredError } from '@/types/auth';
import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

const STORAGE_KEY = 'ecotrack-admin-auth';
const useMock = import.meta.env.VITE_USE_MOCK_API === 'true';
const api = useMock ? mockApi : realApi;

// ---------------------------------------------------------------------------
// State-Typen
// ---------------------------------------------------------------------------

type LoginStatus = 'idle' | 'loading' | 'success' | 'error';

interface AuthState {
  session: AuthSession | null;
  loginStatus: LoginStatus;
  loginError: string | null;

  /**
   * true wenn Keycloak einen Passwortwechsel erzwingt.
   * In diesem Fall wird ein Formular zum Passwort-Ändern angezeigt.
   */
  passwordChangeRequired: boolean;
  passwordChangeEmail: string | null;

  /** true wenn die E-Mail noch nicht verifiziert wurde */
  emailNotVerified: boolean;

  // Aktionen
  login: (request: AdminLoginRequest) => Promise<void>;
  changePassword: (currentPassword: string, newPassword: string) => Promise<string>;
  logout: () => Promise<void>;
  clearError: () => void;
}

// ---------------------------------------------------------------------------
// Store
// ---------------------------------------------------------------------------

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      session: null,
      loginStatus: 'idle',
      loginError: null,
      passwordChangeRequired: false,
      passwordChangeEmail: null,
      emailNotVerified: false,

      login: async (request: AdminLoginRequest) => {
        set({ loginStatus: 'loading', loginError: null, passwordChangeRequired: false, passwordChangeEmail: null, emailNotVerified: false });

        try {
          const response = await api.adminLogin(request);

          const session: AuthSession = {
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            expiresAt: Date.now() + response.expiresIn * 1000,
            user: response.user,
          };

          set({ session, loginStatus: 'success', loginError: null });
        } catch (err) {
          if (err instanceof PasswordChangeRequiredError) {
            set({
              loginStatus: 'idle',
              loginError: null,
              passwordChangeRequired: true,
              passwordChangeEmail: request.email,
            });
            return;
          }

          if (err instanceof EmailNotVerifiedError) {
            set({
              loginStatus: 'error',
              loginError: err.message,
              emailNotVerified: true,
            });
            return;
          }

          if (err instanceof InsufficientRoleError) {
            set({
              loginStatus: 'error',
              loginError: err.message,
            });
            return;
          }

          const message =
            err instanceof Error ? err.message : 'Anmeldung fehlgeschlagen.';
          set({ loginStatus: 'error', loginError: message });
        }
      },

      changePassword: async (currentPassword: string, newPassword: string) => {
        const email = get().passwordChangeEmail;
        if (!email) throw new Error('Keine E-Mail für Passwortänderung gespeichert');

        const result = await api.changePassword(email, currentPassword, newPassword);
        set({
          passwordChangeRequired: false,
          passwordChangeEmail: null,
          loginError: null,
        });
        return result.message;
      },

      logout: async () => {
        const { session } = get();
        if (session) {
          try {
            await api.adminLogout({ refreshToken: session.refreshToken });
          } catch {
            // Token bereits abgelaufen — Logout trotzdem lokal durchführen
          }
        }
        set({
          session: null,
          loginStatus: 'idle',
          loginError: null,
          passwordChangeRequired: false,
          passwordChangeEmail: null,
          emailNotVerified: false,
        });
      },

      clearError: () => set({ loginError: null, loginStatus: 'idle' }),
    }),
    {
      name: STORAGE_KEY,
      storage: createJSONStorage(() => sessionStorage),
      // Nur Session persistieren, nicht den Ladezustand
      partialize: (state) => ({ session: state.session }),
    },
  ),
);

// ---------------------------------------------------------------------------
// Selektoren
// ---------------------------------------------------------------------------

/** true wenn der Benutzer eingeloggt und das Token noch gültig ist */
export const isAuthenticated = (state: AuthState): boolean =>
  state.session !== null && Date.now() < state.session.expiresAt;
