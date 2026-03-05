/**
 * Tests für authMockApi (MS-01)
 * AAA-Pattern: Arrange → Act → Assert
 */

import { PasswordChangeRequiredError } from '@/types/auth';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { adminLogin, adminLogout } from '../authMockApi';

// Netzwerk-Delay auf 0 setzen für schnelle Tests
vi.mock('../authMockApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../authMockApi')>();
  return actual;
});

describe('authMockApi', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  // ---------------------------------------------------------------------------
  // adminLogin — Happy Path
  // ---------------------------------------------------------------------------

  it('should_returnAuthResponse_when_validAdminCredentials', async () => {
    // Arrange
    const promise = adminLogin({ email: 'admin@ecotrack.test', password: 'Admin123!' });
    vi.runAllTimers();

    // Act
    const result = await promise;

    // Assert
    expect(result.accessToken).toBeTruthy();
    expect(result.refreshToken).toBeTruthy();
    expect(result.expiresIn).toBe(900);
    expect(result.user.role).toBe('ADMIN');
    expect(result.user.email).toBe('admin@ecotrack.test');
  });

  it('should_returnAuthResponse_when_validTeacherCredentials', async () => {
    // Arrange
    const promise = adminLogin({ email: 'lehrer@ecotrack.test', password: 'Lehrer123!' });
    vi.runAllTimers();

    // Act
    const result = await promise;

    // Assert
    expect(result.user.role).toBe('LEHRER');
  });

  // ---------------------------------------------------------------------------
  // adminLogin — Fail-Pfade
  // ---------------------------------------------------------------------------

  it('should_throwError_when_invalidPassword', async () => {
    // Arrange
    const promise = adminLogin({ email: 'admin@ecotrack.test', password: 'WrongPassword!' });
    vi.runAllTimers();

    // Act & Assert
    await expect(promise).rejects.toThrow('E-Mail oder Passwort falsch');
  });

  it('should_throwError_when_emailNotFound', async () => {
    // Arrange
    const promise = adminLogin({ email: 'unknown@ecotrack.test', password: 'Any123!' });
    vi.runAllTimers();

    // Act & Assert
    await expect(promise).rejects.toThrow();
  });

  it('should_throwPasswordChangeRequiredError_when_mustChangePassword', async () => {
    // Arrange
    const promise = adminLogin({ email: 'new-admin@ecotrack.test', password: 'TempPass123!' });
    vi.runAllTimers();

    // Act & Assert
    await expect(promise).rejects.toBeInstanceOf(PasswordChangeRequiredError);
  });

  it('should_includeKeycloakUrl_in_PasswordChangeRequiredError', async () => {
    // Arrange
    const promise = adminLogin({ email: 'new-admin@ecotrack.test', password: 'TempPass123!' });
    vi.runAllTimers();

    // Act & Assert
    try {
      await promise;
      expect.fail('Sollte eine Exception werfen');
    } catch (err) {
      expect(err).toBeInstanceOf(PasswordChangeRequiredError);
      expect((err as PasswordChangeRequiredError).keycloakChangeUrl).toContain('UPDATE_PASSWORD');
    }
  });

  // ---------------------------------------------------------------------------
  // adminLogout
  // ---------------------------------------------------------------------------

  it('should_logoutSuccessfully_when_validRefreshToken', async () => {
    // Arrange — erst einloggen um ein echtes refreshToken zu erhalten
    const loginPromise = adminLogin({ email: 'admin@ecotrack.test', password: 'Admin123!' });
    vi.runAllTimers();
    const session = await loginPromise;

    // Act — Logout
    const logoutPromise = adminLogout({ refreshToken: session.refreshToken });
    vi.runAllTimers();

    // Assert — kein Fehler
    await expect(logoutPromise).resolves.toBeUndefined();
  });

  it('should_logoutSilently_when_unknownRefreshToken', async () => {
    // Arrange & Act — unbekanntes Token → kein Fehler (idempotent)
    const promise = adminLogout({ refreshToken: 'unknown-token' });
    vi.runAllTimers();

    // Assert
    await expect(promise).resolves.toBeUndefined();
  });
});
