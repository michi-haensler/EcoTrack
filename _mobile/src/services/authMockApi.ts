/**
 * Mock-API-Service für Auth-Flows — Mobile (MS-01)
 *
 * Simuliert die Backend-Endpunkte ohne echtes Backend:
 * - POST /api/v1/registration
 * - POST /api/v1/auth/mobile/login
 * - POST /api/v1/auth/password/reset-request
 * - POST /api/v1/auth/logout
 */

import type {
    AuthResponse,
    LoginRequest,
    LogoutRequest,
    PasswordResetRequest,
    RegisterRequest,
    UserInfo,
} from '@/types/auth';

const MOCK_DELAY_MS = 600;

const delay = (ms: number) => new Promise<void>((r) => setTimeout(r, ms));

// ---------------------------------------------------------------------------
// Mock-Datenbank
// ---------------------------------------------------------------------------

interface MockUser {
  email: string;
  password: string;
  user: UserInfo;
  emailVerified: boolean;
}

// Im Speicher gespeicherte Nutzer (wird durch register() befüllt)
const mockUsers: MockUser[] = [
  {
    email: 'student@ecotrack.test',
    password: 'Passwort123!',
    user: {
      userId: 'aaa00001-0000-0000-0000-000000000001',
      ecoUserId: 'aaa00001-0000-0000-0000-000000000001',
      email: 'student@ecotrack.test',
      firstName: 'Anna',
      lastName: 'Muster',
      role: 'SCHUELER',
    },
    emailVerified: true,
  },
];

const activeSessions = new Map<string, string>();

// ---------------------------------------------------------------------------
// Hilfsfunktionen
// ---------------------------------------------------------------------------

function generateFakeToken(): string {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  return Array.from({ length: 64 }, () => chars[Math.floor(Math.random() * chars.length)]).join('');
}

function makeApiError(message: string, code: string, status: number): Error {
  const err = new Error(message) as Error & { code: string; status: number };
  err.code = code;
  err.status = status;
  return err;
}

function validatePassword(password: string): boolean {
  // mind. 8 Zeichen, 1 Großbuchstabe, 1 Zahl, 1 Sonderzeichen
  return (
    password.length >= 8 &&
    /[A-Z]/.test(password) &&
    /[0-9]/.test(password) &&
    /[^A-Za-z0-9]/.test(password)
  );
}

// ---------------------------------------------------------------------------
// API-Methoden
// ---------------------------------------------------------------------------

/**
 * Simuliert POST /api/v1/registration
 *
 * Nach erfolgreicher Registrierung muss die E-Mail verifiziert werden
 * (emailVerified = false). Der Login schlägt bis dahin fehl.
 */
export async function register(request: RegisterRequest): Promise<AuthResponse> {
  await delay(MOCK_DELAY_MS);

  // Validierungen
  if (mockUsers.some((u) => u.email.toLowerCase() === request.email.toLowerCase())) {
    throw makeApiError('E-Mail ist bereits registriert', 'EMAIL_EXISTS', 400);
  }

  if (request.role === 'SCHUELER' && !request.classId) {
    throw makeApiError('classId ist für Schüler erforderlich', 'CLASS_REQUIRED', 400);
  }

  if (!validatePassword(request.password)) {
    throw makeApiError(
      'Passwort muss mind. 8 Zeichen, 1 Großbuchstabe, 1 Zahl und 1 Sonderzeichen haben',
      'WEAK_PASSWORD',
      400,
    );
  }

  const newUser: UserInfo = {
    userId: `user-${Date.now()}`,
    ecoUserId: `eco-${Date.now()}`,
    email: request.email.toLowerCase(),
    firstName: request.firstName,
    lastName: request.lastName,
    role: request.role,
  };

  // Neu registrierte Nutzer müssen E-Mail verifizieren
  mockUsers.push({
    email: request.email.toLowerCase(),
    password: request.password,
    user: newUser,
    emailVerified: false,
  });

  // Für Demo-Zwecke trotzdem Token zurückgeben (echtes Backend wartet auf Verifikation)
  const accessToken = generateFakeToken();
  const refreshToken = generateFakeToken();
  activeSessions.set(refreshToken, newUser.userId);

  return { accessToken, refreshToken, expiresIn: 900, user: newUser };
}

/**
 * Simuliert POST /api/v1/auth/mobile/login
 */
export async function mobileLogin(request: LoginRequest): Promise<AuthResponse> {
  await delay(MOCK_DELAY_MS);

  const found = mockUsers.find(
    (u) =>
      u.email.toLowerCase() === request.email.toLowerCase() &&
      u.password === request.password,
  );

  if (!found) {
    throw makeApiError('E-Mail oder Passwort falsch', 'INVALID_CREDENTIALS', 401);
  }

  if (!found.emailVerified) {
    throw makeApiError(
      'E-Mail wurde noch nicht verifiziert. Bitte prüfe deinen Posteingang.',
      'EMAIL_NOT_VERIFIED',
      403,
    );
  }

  const accessToken = generateFakeToken();
  const refreshToken = generateFakeToken();
  activeSessions.set(refreshToken, found.user.userId);

  return { accessToken, refreshToken, expiresIn: 900, user: found.user };
}

/**
 * Simuliert POST /api/v1/auth/password/reset-request
 * Gibt immer HTTP 202 zurück (Sicherheit: kein User-Enumeration).
 */
export async function requestPasswordReset(request: PasswordResetRequest): Promise<void> {
  await delay(MOCK_DELAY_MS);
  // Im Mock: nur simulieren, kein echter E-Mail-Versand
  console.info(`[Mock] Passwort-Reset-E-Mail gesendet an: ${request.email}`);
}

/**
 * Simuliert POST /api/v1/auth/logout
 * Löscht die Session im Mock.
 */
export async function logout(request: LogoutRequest): Promise<void> {
  await delay(200);
  activeSessions.delete(request.refreshToken);
}

// ---------------------------------------------------------------------------
// Hilfsfunktion für Tests: E-Mail als verifiziert markieren
// ---------------------------------------------------------------------------
export function __testVerifyEmail(email: string): void {
  const user = mockUsers.find((u) => u.email === email.toLowerCase());
  if (user) {
    user.emailVerified = true;
  }
}
