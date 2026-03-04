/**
 * Tests für authMockApi — Mobile (MS-01)
 * Testet alle Auth-API-Flows mit Mock
 */

import {
    __testVerifyEmail,
    logout,
    mobileLogin,
    register,
    requestPasswordReset,
} from '../src/services/authMockApi';

// Delay auf 0 setzen
jest.useFakeTimers();

function flushTimers<T>(promise: Promise<T>): Promise<T> {
  jest.runAllTimers();
  return promise;
}

describe('authMockApi — Mobile', () => {
  // ---------------------------------------------------------------------------
  // register
  // ---------------------------------------------------------------------------

  describe('register', () => {
    it('should_registerStudent_when_validPayloadWithClassId', async () => {
      // Arrange
      const req = {
        email: `s_${Date.now()}@test.test`,
        password: 'Passwort123!',
        firstName: 'Anna',
        lastName: 'Muster',
        role: 'SCHUELER' as const,
        classId: 'some-class-id',
      };

      // Act
      const result = await flushTimers(register(req));

      // Assert
      expect(result.accessToken).toBeTruthy();
      expect(result.user.role).toBe('SCHUELER');
      expect(result.user.email).toBe(req.email);
    });

    it('should_throwEmailExists_when_emailAlreadyRegistered', async () => {
      // Arrange — ersten User anlegen
      const email = `dup_${Date.now()}@test.test`;
      await flushTimers(register({
        email,
        password: 'Passwort123!',
        firstName: 'A',
        lastName: 'B',
        role: 'LEHRER',
      }));

      // Act & Assert
      await expect(
        flushTimers(register({
          email,
          password: 'Passwort123!',
          firstName: 'A',
          lastName: 'B',
          role: 'LEHRER',
        })),
      ).rejects.toMatchObject({ code: 'EMAIL_EXISTS' });
    });

    it('should_throwClassRequired_when_studentRegistersWithoutClassId', async () => {
      // Arrange & Act & Assert
      await expect(
        flushTimers(register({
          email: `s_noclass_${Date.now()}@test.test`,
          password: 'Passwort123!',
          firstName: 'No',
          lastName: 'Class',
          role: 'SCHUELER',
          classId: null,
        })),
      ).rejects.toMatchObject({ code: 'CLASS_REQUIRED' });
    });

    it('should_throwWeakPassword_when_passwordTooShort', async () => {
      // Arrange & Act & Assert
      await expect(
        flushTimers(register({
          email: `weak_${Date.now()}@test.test`,
          password: 'short',
          firstName: 'A',
          lastName: 'B',
          role: 'LEHRER',
        })),
      ).rejects.toMatchObject({ code: 'WEAK_PASSWORD' });
    });
  });

  // ---------------------------------------------------------------------------
  // mobileLogin
  // ---------------------------------------------------------------------------

  describe('mobileLogin', () => {
    it('should_loginSuccessfully_when_emailVerifiedAndCorrectCredentials', async () => {
      // Arrange — existierenden verifizierten Nutzer verwenden
      // Act
      const result = await flushTimers(
        mobileLogin({ email: 'student@ecotrack.test', password: 'Passwort123!' }),
      );

      // Assert
      expect(result.accessToken).toBeTruthy();
      expect(result.user.role).toBe('SCHUELER');
    });

    it('should_throwInvalidCredentials_when_wrongPassword', async () => {
      // Arrange & Act & Assert
      await expect(
        flushTimers(mobileLogin({ email: 'student@ecotrack.test', password: 'WrongPass!' })),
      ).rejects.toMatchObject({ code: 'INVALID_CREDENTIALS' });
    });

    it('should_throwEmailNotVerified_when_loginBeforeVerification', async () => {
      // Arrange — neuen Benutzer registrieren (emailVerified = false)
      const email = `unverified_${Date.now()}@test.test`;
      await flushTimers(register({
        email,
        password: 'Passwort123!',
        firstName: 'Unverifiziert',
        lastName: 'User',
        role: 'LEHRER',
      }));

      // Act & Assert — Login schlägt fehl vor Verifikation
      await expect(
        flushTimers(mobileLogin({ email, password: 'Passwort123!' })),
      ).rejects.toMatchObject({ code: 'EMAIL_NOT_VERIFIED' });
    });

    it('should_loginSuccessfully_after_emailVerification', async () => {
      // Arrange — registrieren und verifizieren
      const email = `verified_${Date.now()}@test.test`;
      await flushTimers(register({
        email,
        password: 'Passwort123!',
        firstName: 'Verifiziert',
        lastName: 'User',
        role: 'LEHRER',
      }));
      __testVerifyEmail(email);

      // Act
      const result = await flushTimers(mobileLogin({ email, password: 'Passwort123!' }));

      // Assert
      expect(result.accessToken).toBeTruthy();
    });
  });

  // ---------------------------------------------------------------------------
  // requestPasswordReset
  // ---------------------------------------------------------------------------

  describe('requestPasswordReset', () => {
    it('should_resolveSuccessfully_when_emailExists', async () => {
      // Arrange & Act & Assert
      await expect(
        flushTimers(requestPasswordReset({ email: 'student@ecotrack.test' })),
      ).resolves.toBeUndefined();
    });

    it('should_resolveSuccessfully_even_when_emailNotFound', async () => {
      // Arrange & Act & Assert — kein User-Enumeration
      await expect(
        flushTimers(requestPasswordReset({ email: 'unknown@ecotrack.test' })),
      ).resolves.toBeUndefined();
    });
  });

  // ---------------------------------------------------------------------------
  // logout
  // ---------------------------------------------------------------------------

  describe('logout', () => {
    it('should_resolveSuccessfully_with_validRefreshToken', async () => {
      // Arrange
      const loginResult = await flushTimers(
        mobileLogin({ email: 'student@ecotrack.test', password: 'Passwort123!' }),
      );

      // Act & Assert
      await expect(
        flushTimers(logout({ refreshToken: loginResult.refreshToken })),
      ).resolves.toBeUndefined();
    });

    it('should_resolveSuccessfully_with_unknownToken', async () => {
      // Arrange & Act & Assert — idempotent
      await expect(
        flushTimers(logout({ refreshToken: 'unknown-token' })),
      ).resolves.toBeUndefined();
    });
  });
});
