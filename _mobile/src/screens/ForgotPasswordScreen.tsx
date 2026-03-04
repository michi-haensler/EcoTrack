/**
 * ForgotPasswordScreen — Mobile (MS-01)
 *
 * Abgedeckte Akzeptanzkriterien:
 * - E-Mail für Passwort-Reset eingeben
 * - Erfolgs-Feedback anzeigen
 * - Deep-Link-Rückkehr nach Passwortänderung (im useEffect)
 */

import type { AuthStackParamList } from '@/navigation/types';
import { useAuthStore } from '@/stores/authStore';
import { useNavigation } from '@react-navigation/native';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { useEffect, useState } from 'react';
import {
    ActivityIndicator,
    KeyboardAvoidingView,
    Linking,
    Platform,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
} from 'react-native';

type ForgotPasswordNavigationProp = NativeStackNavigationProp<
  AuthStackParamList,
  'ForgotPassword'
>;

const COLORS = {
  primary: '#16a34a',
  error: '#dc2626',
  errorBg: '#fef2f2',
  success: '#15803d',
  successBg: '#f0fdf4',
  text: '#111827',
  textSecondary: '#6b7280',
  border: '#d1d5db',
  borderFocused: '#16a34a',
  background: '#f9fafb',
  white: '#ffffff',
  disabled: '#9ca3af',
} as const;

export function ForgotPasswordScreen() {
  const navigation = useNavigation<ForgotPasswordNavigationProp>();
  const [email, setEmail] = useState('');
  const [emailFocused, setEmailFocused] = useState(false);

  const requestPasswordReset = useAuthStore((s) => s.requestPasswordReset);
  const resetStatus = useAuthStore((s) => s.resetStatus);
  const resetError = useAuthStore((s) => s.resetError);
  const clearErrors = useAuthStore((s) => s.clearErrors);

  const isLoading = resetStatus === 'loading';
  const isSuccess = resetStatus === 'success';

  // Deep-Link-Handler: ecotrack://callback → zurück zum Login navigieren
  useEffect(() => {
    const subscription = Linking.addEventListener('url', ({ url }) => {
      if (url.startsWith('ecotrack://callback')) {
        navigation.navigate('Login');
      }
    });
    return () => subscription.remove();
  }, [navigation]);

  async function handleSubmit() {
    if (!email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      return;
    }
    clearErrors();
    await requestPasswordReset({ email: email.trim().toLowerCase() });
  }

  return (
    <KeyboardAvoidingView
      style={styles.flex}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <View style={styles.container}>
        {/* Header */}
        <View style={styles.header}>
          <Text style={styles.icon}>🔑</Text>
          <Text style={styles.title}>Passwort vergessen?</Text>
          <Text style={styles.subtitle}>
            Kein Problem. Wir senden dir einen Link zum Zurücksetzen per E-Mail.
          </Text>
        </View>

        {isSuccess ? (
          /* Erfolgsansicht */
          <View
            style={styles.successBox}
            accessibilityRole="status"
            accessibilityLiveRegion="polite"
          >
            <Text style={styles.successIcon}>📬</Text>
            <Text style={styles.successTitle}>E-Mail gesendet!</Text>
            <Text style={styles.successText}>
              Falls ein Konto mit dieser E-Mail existiert, hast du gleich eine
              Nachricht mit einem Reset-Link erhalten.
            </Text>
            <TouchableOpacity
              style={styles.button}
              onPress={() => navigation.navigate('Login')}
              accessibilityRole="button"
            >
              <Text style={styles.buttonText}>Zurück zum Login</Text>
            </TouchableOpacity>
          </View>
        ) : (
          /* Formular */
          <View style={styles.form}>
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
                returnKeyType="send"
                onSubmitEditing={handleSubmit}
                placeholder="name@schule.at"
                placeholderTextColor={COLORS.textSecondary}
                editable={!isLoading}
                accessibilityLabel="E-Mail-Adresse"
                accessibilityRequired={true}
              />
            </View>

            {/* Fehlermeldung */}
            {resetError ? (
              <View style={styles.errorBox} accessibilityRole="alert">
                <Text style={styles.errorText}>{resetError}</Text>
              </View>
            ) : null}

            {/* Submit */}
            <TouchableOpacity
              style={[styles.button, isLoading && styles.buttonDisabled]}
              onPress={handleSubmit}
              disabled={isLoading}
              accessibilityRole="button"
              accessibilityLabel={isLoading ? 'Wird gesendet' : 'Reset-Link anfordern'}
            >
              {isLoading ? (
                <ActivityIndicator color={COLORS.white} />
              ) : (
                <Text style={styles.buttonText}>Reset-Link anfordern</Text>
              )}
            </TouchableOpacity>

            {/* Zurück zum Login */}
            <TouchableOpacity
              style={styles.backButton}
              onPress={() => navigation.goBack()}
              accessibilityRole="button"
            >
              <Text style={styles.backButtonText}>← Zurück zum Login</Text>
            </TouchableOpacity>
          </View>
        )}
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1, backgroundColor: COLORS.background },
  container: {
    flex: 1,
    paddingHorizontal: 24,
    paddingVertical: 40,
    justifyContent: 'center',
  },
  header: { alignItems: 'center', marginBottom: 36 },
  icon: { fontSize: 48, marginBottom: 12 },
  title: { fontSize: 22, fontWeight: '700', color: COLORS.text, marginBottom: 8, textAlign: 'center' },
  subtitle: { fontSize: 14, color: COLORS.textSecondary, textAlign: 'center', lineHeight: 20 },
  form: { gap: 16 },
  fieldContainer: { gap: 6 },
  label: { fontSize: 14, fontWeight: '500', color: COLORS.text },
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
  },
  buttonDisabled: { backgroundColor: COLORS.disabled },
  buttonText: { color: COLORS.white, fontWeight: '600', fontSize: 16 },
  backButton: { alignItems: 'center', paddingVertical: 8 },
  backButtonText: { color: COLORS.primary, fontSize: 14, fontWeight: '500' },
  successBox: {
    backgroundColor: '#f0fdf4',
    borderWidth: 1,
    borderColor: '#bbf7d0',
    borderRadius: 16,
    padding: 24,
    alignItems: 'center',
    gap: 12,
  },
  successIcon: { fontSize: 48 },
  successTitle: { fontSize: 20, fontWeight: '700', color: COLORS.success },
  successText: {
    fontSize: 14,
    color: COLORS.textSecondary,
    textAlign: 'center',
    lineHeight: 20,
  },
});
