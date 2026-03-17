import { API_BASE_URL } from '../config/api';
import { ApiErrorResponse } from '../types/ecotrack-api';

interface RequestOptions {
  method?: 'GET' | 'POST';
  token?: string | null;
  body?: unknown;
}

export class ApiError extends Error {
  status: number;
  code: string;
  timestamp?: string;

  constructor(status: number, code: string, message: string, timestamp?: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.code = code;
    this.timestamp = timestamp;
  }
}

export function isApiError(error: unknown): error is ApiError {
  return error instanceof ApiError;
}

export function getErrorMessage(error: unknown) {
  if (isApiError(error)) {
    return error.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'Unbekannter Fehler';
}

export async function requestJson<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', token = null, body } = options;

  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      method,
      headers: {
        Accept: 'application/json',
        ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
    });

    const text = await response.text();
    const payload = text ? (JSON.parse(text) as unknown) : null;

    if (!response.ok) {
      const errorPayload = (payload ?? {}) as Partial<ApiErrorResponse>;
      throw new ApiError(
        response.status,
        errorPayload.code ?? 'REQUEST_FAILED',
        errorPayload.message ?? 'Die Anfrage konnte nicht verarbeitet werden.',
        errorPayload.timestamp,
      );
    }

    return payload as T;
  } catch (error) {
    if (isApiError(error)) {
      throw error;
    }

    throw new ApiError(
      0,
      'NETWORK_ERROR',
      `Backend nicht erreichbar unter ${API_BASE_URL}. Pruefe Server, Emulator und Port-Freigabe.`,
    );
  }
}
