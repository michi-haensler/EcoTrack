import { Pressable, StyleSheet, Text, View } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { SafeAreaView } from 'react-native-safe-area-context';
import { AppCard } from '../../components/ui/app-card';
import { MockupBackground } from '../../components/ui/mockup-background';
import { PrimaryButton } from '../../components/ui/primary-button';
import { RootStackParamList } from '../../navigation/types';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

type EmailStatusScreenProps = NativeStackScreenProps<RootStackParamList, 'EmailStatus'>;

const COPY = {
  verify: {
    title: 'Bestaetige deine E-Mail',
    description:
      'Wir haben dir einen Verifizierungslink geschickt. Oeffne dein Postfach und aktiviere dein EcoTrack Konto.',
    secondaryLabel: 'Zum Login',
  },
  reset: {
    title: 'Link versendet',
    description:
      'Dein Reset-Link ist unterwegs. Pruefe dein Postfach und folge den Schritten fuer ein neues Passwort.',
    secondaryLabel: 'Zurueck zum Login',
  },
} as const;

export function EmailStatusScreen({ navigation, route }: EmailStatusScreenProps) {
  const { email, mode } = route.params;
  const copy = COPY[mode];

  return (
    <SafeAreaView edges={['top', 'bottom']} style={styles.safeArea}>
      <MockupBackground>
        <View style={styles.container}>
          <View style={styles.header}>
            <Pressable onPress={() => navigation.goBack()} style={styles.backButton}>
              <Text style={styles.backLabel}>Zurueck</Text>
            </Pressable>
          </View>

          <View style={styles.hero}>
            <View style={styles.heroGlow} />
            <View style={styles.mailCard}>
              <View style={styles.mailbox} />
              <View style={styles.mailFlag} />
            </View>
            <View style={styles.mailBubble}>
              <Text style={styles.mailBubbleText}>MAIL</Text>
            </View>
          </View>

          <AppCard style={styles.card}>
            <Text style={styles.title}>{copy.title}</Text>
            <Text style={styles.description}>{copy.description}</Text>

            <View style={styles.emailBadge}>
              <Text style={styles.emailText}>{email}</Text>
            </View>

            <PrimaryButton disabled label="Erneut senden in 00:59" />

            <Pressable onPress={() => navigation.goBack()} style={styles.inlineButton}>
              <Text style={styles.inlineButtonText}>E-Mail-Adresse aendern</Text>
            </Pressable>

            <Pressable onPress={() => navigation.replace('Login')} style={styles.inlineButton}>
              <Text style={styles.secondaryText}>{copy.secondaryLabel}</Text>
            </Pressable>
          </AppCard>

          <View style={styles.progress}>
            <View style={[styles.progressDot, styles.progressDotWide]} />
            <View style={styles.progressDot} />
            <View style={styles.progressDot} />
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
    paddingBottom: Spacing.xl,
    justifyContent: 'center',
  },
  header: {
    marginBottom: Spacing.md,
  },
  backButton: {
    alignSelf: 'flex-start',
    paddingVertical: Spacing.sm,
  },
  backLabel: {
    color: Colors.text,
    fontSize: 14,
    fontWeight: '700',
  },
  hero: {
    alignItems: 'center',
    justifyContent: 'center',
    height: 240,
  },
  heroGlow: {
    position: 'absolute',
    width: 220,
    height: 220,
    borderRadius: 110,
    backgroundColor: Colors.primarySoft,
  },
  mailCard: {
    width: 170,
    height: 170,
    borderRadius: 32,
    backgroundColor: Colors.surface,
    borderWidth: 1,
    borderColor: Colors.border,
    alignItems: 'center',
    justifyContent: 'center',
  },
  mailbox: {
    width: 82,
    height: 108,
    borderTopLeftRadius: 46,
    borderTopRightRadius: 46,
    borderBottomLeftRadius: 18,
    borderBottomRightRadius: 18,
    backgroundColor: Colors.primary,
  },
  mailFlag: {
    position: 'absolute',
    top: 52,
    width: 34,
    height: 6,
    borderRadius: 3,
    backgroundColor: 'rgba(255,255,255,0.35)',
  },
  mailBubble: {
    position: 'absolute',
    top: 22,
    right: 52,
    minWidth: 72,
    paddingVertical: Spacing.sm,
    paddingHorizontal: Spacing.md,
    borderRadius: 16,
    backgroundColor: Colors.surface,
    borderWidth: 1,
    borderColor: Colors.border,
  },
  mailBubbleText: {
    color: Colors.primaryDark,
    fontSize: 13,
    fontWeight: '800',
    textAlign: 'center',
  },
  card: {
    paddingHorizontal: Spacing.xl,
  },
  title: {
    color: Colors.text,
    fontSize: 30,
    lineHeight: 36,
    fontWeight: '800',
    textAlign: 'center',
    marginBottom: Spacing.md,
  },
  description: {
    color: Colors.secondary,
    fontSize: 16,
    lineHeight: 25,
    textAlign: 'center',
    marginBottom: Spacing.lg,
  },
  emailBadge: {
    alignSelf: 'center',
    paddingHorizontal: Spacing.lg,
    paddingVertical: Spacing.md,
    borderRadius: 16,
    backgroundColor: Colors.primarySoft,
    borderWidth: 1,
    borderColor: 'rgba(15, 184, 108, 0.22)',
    marginBottom: Spacing.lg,
  },
  emailText: {
    color: Colors.text,
    fontSize: 17,
    fontWeight: '800',
    textAlign: 'center',
  },
  inlineButton: {
    alignItems: 'center',
    marginTop: Spacing.md,
  },
  inlineButtonText: {
    color: Colors.primary,
    fontSize: 14,
    fontWeight: '800',
  },
  secondaryText: {
    color: Colors.textMuted,
    fontSize: 14,
    fontWeight: '600',
  },
  progress: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginTop: Spacing.lg,
  },
  progressDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: Colors.borderStrong,
    marginHorizontal: Spacing.xs,
  },
  progressDotWide: {
    width: 32,
    backgroundColor: Colors.primary,
  },
});
