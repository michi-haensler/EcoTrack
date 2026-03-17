import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { BrandMark } from '../../components/ui/brand-mark';
import { MockupBackground } from '../../components/ui/mockup-background';
import { PrimaryButton } from '../../components/ui/primary-button';
import { RootStackParamList } from '../../navigation/types';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

type WelcomeScreenProps = NativeStackScreenProps<RootStackParamList, 'Welcome'>;

export function WelcomeScreen({ navigation }: WelcomeScreenProps) {
  return (
    <SafeAreaView edges={['top', 'bottom']} style={styles.safeArea}>
      <MockupBackground>
        <View style={styles.container}>
          <View style={styles.topBar}>
            <Pressable onPress={() => navigation.replace('Login')}>
              <Text style={styles.skipText}>Ueberspringen</Text>
            </Pressable>
          </View>

          <View style={styles.content}>
            <View style={styles.hero}>
              <View style={styles.heroGlow} />
              <View style={styles.heroPlanet}>
                <BrandMark size="lg" />
                <View style={styles.heroGround} />
              </View>
              <View style={[styles.floatingBadge, styles.floatingTop]}>
                <Text style={styles.floatingLabel}>eco</Text>
              </View>
              <View style={[styles.floatingBadge, styles.floatingBottom]}>
                <Text style={styles.floatingLabel}>score</Text>
              </View>
            </View>

            <View style={styles.copy}>
              <Text style={styles.title}>
                Willkommen bei <Text style={styles.titleAccent}>EcoTrack!</Text>
              </Text>
              <Text style={styles.description}>
                Logge deine nachhaltigen Aktionen und sammle Punkte fuer deinen Alltag
                und den Planeten.
              </Text>
            </View>
          </View>

          <View style={styles.footer}>
            <View style={styles.progress}>
              <View style={[styles.progressDot, styles.progressDotActive]} />
              <View style={styles.progressDot} />
              <View style={styles.progressDot} />
            </View>
            <PrimaryButton
              accentText="GO"
              label="Weiter"
              onPress={() => navigation.navigate('Login')}
            />
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
  },
  topBar: {
    alignItems: 'flex-end',
    paddingTop: Spacing.md,
  },
  skipText: {
    color: Colors.primaryDark,
    fontSize: 14,
    fontWeight: '700',
  },
  content: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  hero: {
    width: '100%',
    maxWidth: 320,
    height: 320,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: Spacing.xl,
  },
  heroGlow: {
    position: 'absolute',
    width: 260,
    height: 260,
    borderRadius: 130,
    backgroundColor: Colors.primarySoft,
  },
  heroPlanet: {
    width: 220,
    height: 220,
    borderRadius: 110,
    backgroundColor: Colors.surface,
    borderWidth: 1,
    borderColor: Colors.border,
    alignItems: 'center',
    justifyContent: 'center',
  },
  heroGround: {
    position: 'absolute',
    bottom: 30,
    width: 100,
    height: 22,
    borderRadius: 11,
    backgroundColor: Colors.primarySoft,
  },
  floatingBadge: {
    position: 'absolute',
    minWidth: 68,
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.sm,
    borderRadius: 16,
    backgroundColor: Colors.surface,
    borderWidth: 1,
    borderColor: Colors.border,
  },
  floatingTop: {
    top: 48,
    right: 20,
  },
  floatingBottom: {
    bottom: 42,
    left: 22,
  },
  floatingLabel: {
    color: Colors.primaryDark,
    fontSize: 13,
    fontWeight: '800',
    textTransform: 'uppercase',
    textAlign: 'center',
  },
  copy: {
    alignItems: 'center',
    maxWidth: 310,
  },
  title: {
    color: Colors.text,
    fontSize: 32,
    lineHeight: 38,
    fontWeight: '800',
    textAlign: 'center',
    marginBottom: Spacing.md,
  },
  titleAccent: {
    color: Colors.primary,
  },
  description: {
    color: Colors.textMuted,
    fontSize: 18,
    lineHeight: 28,
    textAlign: 'center',
  },
  footer: {
    width: '100%',
  },
  progress: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: Spacing.lg,
  },
  progressDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: Colors.borderStrong,
    marginHorizontal: Spacing.xs,
  },
  progressDotActive: {
    width: 28,
    backgroundColor: Colors.primary,
  },
});
