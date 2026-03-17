import { useState } from 'react';
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { SafeAreaView } from 'react-native-safe-area-context';
import { AuthInput } from '../../components/ui/auth-input';
import { BrandMark } from '../../components/ui/brand-mark';
import { MockupBackground } from '../../components/ui/mockup-background';
import { PrimaryButton } from '../../components/ui/primary-button';
import { useEcoTrackApp } from '../../hooks/use-ecotrack-app';
import { RootStackParamList } from '../../navigation/types';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

type RegisterScreenProps = NativeStackScreenProps<RootStackParamList, 'Register'>;

export function RegisterScreen({ navigation }: RegisterScreenProps) {
  const { authError, isAuthBusy, registerUser } = useEcoTrackApp();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [localError, setLocalError] = useState<string | null>(null);

  async function handleRegister() {
    const trimmedName = name.trim();
    const nameParts = trimmedName.split(' ').filter(Boolean);

    if (nameParts.length < 2) {
      setLocalError('Bitte Vor- und Nachnamen eingeben.');
      return;
    }

    setLocalError(null);

    const [firstName, ...lastNameParts] = nameParts;

    try {
      await registerUser({
        email,
        password,
        firstName,
        lastName: lastNameParts.join(' '),
        classId: null,
      });

      navigation.navigate('EmailStatus', {
        email: email.trim().toLowerCase(),
        mode: 'verify',
      });
    } catch {
      // Fehlertext wird ueber authError angezeigt.
    }
  }

  return (
    <SafeAreaView edges={['top', 'bottom']} style={styles.safeArea}>
      <MockupBackground>
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : undefined}
          style={styles.keyboard}
        >
          <ScrollView
            contentContainerStyle={styles.scrollContent}
            keyboardShouldPersistTaps="handled"
            showsVerticalScrollIndicator={false}
          >
            <View style={styles.header}>
              <BrandMark size="md" />
              <Text style={styles.brandTitle}>Dein EcoTrack Konto</Text>
              <Text style={styles.brandSubtitle}>
                Erstelle dein Profil und starte direkt mit deinen ersten nachhaltigen
                Punkten.
              </Text>
            </View>

            <View style={styles.form}>
              <AuthInput
                autoCapitalize="words"
                label="Voller Name"
                onChangeText={setName}
                placeholder="Vorname Nachname"
                value={name}
              />
              <AuthInput
                keyboardType="email-address"
                label="E-Mail"
                onChangeText={setEmail}
                placeholder="name@schueler.htl-leoben.at"
                value={email}
              />
              <AuthInput
                label="Passwort"
                onChangeText={setPassword}
                placeholder="Mindestens 8 Zeichen"
                secureTextEntry={!showPassword}
                trailingActionLabel={showPassword ? 'Verbergen' : 'Anzeigen'}
                onTrailingActionPress={() => setShowPassword(current => !current)}
                value={password}
              />

              <Text style={styles.legalText}>
                Registriere dich mit deiner Schul-E-Mail. Die Klassenzuordnung erfolgt aktuell
                nach der Freigabe durch Schule oder Lehrkraft.
              </Text>

              {localError ? <Text style={styles.errorText}>{localError}</Text> : null}
              {authError ? <Text style={styles.errorText}>{authError}</Text> : null}

              <PrimaryButton
                accentText="NEW"
                label={isAuthBusy ? 'Registrierung laeuft...' : 'Konto erstellen'}
                onPress={handleRegister}
                disabled={isAuthBusy || !name.trim() || !email.trim() || password.length < 8}
              />

              {isAuthBusy ? (
                <ActivityIndicator color={Colors.primaryDark} style={styles.loadingIndicator} />
              ) : null}
            </View>

            <View style={styles.footer}>
              <Text style={styles.footerText}>Schon registriert?</Text>
              <Pressable onPress={() => navigation.goBack()}>
                <Text style={styles.footerLink}>Zum Login</Text>
              </Pressable>
            </View>
          </ScrollView>
        </KeyboardAvoidingView>
      </MockupBackground>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: Colors.background,
  },
  keyboard: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: 'center',
    paddingHorizontal: Spacing.lg,
    paddingVertical: Spacing.xl,
  },
  header: {
    alignItems: 'center',
    marginBottom: Spacing.lg,
  },
  brandTitle: {
    marginTop: Spacing.lg,
    color: Colors.text,
    fontSize: 30,
    lineHeight: 36,
    fontWeight: '800',
    textAlign: 'center',
  },
  brandSubtitle: {
    marginTop: Spacing.sm,
    color: Colors.secondary,
    fontSize: 15,
    lineHeight: 23,
    textAlign: 'center',
    maxWidth: 320,
  },
  form: {
    backgroundColor: 'rgba(255,255,255,0.86)',
    borderRadius: 28,
    borderWidth: 1,
    borderColor: Colors.border,
    padding: Spacing.lg,
  },
  legalText: {
    color: Colors.textMuted,
    fontSize: 13,
    lineHeight: 20,
    marginBottom: Spacing.lg,
  },
  errorText: {
    color: Colors.error,
    fontSize: 14,
    lineHeight: 20,
    marginBottom: Spacing.md,
  },
  footer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: Spacing.lg,
  },
  footerText: {
    color: Colors.secondary,
    fontSize: 14,
  },
  footerLink: {
    marginLeft: Spacing.xs,
    color: Colors.primary,
    fontSize: 14,
    fontWeight: '800',
  },
  loadingIndicator: {
    marginTop: Spacing.md,
  },
});
