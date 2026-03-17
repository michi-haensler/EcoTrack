import { ScrollView, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { AppCard } from '../../components/ui/app-card';
import { PrimaryButton } from '../../components/ui/primary-button';
import { useEcoTrackApp } from '../../hooks/use-ecotrack-app';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';
import { formatLevelLabel, getInitials } from '../../utils/ecotrack-formatters';

export function ProfileScreen() {
  const { apiBaseUrl, profile, progress, signOut } = useEcoTrackApp();
  const displayName = profile?.name.displayName ?? 'Alex Green';
  const email = profile?.email ?? 'alex.green@schueler.htl-leoben.at';
  const schoolName = profile?.schoolName ?? 'Noch nicht zugewiesen';
  const className = profile?.className ?? 'Noch nicht zugewiesen';
  const role = profile?.role ?? 'SCHUELER';

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Text style={styles.title} accessibilityRole="header">
          Profil
        </Text>
        <Text style={styles.subtitle}>
          Deine persoenlichen Daten, Zuordnung und App-Einstellungen.
        </Text>

        <AppCard style={styles.heroCard}>
          <View style={styles.avatar}>
            <Text style={styles.avatarText}>{getInitials(displayName)}</Text>
          </View>
          <Text style={styles.name}>{displayName}</Text>
          <Text style={styles.meta}>{email}</Text>
          <Text style={styles.meta}>
            {schoolName} | {className}
          </Text>
        </AppCard>

        <AppCard style={styles.sectionCard}>
          <Text style={styles.sectionTitle}>Zuordnung</Text>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Schule</Text>
            <Text style={styles.infoValue}>{schoolName}</Text>
          </View>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Klasse</Text>
            <Text style={styles.infoValue}>{className}</Text>
          </View>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Rolle</Text>
            <Text style={styles.infoValue}>{role}</Text>
          </View>
        </AppCard>

        <AppCard style={styles.sectionCard}>
          <Text style={styles.sectionTitle}>Konto</Text>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Punkte</Text>
            <Text style={styles.infoValue}>{progress?.totalPoints ?? 0}</Text>
          </View>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Level</Text>
            <Text style={styles.infoValue}>{formatLevelLabel(progress?.currentLevel)}</Text>
          </View>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Backend</Text>
            <Text style={styles.apiValue}>{apiBaseUrl}</Text>
          </View>
        </AppCard>

        <PrimaryButton label="Abmelden" onPress={signOut} />
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.background,
  },
  content: {
    flex: 1,
    padding: Spacing.lg,
  },
  title: {
    color: Colors.text,
    fontSize: 34,
    lineHeight: 40,
    fontWeight: '800',
    marginBottom: Spacing.sm,
  },
  subtitle: {
    color: Colors.textMuted,
    fontSize: 16,
    lineHeight: 24,
    marginBottom: Spacing.lg,
  },
  heroCard: {
    alignItems: 'center',
    marginBottom: Spacing.lg,
  },
  avatar: {
    width: 88,
    height: 88,
    borderRadius: 44,
    backgroundColor: Colors.primarySoft,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: Spacing.md,
  },
  avatarText: {
    color: Colors.primaryDark,
    fontSize: 28,
    fontWeight: '800',
  },
  name: {
    color: Colors.text,
    fontSize: 24,
    fontWeight: '800',
    marginBottom: Spacing.xs,
  },
  meta: {
    color: Colors.textMuted,
    fontSize: 15,
    lineHeight: 22,
  },
  sectionCard: {
    marginBottom: Spacing.md,
  },
  sectionTitle: {
    color: Colors.text,
    fontSize: 18,
    fontWeight: '800',
    marginBottom: Spacing.md,
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: Spacing.sm,
  },
  infoLabel: {
    flex: 1,
    color: Colors.textMuted,
    fontSize: 15,
    marginRight: Spacing.md,
  },
  infoValue: {
    color: Colors.text,
    fontSize: 15,
    fontWeight: '700',
  },
  apiValue: {
    color: Colors.text,
    fontSize: 13,
    fontWeight: '700',
    flex: 1,
    textAlign: 'right',
  },
});
