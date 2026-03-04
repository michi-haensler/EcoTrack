/**
 * Auth-Typen für die Admin-Web-App (MS-01)
 */

export type UserRole = 'SCHUELER' | 'LEHRER' | 'ADMIN';

export interface UserInfo {
  userId: string;
  ecoUserId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: UserInfo;
}

export interface AdminLoginRequest {
  email: string;
  password: string;
}

export interface LogoutRequest {
  refreshToken: string;
}

/** API-Fehlerantwort vom Backend */
export interface ApiErrorResponse {
  code: string;
  message: string;
  status: number;
}

/**
 * Wird geworfen, wenn Keycloak einen Passwortwechsel erzwingt.
 * Code: PASSWORD_CHANGE_REQUIRED
 */
export class PasswordChangeRequiredError extends Error {
  constructor(public readonly keycloakChangeUrl: string) {
    super('Passwort muss geändert werden');
    this.name = 'PasswordChangeRequiredError';
  }
}

/** Auth-Session im Store */
export interface AuthSession {
  accessToken: string;
  refreshToken: string;
  expiresAt: number; // Unix-Timestamp ms
  user: UserInfo;
}
