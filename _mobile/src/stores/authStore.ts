/**
 * Auth-Store (Zustand) — Mobile (MS-01)
 *
 * Verwaltet die Auth-Session:
 * - Login, Registrierung, Logout
 * - Passwort-Reset-Request
 * - Token-Persistenz via AsyncStorage
 */

import {
    logout as logoutApi,
    mobileLogin,
    register as registerApi,
    requestPasswordReset as requestResetApi,
} from '@/services/authMockApi';
import type {
    AuthSession,
    LoginRequest,
    PasswordResetRequest,
    RegisterRequest,
} from '@/types/auth';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

const STORAGE_KEY = 'ecotrack-mobile-auth';

// ---------------------------------------------------------------------------
// State-Typen
// ---------------------------------------------------------------------------

type ActionStatus = 'idle' | 'loading' | 'success' | 'error';

interface AuthState {
  session: AuthSession | null;

  loginStatus: ActionStatus;
  loginError: string | null;

  registerStatus: ActionStatus;
  registerError: string | null;

  resetStatus: ActionStatus;
  resetError: string | null;

  // Aktionen
  login: (request: LoginRequest) => Promise<void>;
  register: (request: RegisterRequest) => Promise<void>;
  requestPasswordReset: (request: PasswordResetRequest) => Promise<void>;
  logout: () => Promise<void>;
  clearErrors: () => void;
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
      registerStatus: 'idle',
      registerError: null,
      resetStatus: 'idle',
      resetError: null,

      login: async (request: LoginRequest) => {
        set({ loginStatus: 'loading', loginError: null });
        try {
          const response = await mobileLogin(request);
          const session: AuthSession = {
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            expiresAt: Date.now() + response.expiresIn * 1000,
            user: response.user,
          };
          set({ session, loginStatus: 'success' });
        } catch (err) {
          const message = err instanceof Error ? err.message : 'Anmeldung fehlgeschlagen.';
          set({ loginStatus: 'error', loginError: message });
        }
      },

      register: async (request: RegisterRequest) => {
        set({ registerStatus: 'loading', registerError: null });
        try {
          await registerApi(request);
          set({ registerStatus: 'success' });
        } catch (err) {
          const message = err instanceof Error ? err.message : 'Registrierung fehlgeschlagen.';
          set({ registerStatus: 'error', registerError: message });
        }
      },

      requestPasswordReset: async (request: PasswordResetRequest) => {
        set({ resetStatus: 'loading', resetError: null });
        try {
          await requestResetApi(request);
          set({ resetStatus: 'success' });
        } catch (err) {
          const message = err instanceof Error ? err.message : 'Reset fehlgeschlagen.';
          set({ resetStatus: 'error', resetError: message });
        }
      },

      logout: async () => {
        const { session } = get();
        if (session) {
          try {
            await logoutApi({ refreshToken: session.refreshToken });
          } catch {
            // Token bereits abgelaufen
          }
        }
        set({
          session: null,
          loginStatus: 'idle',
          loginError: null,
          registerStatus: 'idle',
          registerError: null,
          resetStatus: 'idle',
          resetError: null,
        });
      },

      clearErrors: () =>
        set({
          loginError: null,
          loginStatus: 'idle',
          registerError: null,
          registerStatus: 'idle',
          resetError: null,
          resetStatus: 'idle',
        }),
    }),
    {
      name: STORAGE_KEY,
      storage: createJSONStorage(() => AsyncStorage),
      partialize: (state) => ({ session: state.session }),
    },
  ),
);

/** true wenn eingeloggt und Token noch gültig */
export const isAuthenticated = (state: AuthState): boolean =>
  state.session !== null && Date.now() < state.session.expiresAt;
