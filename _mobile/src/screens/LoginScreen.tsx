/**
 * LoginScreen — Mobile (MS-01)
 *
 * Abgedeckte Akzeptanzkriterien:
 * - E-Mail + Passwort Login
 * - Loading-Zustand
 * - Fehlermeldung bei falschen Credentials
 * - E-Mail-Verifikations-Hinweis (EMAIL_NOT_VERIFIED)
 * - Navigation zu Register/Passwort-Vergessen
 */

import type { AuthStackParamList } from '@/navigation/types';
import { isAuthenticated, useAuthStore } from '@/stores/authStore';
import { useNavigation } from '@react-navigation/native';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import React, { useEffect, useRef } from 'react';
import {
    ActivityIndicator,
    Alert,
    KeyboardAvoidingView,
    Platform,
    ScrollView,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
    type TextInput as TextInputType,
} from 'react-native';

type LoginNavigationProp = NativeStackNavigationProp<AuthStackParamList, 'Login'>;

const COLORS = {
  primary: '#16a34a',
  primaryDark: '#15803d',
  error: '#dc2626',
  errorBg: '#fef2f2',
  text: '#111827',
  textSecondary: '#6b7280',
  border: '#d1d5db',
  borderFocused: '#16a34a',
  background: '#f9fafb',
  white: '#ffffff',
  disabled: '#9ca3af',
} as const;

export function LoginScreen() {
  const navigation = useNavigation<LoginNavigationProp>();

  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [emailFocused, setEmailFocused] = React.useState(false);
  const [passwordFocused, setPasswordFocused] = React.useState(false);

  const passwordRef = useRef<TextInputType>(null);

  const login = useAuthStore((s) => s.login);
  const loginStatus = useAuthStore((s) => s.loginStatus);
  const loginError = useAuthStore((s) => s.loginError);
  const clearErrors = useAuthStore((s) => s.clearErrors);
  const authenticated = useAuthStore(isAuthenticated);

  // Falls bereits eingeloggt → zum Haupt-Screen navigieren
  useEffect(() => {
    if (authenticated) {
      navigation.reset({ index: 0, routes: [{ name: 'Home' as never }] });
    }
  }, [authenticated, navigation]);

  const isLoading = loginStatus === 'loading';

  async function handleLogin() {
    if (!email.trim() || !password) {
      Alert.alert('Pflichtfelder', 'Bitte E-Mail und Passwort eingeben.');
      return;
    }
    clearErrors();
    await login({ email: email.trim(), password });
  }

  return (
    <KeyboardAvoidingView
      style={styles.flex}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <ScrollView
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled"
      >
        {/* Header */}
        <View style={styles.header}>
          <Text style={styles.logo} accessibilityRole="image" accessibilityLabel="EcoTrack Logo">
            🌿
          </Text>
          <Text style={styles.title}>Willkommen zurück</Text>
          <Text style={styles.subtitle}>Melde dich mit deinem EcoTrack-Konto an</Text>
        </View>

        {/* Formular */}
        <View style={styles.form}>
          {/* E-Mail */}
          <View style={styles.fieldContainer}>
            <Text style={styles.label}>E-Mail-Adresse</Text>
            <TextInput
              style={[styles.input, emailFocused && styles.inputFocused]}
              value={email}
              onChangeText={setEmail}
              onFocus={() => setEmailFocused(true)}
              onBlur={() => setEmailFocused(false)}
              autoCapitalize="none"
              autoCorrect={false}
              keyboardType="email-address"
              textContentType="emailAddress"
              returnKeyType="next"
              onSubmitEditing={() => passwordRef.current?.focus()}
              placeholder="name@schule.at"
              placeholderTextColor={COLORS.textSecondary}
              editable={!isLoading}
              accessibilityLabel="E-Mail-Adresse"
              accessibilityRequired={true}
            />
          </View>

          {/* Passwort */}
          <View style={styles.fieldContainer}>
            <View style={styles.labelRow}>
              <Text style={styles.label}>Passwort</Text>
              <TouchableOpacity
                onPress={() => navigation.navigate('ForgotPassword')}
                accessibilityRole="link"
                accessibilityLabel="Passwort vergessen"
              >
                <Text style={styles.forgotLink}>Passwort vergessen?</Text>
              </TouchableOpacity>
            </View>
            <TextInput
              ref={passwordRef}
              style={[styles.input, passwordFocused && styles.inputFocused]}
              value={password}
              onChangeText={setPassword}
              onFocus={() => setPasswordFocused(true)}
              onBlur={() => setPasswordFocused(false)}
              secureTextEntry
              textContentType="password"
              returnKeyType="done"
              onSubmitEditing={handleLogin}
              placeholder="••••••••"
              placeholderTextColor={COLORS.textSecondary}
              editable={!isLoading}
              accessibilityLabel="Passwort"
              accessibilityRequired={true}
            />
          </View>

          {/* Fehlermeldung */}
          {loginError ? (
            <View
              style={styles.errorBox}
              accessibilityRole="alert"
              accessibilityLiveRegion="assertive"
            >
              <Text style={styles.errorText}>{loginError}</Text>
            </View>
          ) : null}

          {/* Login-Button */}
          <TouchableOpacity
            style={[styles.button, isLoading && styles.buttonDisabled]}
            onPress={handleLogin}
            disabled={isLoading}
            accessibilityRole="button"
            accessibilityLabel={isLoading ? 'Wird angemeldet' : 'Anmelden'}
            accessibilityState={{ busy: isLoading }}
          >
            {isLoading ? (
              <ActivityIndicator color={COLORS.white} />
            ) : (
              <Text style={styles.buttonText}>Anmelden</Text>
            )}
          </TouchableOpacity>
        </View>

        {/* Registrierungs-Link */}
        <View style={styles.footer}>
          <Text style={styles.footerText}>Noch kein Konto? </Text>
          <TouchableOpacity
            onPress={() => navigation.navigate('Register')}
            accessibilityRole="link"
            accessibilityLabel="Registrieren"
          >
            <Text style={styles.footerLink}>Registrieren</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1, backgroundColor: COLORS.background },
  container: {
    flexGrow: 1,
    justifyContent: 'center',
    paddingHorizontal: 24,
    paddingVertical: 40,
  },
  header: { alignItems: 'center', marginBottom: 36 },
  logo: { fontSize: 56, marginBottom: 12 },
  title: { fontSize: 24, fontWeight: '700', color: COLORS.text, marginBottom: 6 },
  subtitle: { fontSize: 14, color: COLORS.textSecondary, textAlign: 'center' },
  form: { gap: 16 },
  fieldContainer: { gap: 6 },
  label: { fontSize: 14, fontWeight: '500', color: COLORS.text },
  labelRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  input: {
    borderWidth: 1,
    borderColor: COLORS.border,
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 16,
    color: COLORS.text,
    backgroundColor: COLORS.white,
  },
  inputFocused: { borderColor: COLORS.borderFocused },
  forgotLink: { fontSize: 13, color: COLORS.primary, fontWeight: '500' },
  errorBox: {
    backgroundColor: COLORS.errorBg,
    borderWidth: 1,
    borderColor: '#fecaca',
    borderRadius: 8,
    padding: 12,
  },
  errorText: { color: COLORS.error, fontSize: 14 },
  button: {
    backgroundColor: COLORS.primary,
    borderRadius: 10,
    paddingVertical: 14,
    alignItems: 'center',
    marginTop: 4,
  },
  buttonDisabled: { backgroundColor: COLORS.disabled },
  buttonText: { color: COLORS.white, fontWeight: '600', fontSize: 16 },
  footer: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginTop: 32,
  },
  footerText: { color: COLORS.textSecondary, fontSize: 14 },
  footerLink: { color: COLORS.primary, fontWeight: '600', fontSize: 14 },
});
