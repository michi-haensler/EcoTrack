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
import { isApiError } from '../../services/api-client';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

type LoginScreenProps = NativeStackScreenProps<RootStackParamList, 'Login'>;

export function LoginScreen({ navigation }: LoginScreenProps) {
  const { authError, isAuthBusy, signIn } = useEcoTrackApp();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  async function handleLogin() {
    try {
      await signIn({ email, password });
    } catch (error) {
      if (isApiError(error) && error.code === 'EMAIL_NOT_VERIFIED') {
        navigation.navigate('EmailStatus', {
          email: email.trim().toLowerCase(),
          mode: 'verify',
        });
      }
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
              <Text style={styles.brandTitle}>EcoTrack</Text>
              <Text style={styles.brandSubtitle}>
                Melde dich mit deinem Schueler-Konto an und synchronisiere deine Daten mit dem Backend.
              </Text>
            </View>

            <View style={styles.form}>
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
                placeholder="Dein Passwort"
                secureTextEntry={!showPassword}
                trailingActionLabel={showPassword ? 'Verbergen' : 'Anzeigen'}
                onTrailingActionPress={() => setShowPassword(current => !current)}
                value={password}
              />

              <View style={styles.optionsRow}>
                <Pressable onPress={() => navigation.navigate('ResetPassword')}>
                  <Text style={styles.linkText}>Passwort vergessen?</Text>
                </Pressable>
              </View>

              {authError ? <Text style={styles.errorText}>{authError}</Text> : null}

              <PrimaryButton
                label={isAuthBusy ? 'Anmeldung laeuft...' : 'Anmelden'}
                onPress={handleLogin}
                disabled={isAuthBusy || !email.trim() || !password}
              />

              <View style={styles.dividerRow}>
                <View style={styles.divider} />
                <Text style={styles.dividerLabel}>ODER</Text>
                <View style={styles.divider} />
              </View>

              <PrimaryButton
                label="Face ID folgt spaeter"
                onPress={() => undefined}
                variant="outline"
                disabled={true}
              />

              {isAuthBusy ? (
                <ActivityIndicator color={Colors.primaryDark} style={styles.loadingIndicator} />
              ) : null}
            </View>

            <View style={styles.footer}>
              <Text style={styles.footerText}>Noch kein Konto?</Text>
              <Pressable onPress={() => navigation.navigate('Register')}>
                <Text style={styles.footerLink}>Registrieren</Text>
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
    marginBottom: Spacing.xl,
  },
  brandTitle: {
    marginTop: Spacing.lg,
    color: Colors.text,
    fontSize: 32,
    fontWeight: '800',
  },
  brandSubtitle: {
    marginTop: Spacing.sm,
    color: Colors.secondary,
    fontSize: 16,
    lineHeight: 24,
    textAlign: 'center',
  },
  form: {
    backgroundColor: 'rgba(255,255,255,0.82)',
    borderRadius: 28,
    borderWidth: 1,
    borderColor: Colors.border,
    padding: Spacing.lg,
  },
  optionsRow: {
    alignItems: 'flex-end',
    marginTop: Spacing.xs,
    marginBottom: Spacing.lg,
  },
  linkText: {
    color: Colors.primaryDark,
    fontSize: 14,
    fontWeight: '700',
  },
  errorText: {
    color: Colors.error,
    fontSize: 14,
    lineHeight: 20,
    marginBottom: Spacing.md,
  },
  dividerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: Spacing.lg,
  },
  divider: {
    flex: 1,
    height: 1,
    backgroundColor: Colors.border,
  },
  dividerLabel: {
    marginHorizontal: Spacing.md,
    color: Colors.textSoft,
    fontSize: 12,
    fontWeight: '800',
    letterSpacing: 1,
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
