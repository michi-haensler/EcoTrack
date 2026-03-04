/**
 * Auth-Store (Zustand) — MS-01 Admin-Web
 *
 * Verwaltet die Authentifizierungssession:
 * - Login / Logout
 * - Token-Persistenz im sessionStorage
 * - PasswordChangeRequired-Handling
 */

import { adminLogin, adminLogout } from '@/services/authMockApi';
import type { AdminLoginRequest, AuthSession } from '@/types/auth';
import { PasswordChangeRequiredError } from '@/types/auth';
import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

const STORAGE_KEY = 'ecotrack-admin-auth';

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
   * In diesem Fall muss zur Keycloak-UI weitergeleitet werden.
   */
  passwordChangeRequired: boolean;
  passwordChangeUrl: string | null;

  // Aktionen
  login: (request: AdminLoginRequest) => Promise<void>;
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
      passwordChangeUrl: null,

      login: async (request: AdminLoginRequest) => {
        set({ loginStatus: 'loading', loginError: null, passwordChangeRequired: false });

        try {
          const response = await adminLogin(request);

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
              passwordChangeUrl: err.keycloakChangeUrl,
            });
            return;
          }

          const message =
            err instanceof Error ? err.message : 'Anmeldung fehlgeschlagen.';
          set({ loginStatus: 'error', loginError: message });
        }
      },

      logout: async () => {
        const { session } = get();
        if (session) {
          try {
            await adminLogout({ refreshToken: session.refreshToken });
          } catch {
            // Token bereits abgelaufen — Logout trotzdem lokal durchführen
          }
        }
        set({
          session: null,
          loginStatus: 'idle',
          loginError: null,
          passwordChangeRequired: false,
          passwordChangeUrl: null,
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
