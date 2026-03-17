import {
  ActionDefinitionResponse,
  ActivityPageResponse,
  AuthResponse,
  CreateActivityRequest,
  EcoUserProfileResponse,
  LoginRequest,
  PasswordResetRequest,
  ProgressSnapshotResponse,
  RegistrationRequest,
  RegistrationResponse,
} from '../types/ecotrack-api';
import { requestJson } from './api-client';

export function loginStudent(request: LoginRequest) {
  return requestJson<AuthResponse>('/v1/auth/mobile/login', {
    method: 'POST',
    body: request,
  });
}

export function registerStudent(request: RegistrationRequest) {
  return requestJson<RegistrationResponse>('/v2/registration', {
    method: 'POST',
    body: request,
  });
}

export function requestPasswordReset(request: PasswordResetRequest) {
  return requestJson<void>('/auth/password/reset-request', {
    method: 'POST',
    body: request,
  });
}

export function logout(refreshToken: string | null, accessToken: string | null) {
  return requestJson<void>('/auth/logout', {
    method: 'POST',
    token: accessToken,
    ...(refreshToken ? { body: { refreshToken } } : {}),
  });
}

export function fetchMyProfile(accessToken: string) {
  return requestJson<EcoUserProfileResponse>('/eco-users/me', {
    token: accessToken,
  });
}

export function fetchMyProgress(accessToken: string) {
  return requestJson<ProgressSnapshotResponse>('/progress', {
    token: accessToken,
  });
}

export function fetchActivities(accessToken: string, category?: string) {
  const suffix = category ? `?size=20&category=${category}` : '?size=20';
  return requestJson<ActivityPageResponse>(`/activities${suffix}`, {
    token: accessToken,
  });
}

export function fetchCatalog(accessToken: string) {
  return requestJson<ActionDefinitionResponse[]>('/activities/catalog', {
    token: accessToken,
  });
}

export function createActivity(accessToken: string, request: CreateActivityRequest) {
  return requestJson('/activities', {
    method: 'POST',
    token: accessToken,
    body: request,
  });
}
