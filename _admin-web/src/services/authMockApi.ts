/**
 * Mock-API-Service für Auth-Flows (MS-01)
 *
 * Simuliert die Backend-Endpunkte ohne echtes Backend:
 * - POST /api/v1/auth/admin/login
 * - POST /api/v1/auth/logout
 *
 * Kann über die Umgebungsvariable VITE_USE_MOCK_API auf false gesetzt werden,
 * um auf das echte Backend zu wechseln.
 */

import type {
    AdminLoginRequest,
    AuthResponse,
    LogoutRequest,
    UserInfo,
} from '@/types/auth';
import { PasswordChangeRequiredError } from '@/types/auth';

const MOCK_DELAY_MS = 600;

/** Simuliert eine Netzwerkverzögerung */
const delay = (ms: number) => new Promise((r) => setTimeout(r, ms));

// ---------------------------------------------------------------------------
// Mock-Datenbank
// ---------------------------------------------------------------------------

interface MockUser {
  email: string;
  password: string;
  user: UserInfo;
  mustChangePassword: boolean;
}

const MOCK_USERS: MockUser[] = [
  {
    email: 'admin@ecotrack.test',
    password: 'Admin123!',
    user: {
      userId: '00000001-0000-0000-0000-000000000001',
      ecoUserId: '00000001-0000-0000-0000-000000000001',
      email: 'admin@ecotrack.test',
      firstName: 'System',
      lastName: 'Admin',
      role: 'ADMIN',
    },
    mustChangePassword: false,
  },
  {
    email: 'lehrer@ecotrack.test',
    password: 'Lehrer123!',
    user: {
      userId: '00000001-0000-0000-0000-000000000002',
      ecoUserId: '00000001-0000-0000-0000-000000000002',
      email: 'lehrer@ecotrack.test',
      firstName: 'Maria',
      lastName: 'Muster',
      role: 'LEHRER',
    },
    mustChangePassword: false,
  },
  {
    // Testet den update_password-Flow
    email: 'new-admin@ecotrack.test',
    password: 'TempPass123!',
    user: {
      userId: '00000001-0000-0000-0000-000000000003',
      ecoUserId: '00000001-0000-0000-0000-000000000003',
      email: 'new-admin@ecotrack.test',
      firstName: 'Neuer',
      lastName: 'Admin',
      role: 'ADMIN',
    },
    mustChangePassword: true,
  },
];

// Aktive Sessions (refreshToken → userId)
const activeSessions = new Map<string, string>();

// ---------------------------------------------------------------------------
// Hilfsfunktionen
// ---------------------------------------------------------------------------

function generateFakeToken(): string {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  return Array.from({ length: 64 }, () => chars[Math.floor(Math.random() * chars.length)]).join('');
}

// ---------------------------------------------------------------------------
// API-Methoden
// ---------------------------------------------------------------------------

/**
 * Simuliert POST /api/v1/auth/admin/login
 *
 * @throws {PasswordChangeRequiredError} wenn mustChangePassword=true
 * @throws {Error} bei falschen Credentials (code: INVALID_CREDENTIALS)
 */
export async function adminLogin(request: AdminLoginRequest): Promise<AuthResponse> {
  await delay(MOCK_DELAY_MS);

  const found = MOCK_USERS.find(
    (u) =>
      u.email.toLowerCase() === request.email.toLowerCase() &&
      u.password === request.password,
  );

  if (!found) {
    const error = new Error('E-Mail oder Passwort falsch');
    (error as Error & { code: string; status: number }).code = 'INVALID_CREDENTIALS';
    (error as Error & { code: string; status: number }).status = 401;
    throw error;
  }

  if (found.mustChangePassword) {
    const keycloakUrl = `${import.meta.env.VITE_KEYCLOAK_URL ?? 'http://localhost:8180'}/realms/${import.meta.env.VITE_KEYCLOAK_REALM ?? 'ecotrack'}/login-actions/required-action?execution=UPDATE_PASSWORD`;
    throw new PasswordChangeRequiredError(keycloakUrl);
  }

  const accessToken = generateFakeToken();
  const refreshToken = generateFakeToken();

  activeSessions.set(refreshToken, found.user.userId);

  return {
    accessToken,
    refreshToken,
    expiresIn: 900, // 15 Minuten
    user: found.user,
  };
}

/**
 * Simuliert POST /api/v1/auth/logout
 * Löscht die Session anhand des Refresh-Tokens.
 */
export async function adminLogout(request: LogoutRequest): Promise<void> {
  await delay(200);
  activeSessions.delete(request.refreshToken);
}
