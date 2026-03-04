/**
 * Tests für LoginScreen — Mobile (MS-01)
 * @testing-library/react-native
 */

import { fireEvent, render, waitFor } from '@testing-library/react-native';

// --- Mocks -------------------------------------------------------------------

// Navigation
const mockReset = jest.fn();
const mockNavigate = jest.fn();
jest.mock('@react-navigation/native', () => ({
  useNavigation: () => ({ navigate: mockNavigate, reset: mockReset }),
}));

// authStore
const mockLogin = jest.fn();
const mockClearErrors = jest.fn();
let mockLoginStatus: string = 'idle';
let mockLoginError: string | null = null;
let mockAuthenticated = false;

jest.mock('@/stores/authStore', () => ({
  useAuthStore: (selector: (s: unknown) => unknown) =>
    selector({
      login: mockLogin,
      loginStatus: mockLoginStatus,
      loginError: mockLoginError,
      clearErrors: mockClearErrors,
    }),
  isAuthenticated: (s: { session: unknown }) => mockAuthenticated,
}));

// Fake fake-timers
jest.useFakeTimers();

import { LoginScreen } from '../src/screens/LoginScreen';

// ---------------------------------------------------------------------------

beforeEach(() => {
  jest.clearAllMocks();
  mockLoginStatus = 'idle';
  mockLoginError = null;
  mockAuthenticated = false;
});

describe('LoginScreen', () => {
  it('should_renderAllFormElements', () => {
    // Arrange & Act
    const { getByLabelText, getByText } = render(<LoginScreen />);

    // Assert
    expect(getByLabelText('E-Mail-Adresse')).toBeTruthy();
    expect(getByLabelText('Passwort')).toBeTruthy();
    expect(getByText(/Anmelden/i)).toBeTruthy();
    expect(getByText(/Passwort vergessen/i)).toBeTruthy();
    expect(getByText(/Registrieren/i)).toBeTruthy();
  });

  it('should_callLogin_when_formSubmitted', async () => {
    // Arrange
    mockLogin.mockResolvedValue(undefined);
    const { getByLabelText, getByText } = render(<LoginScreen />);

    // Act
    fireEvent.changeText(getByLabelText('E-Mail-Adresse'), 'lehrer@ecotrack.test');
    fireEvent.changeText(getByLabelText('Passwort'), 'Passwort123!');
    fireEvent.press(getByText(/Anmelden/i));

    // Assert
    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith({
        email: 'lehrer@ecotrack.test',
        password: 'Passwort123!',
      });
    });
  });

  it('should_showError_when_invalidCredentials', () => {
    // Arrange
    mockLoginStatus = 'error';
    mockLoginError = 'Ungültige Anmeldedaten.';

    // Act
    const { getByText } = render(<LoginScreen />);

    // Assert
    expect(getByText(/Ungültige Anmeldedaten/i)).toBeTruthy();
  });

  it('should_showEmailNotVerifiedMessage_when_errorIsEmailNotVerified', () => {
    // Arrange
    mockLoginStatus = 'error';
    mockLoginError = 'Bitte bestätige zuerst deine E-Mail-Adresse.';

    // Act
    const { getByText } = render(<LoginScreen />);

    // Assert
    expect(getByText(/bestätige/i)).toBeTruthy();
  });

  it('should_navigateToRegister_when_registerLinkPressed', () => {
    // Arrange
    const { getByText } = render(<LoginScreen />);

    // Act
    fireEvent.press(getByText(/Registrieren/i));

    // Assert
    expect(mockNavigate).toHaveBeenCalledWith('Register');
  });

  it('should_navigateToForgotPassword_when_linkPressed', () => {
    // Arrange
    const { getByText } = render(<LoginScreen />);

    // Act
    fireEvent.press(getByText(/Passwort vergessen/i));

    // Assert
    expect(mockNavigate).toHaveBeenCalledWith('ForgotPassword');
  });
});
