/**
 * Auth-API-Service — Echte Backend-Kommunikation (MS-01)
 *
 * Endpunkte:
 * - POST /api/v1/auth/admin/login
 * - POST /api/v1/auth/logout
 * - POST /api/v1/auth/password/reset-request
 * - GET  /api/v1/users/me
 */

import apiClient from '@/api/apiClient';
import type {
    AdminLoginRequest,
    ApiErrorResponse,
    AuthResponse,
    LogoutRequest,
    UserInfo,
} from '@/types/auth';
import { EmailNotVerifiedError, InsufficientRoleError, PasswordChangeRequiredError } from '@/types/auth';
import { AxiosError } from 'axios';

// ---------------------------------------------------------------------------
// Keycloak-URL für Passwortänderungs-Redirect (nicht mehr verwendet, aber als Fallback)
// ---------------------------------------------------------------------------

function getKeycloakPasswordChangeUrl(): string {
  const baseUrl = import.meta.env.VITE_KEYCLOAK_URL ?? 'http://localhost:8180';
  const realm = import.meta.env.VITE_KEYCLOAK_REALM ?? 'ecotrack';
  return `${baseUrl}/realms/${realm}/login-actions/required-action?execution=UPDATE_PASSWORD`;
}

// ---------------------------------------------------------------------------
// Fehlerbehandlung
// ---------------------------------------------------------------------------

function handleApiError(error: unknown): never {
  if (error instanceof AxiosError && error.response?.data) {
    const body = error.response.data as ApiErrorResponse;

    if (body.code === 'PASSWORD_CHANGE_REQUIRED') {
      throw new PasswordChangeRequiredError(getKeycloakPasswordChangeUrl());
    }

    if (body.code === 'EMAIL_NOT_VERIFIED') {
      throw new EmailNotVerifiedError(body.message || 'E-Mail-Adresse wurde noch nicht verifiziert.');
    }

    if (body.code === 'INSUFFICIENT_ROLE') {
      throw new InsufficientRoleError(body.message || 'Keine Berechtigung für das Admin-Dashboard.');
    }

    const err = new Error(body.message || 'Anmeldung fehlgeschlagen.');
    (err as Error & { code: string; status: number }).code = body.code;
    (err as Error & { code: string; status: number }).status = error.response.status;
    throw err;
  }

  if (error instanceof AxiosError && !error.response) {
    throw new Error('Backend nicht erreichbar. Ist der Server gestartet?');
  }

  throw error;
}

// ---------------------------------------------------------------------------
// API-Methoden
// ---------------------------------------------------------------------------

/**
 * POST /api/v1/auth/admin/login
 *
 * @throws {PasswordChangeRequiredError} wenn mustChangePassword=true
 * @throws {Error} bei falschen Credentials (code: INVALID_CREDENTIALS)
 */
export async function adminLogin(request: AdminLoginRequest): Promise<AuthResponse> {
  try {
    const { data } = await apiClient.post<AuthResponse>(
      '/api/v1/auth/admin/login',
      request,
    );
    return data;
  } catch (error) {
    handleApiError(error);
  }
}

/**
 * POST /api/v1/auth/logout
 * Invalidiert die Session in Keycloak.
 */
export async function adminLogout(request: LogoutRequest): Promise<void> {
  try {
    await apiClient.post('/api/v1/auth/logout', request);
  } catch {
    // Token bereits abgelaufen — Logout trotzdem lokal durchführen
  }
}

/**
 * POST /api/v1/auth/password/reset-request
 */
export async function requestPasswordReset(email: string): Promise<void> {
  await apiClient.post('/api/v1/auth/password/reset-request', { email });
}

/**
 * GET /api/v1/users/me
 */
export async function getCurrentUser(): Promise<UserInfo> {
  const { data } = await apiClient.get<UserInfo>('/api/v1/users/me');
  return data;
}

/**
 * POST /api/v1/auth/password/change
 * Ändert das Passwort (verifiziert aktuelles Passwort via ROPC, setzt neues via Admin API).
 */
export async function changePassword(
  email: string,
  currentPassword: string,
  newPassword: string,
): Promise<{ message: string }> {
  try {
    const { data } = await apiClient.post<{ message: string }>(
      '/api/v1/auth/password/change',
      { email, currentPassword, newPassword },
    );
    return data;
  } catch (error) {
    handleApiError(error);
  }
}
