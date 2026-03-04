/**
 * Auth-Typen für die Mobile-App (MS-01)
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

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  classId?: string | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LogoutRequest {
  refreshToken: string;
}

export interface PasswordResetRequest {
  email: string;
}

/** Auth-Session im Store */
export interface AuthSession {
  accessToken: string;
  refreshToken: string;
  expiresAt: number; // Unix-Timestamp ms
  user: UserInfo;
}

/** API-Fehler */
export interface ApiError extends Error {
  code: string;
  status: number;
}

/** Registrierungs状態 */
export type RegistrationStatus = 'idle' | 'loading' | 'success' | 'error';
