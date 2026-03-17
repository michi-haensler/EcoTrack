import { useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { SafeAreaView } from 'react-native-safe-area-context';
import { AuthInput } from '../../components/ui/auth-input';
import { MockupBackground } from '../../components/ui/mockup-background';
import { PrimaryButton } from '../../components/ui/primary-button';
import { useEcoTrackApp } from '../../hooks/use-ecotrack-app';
import { RootStackParamList } from '../../navigation/types';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

type ResetPasswordScreenProps = NativeStackScreenProps<
  RootStackParamList,
  'ResetPassword'
>;

export function ResetPasswordScreen({ navigation }: ResetPasswordScreenProps) {
  const { authError, isAuthBusy, requestPasswordResetEmail } = useEcoTrackApp();
  const [email, setEmail] = useState('');

  async function handleResetRequest() {
    try {
      await requestPasswordResetEmail(email);
      navigation.navigate('EmailStatus', {
        email: email.trim().toLowerCase(),
        mode: 'reset',
      });
    } catch {
      // Fehlertext wird ueber authError angezeigt.
    }
  }

  return (
    <SafeAreaView edges={['top', 'bottom']} style={styles.safeArea}>
      <MockupBackground>
        <View style={styles.container}>
          <View style={styles.header}>
            <Pressable onPress={() => navigation.goBack()} style={styles.backButton}>
              <Text style={styles.backLabel}>Zurueck</Text>
            </Pressable>
            <Text style={styles.headerTitle}>Reset Password</Text>
            <View style={styles.headerSpacer} />
          </View>

          <View style={styles.content}>
            <View style={styles.hero}>
              <View style={styles.heroCircle} />
              <View style={styles.keyHead} />
              <View style={styles.keyBody} />
              <View style={[styles.keyTooth, styles.keyToothTop]} />
              <View style={[styles.keyTooth, styles.keyToothBottom]} />
              <View style={styles.leafBadge}>
                <View style={styles.leafMark} />
              </View>
            </View>

            <Text style={styles.title}>Passwort vergessen?</Text>
            <Text style={styles.description}>
              Kein Problem. Gib deine E-Mail-Adresse ein und wir senden dir einen
              sicheren Reset-Link.
            </Text>

            <View style={styles.form}>
              <AuthInput
                keyboardType="email-address"
                label="Schulische E-Mail"
                onChangeText={setEmail}
                placeholder="name@schueler.htl-leoben.at"
                value={email}
              />

              {authError ? <Text style={styles.errorText}>{authError}</Text> : null}

              <PrimaryButton
                label={isAuthBusy ? 'Link wird gesendet...' : 'Link senden'}
                onPress={handleResetRequest}
                disabled={isAuthBusy || !email.trim()}
              />

              {isAuthBusy ? (
                <ActivityIndicator color={Colors.primaryDark} style={styles.loadingIndicator} />
              ) : null}
            </View>
          </View>

          <View style={styles.footer}>
            <Text style={styles.footerText}>Passwort doch wieder gefunden?</Text>
            <Pressable onPress={() => navigation.goBack()}>
              <Text style={styles.footerLink}>Zurueck zum Login</Text>
            </Pressable>
          </View>
        </View>
      </MockupBackground>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: Colors.background,
  },
  container: {
    flex: 1,
    paddingHorizontal: Spacing.lg,
    paddingBottom: Spacing.lg,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingTop: Spacing.sm,
  },
  backButton: {
    minWidth: 72,
    paddingVertical: Spacing.sm,
  },
  backLabel: {
    color: Colors.text,
    fontSize: 14,
    fontWeight: '700',
  },
  headerTitle: {
    flex: 1,
    color: Colors.text,
    fontSize: 18,
    fontWeight: '800',
    textAlign: 'center',
  },
  headerSpacer: {
    minWidth: 72,
  },
  content: {
    flex: 1,
    justifyContent: 'center',
  },
  hero: {
    alignSelf: 'center',
    width: 200,
    height: 200,
    marginBottom: Spacing.xl,
    alignItems: 'center',
    justifyContent: 'center',
  },
  heroCircle: {
    position: 'absolute',
    width: 180,
    height: 180,
    borderRadius: 90,
    backgroundColor: Colors.primarySoft,
    borderWidth: 2,
    borderColor: 'rgba(15, 184, 108, 0.2)',
  },
  keyHead: {
    width: 64,
    height: 64,
    borderRadius: 32,
    borderWidth: 10,
    borderColor: Colors.primary,
    backgroundColor: 'transparent',
  },
  keyBody: {
    width: 18,
    height: 78,
    borderRadius: 9,
    backgroundColor: Colors.primary,
    marginTop: -8,
  },
  keyTooth: {
    position: 'absolute',
    width: 22,
    height: 10,
    borderRadius: 5,
    backgroundColor: Colors.primary,
    right: 66,
  },
  keyToothTop: {
    bottom: 60,
  },
  keyToothBottom: {
    bottom: 40,
  },
  leafBadge: {
    position: 'absolute',
    top: 34,
    right: 44,
    width: 52,
    height: 52,
    borderRadius: 26,
    backgroundColor: Colors.surface,
    borderWidth: 1,
    borderColor: Colors.border,
    alignItems: 'center',
    justifyContent: 'center',
  },
  leafMark: {
    width: 18,
    height: 26,
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
    borderBottomLeftRadius: 16,
    borderBottomRightRadius: 16,
    backgroundColor: Colors.primary,
    transform: [{ rotate: '-25deg' }],
  },
  title: {
    color: Colors.text,
    fontSize: 32,
    lineHeight: 38,
    fontWeight: '800',
    textAlign: 'center',
    marginBottom: Spacing.md,
  },
  description: {
    color: Colors.secondary,
    fontSize: 16,
    lineHeight: 26,
    textAlign: 'center',
    marginBottom: Spacing.xl,
  },
  form: {
    backgroundColor: 'rgba(255,255,255,0.84)',
    borderRadius: 28,
    borderWidth: 1,
    borderColor: Colors.border,
    padding: Spacing.lg,
  },
  errorText: {
    color: Colors.error,
    fontSize: 14,
    lineHeight: 20,
    marginBottom: Spacing.md,
  },
  footer: {
    alignItems: 'center',
    paddingVertical: Spacing.lg,
  },
  footerText: {
    color: Colors.secondary,
    fontSize: 14,
  },
  footerLink: {
    marginTop: Spacing.xs,
    color: Colors.primary,
    fontSize: 14,
    fontWeight: '800',
  },
  loadingIndicator: {
    marginTop: Spacing.md,
  },
});
