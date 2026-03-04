/**
 * RegisterScreen — Mobile (MS-01)
 *
 * Abgedeckte Akzeptanzkriterien:
 * - Registrierung mit Name, E-Mail, Passwort
 * - Rollenauswahl (SCHUELER / LEHRER)
 * - Klassen-ID für Schüler (Pflichtfeld)
 * - Validierung mit Inline-Fehlern
 * - Nach Registrierung: Hinweis auf E-Mail-Verifikation
 */

import type { AuthStackParamList } from '@/navigation/types';
import { useAuthStore } from '@/stores/authStore';
import type { UserRole } from '@/types/auth';
import { useNavigation } from '@react-navigation/native';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { useState } from 'react';
import {
    ActivityIndicator,
    KeyboardAvoidingView,
    Platform,
    ScrollView,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
} from 'react-native';

type RegisterNavigationProp = NativeStackNavigationProp<AuthStackParamList, 'Register'>;

const COLORS = {
  primary: '#16a34a',
  primaryDark: '#15803d',
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
  selected: '#dcfce7',
  selectedBorder: '#16a34a',
} as const;

interface FormErrors {
  firstName?: string;
  lastName?: string;
  email?: string;
  password?: string;
  classId?: string;
}

function validateForm(
  firstName: string,
  lastName: string,
  email: string,
  password: string,
  role: UserRole,
  classId: string,
): FormErrors {
  const errors: FormErrors = {};
  if (!firstName.trim()) errors.firstName = 'Vorname ist erforderlich';
  if (!lastName.trim()) errors.lastName = 'Nachname ist erforderlich';
  if (!email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email))
    errors.email = 'Gültige E-Mail-Adresse erforderlich';
  if (password.length < 8) errors.password = 'Mindestens 8 Zeichen';
  else if (!/[A-Z]/.test(password)) errors.password = 'Mind. 1 Großbuchstabe erforderlich';
  else if (!/[0-9]/.test(password)) errors.password = 'Mind. 1 Zahl erforderlich';
  else if (!/[^A-Za-z0-9]/.test(password)) errors.password = 'Mind. 1 Sonderzeichen erforderlich';
  if (role === 'SCHUELER' && !classId.trim())
    errors.classId = 'Klasse ist für Schüler erforderlich';
  return errors;
}

export function RegisterScreen() {
  const navigation = useNavigation<RegisterNavigationProp>();

  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState<UserRole>('SCHUELER');
  const [classId, setClassId] = useState('');
  const [fieldErrors, setFieldErrors] = useState<FormErrors>({});

  const register = useAuthStore((s) => s.register);
  const registerStatus = useAuthStore((s) => s.registerStatus);
  const registerError = useAuthStore((s) => s.registerError);
  const clearErrors = useAuthStore((s) => s.clearErrors);

  const isLoading = registerStatus === 'loading';
  const isSuccess = registerStatus === 'success';

  async function handleRegister() {
    clearErrors();
    const errors = validateForm(firstName, lastName, email, password, role, classId);
    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      return;
    }
    setFieldErrors({});
    await register({
      firstName: firstName.trim(),
      lastName: lastName.trim(),
      email: email.trim().toLowerCase(),
      password,
      role,
      classId: role === 'SCHUELER' ? classId.trim() : null,
    });
  }

  // Erfolgsansicht — E-Mail-Verifikations-Hinweis
  if (isSuccess) {
    return (
      <View style={styles.successContainer}>
        <Text style={styles.successIcon}>✅</Text>
        <Text style={styles.successTitle}>Fast geschafft!</Text>
        <Text style={styles.successText}>
          Wir haben eine Bestätigungs-E-Mail an {email} gesendet.{'\n'}
          Bitte klicke auf den Link, um dein Konto zu aktivieren.
        </Text>
        <TouchableOpacity
          style={styles.button}
          onPress={() => navigation.navigate('Login')}
          accessibilityRole="button"
        >
          <Text style={styles.buttonText}>Zum Login</Text>
        </TouchableOpacity>
      </View>
    );
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
          <Text style={styles.logo}>🌿</Text>
          <Text style={styles.title}>Konto erstellen</Text>
          <Text style={styles.subtitle}>Werde Teil der EcoTrack-Community</Text>
        </View>

        {/* Formular */}
        <View style={styles.form}>
          {/* Name */}
          <View style={styles.row}>
            <View style={[styles.fieldContainer, styles.flex]}>
              <Text style={styles.label}>Vorname</Text>
              <TextInput
                style={[styles.input, fieldErrors.firstName && styles.inputError]}
                value={firstName}
                onChangeText={setFirstName}
                autoCapitalize="words"
                returnKeyType="next"
                placeholder="Anna"
                placeholderTextColor={COLORS.textSecondary}
                editable={!isLoading}
                accessibilityLabel="Vorname"
              />
              {fieldErrors.firstName ? (
                <Text style={styles.fieldError}>{fieldErrors.firstName}</Text>
              ) : null}
            </View>

            <View style={[styles.fieldContainer, styles.flex]}>
              <Text style={styles.label}>Nachname</Text>
              <TextInput
                style={[styles.input, fieldErrors.lastName && styles.inputError]}
                value={lastName}
                onChangeText={setLastName}
                autoCapitalize="words"
                returnKeyType="next"
                placeholder="Muster"
                placeholderTextColor={COLORS.textSecondary}
                editable={!isLoading}
                accessibilityLabel="Nachname"
              />
              {fieldErrors.lastName ? (
                <Text style={styles.fieldError}>{fieldErrors.lastName}</Text>
              ) : null}
            </View>
          </View>

          {/* E-Mail */}
          <View style={styles.fieldContainer}>
            <Text style={styles.label}>E-Mail-Adresse</Text>
            <TextInput
              style={[styles.input, fieldErrors.email && styles.inputError]}
              value={email}
              onChangeText={setEmail}
              autoCapitalize="none"
              autoCorrect={false}
              keyboardType="email-address"
              returnKeyType="next"
              placeholder="name@schule.at"
              placeholderTextColor={COLORS.textSecondary}
              editable={!isLoading}
              accessibilityLabel="E-Mail-Adresse"
            />
            {fieldErrors.email ? (
              <Text style={styles.fieldError}>{fieldErrors.email}</Text>
            ) : null}
          </View>

          {/* Passwort */}
          <View style={styles.fieldContainer}>
            <Text style={styles.label}>Passwort</Text>
            <TextInput
              style={[styles.input, fieldErrors.password && styles.inputError]}
              value={password}
              onChangeText={setPassword}
              secureTextEntry
              returnKeyType="next"
              placeholder="Mind. 8 Zeichen"
              placeholderTextColor={COLORS.textSecondary}
              editable={!isLoading}
              accessibilityLabel="Passwort"
            />
            {fieldErrors.password ? (
              <Text style={styles.fieldError}>{fieldErrors.password}</Text>
            ) : (
              <Text style={styles.hint}>Mind. 8 Zeichen, 1 Großbuchstabe, 1 Zahl, 1 Sonderzeichen</Text>
            )}
          </View>

          {/* Rolle */}
          <View style={styles.fieldContainer}>
            <Text style={styles.label}>Ich bin…</Text>
            <View style={styles.roleRow}>
              {(['SCHUELER', 'LEHRER'] as UserRole[]).map((r) => (
                <TouchableOpacity
                  key={r}
                  style={[styles.roleButton, role === r && styles.roleButtonSelected]}
                  onPress={() => setRole(r)}
                  accessibilityRole="radio"
                  accessibilityState={{ selected: role === r }}
                  accessibilityLabel={r === 'SCHUELER' ? 'Schüler/in' : 'Lehrer/in'}
                >
                  <Text style={[styles.roleText, role === r && styles.roleTextSelected]}>
                    {r === 'SCHUELER' ? '🎒 Schüler/in' : '👩‍🏫 Lehrer/in'}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>

          {/* Klasse (nur bei Schüler) */}
          {role === 'SCHUELER' && (
            <View style={styles.fieldContainer}>
              <Text style={styles.label}>Klassen-ID</Text>
              <TextInput
                style={[styles.input, fieldErrors.classId && styles.inputError]}
                value={classId}
                onChangeText={setClassId}
                autoCapitalize="characters"
                returnKeyType="done"
                onSubmitEditing={handleRegister}
                placeholder="z.B. 3AHIT"
                placeholderTextColor={COLORS.textSecondary}
                editable={!isLoading}
                accessibilityLabel="Klassen-ID"
              />
              {fieldErrors.classId ? (
                <Text style={styles.fieldError}>{fieldErrors.classId}</Text>
              ) : null}
            </View>
          )}

          {/* API-Fehler */}
          {registerError ? (
            <View style={styles.errorBox} accessibilityRole="alert">
              <Text style={styles.errorText}>{registerError}</Text>
            </View>
          ) : null}

          {/* Submit */}
          <TouchableOpacity
            style={[styles.button, isLoading && styles.buttonDisabled]}
            onPress={handleRegister}
            disabled={isLoading}
            accessibilityRole="button"
            accessibilityLabel={isLoading ? 'Registrierung läuft' : 'Registrieren'}
          >
            {isLoading ? (
              <ActivityIndicator color={COLORS.white} />
            ) : (
              <Text style={styles.buttonText}>Registrieren</Text>
            )}
          </TouchableOpacity>
        </View>

        {/* Login-Link */}
        <View style={styles.footer}>
          <Text style={styles.footerText}>Bereits ein Konto? </Text>
          <TouchableOpacity
            onPress={() => navigation.navigate('Login')}
            accessibilityRole="link"
          >
            <Text style={styles.footerLink}>Anmelden</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1, backgroundColor: '#f9fafb' },
  container: {
    flexGrow: 1,
    paddingHorizontal: 24,
    paddingVertical: 40,
  },
  header: { alignItems: 'center', marginBottom: 32 },
  logo: { fontSize: 48, marginBottom: 10 },
  title: { fontSize: 22, fontWeight: '700', color: COLORS.text, marginBottom: 4 },
  subtitle: { fontSize: 14, color: COLORS.textSecondary, textAlign: 'center' },
  form: { gap: 14 },
  row: { flexDirection: 'row', gap: 12 },
  fieldContainer: { gap: 5 },
  label: { fontSize: 13, fontWeight: '500', color: COLORS.text },
  input: {
    borderWidth: 1,
    borderColor: COLORS.border,
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 11,
    fontSize: 15,
    color: COLORS.text,
    backgroundColor: COLORS.white,
  },
  inputError: { borderColor: COLORS.error },
  fieldError: { fontSize: 12, color: COLORS.error, marginTop: 2 },
  hint: { fontSize: 12, color: COLORS.textSecondary },
  roleRow: { flexDirection: 'row', gap: 10 },
  roleButton: {
    flex: 1,
    borderWidth: 1.5,
    borderColor: COLORS.border,
    borderRadius: 10,
    paddingVertical: 10,
    alignItems: 'center',
    backgroundColor: COLORS.white,
  },
  roleButtonSelected: {
    borderColor: COLORS.selectedBorder,
    backgroundColor: COLORS.selected,
  },
  roleText: { fontSize: 14, color: COLORS.textSecondary, fontWeight: '500' },
  roleTextSelected: { color: COLORS.primaryDark },
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
  footer: { flexDirection: 'row', justifyContent: 'center', marginTop: 28 },
  footerText: { color: COLORS.textSecondary, fontSize: 14 },
  footerLink: { color: COLORS.primary, fontWeight: '600', fontSize: 14 },
  successContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
    backgroundColor: COLORS.white,
  },
  successIcon: { fontSize: 64, marginBottom: 16 },
  successTitle: { fontSize: 22, fontWeight: '700', color: COLORS.text, marginBottom: 12 },
  successText: { fontSize: 15, color: COLORS.textSecondary, textAlign: 'center', lineHeight: 22, marginBottom: 32 },
});
