import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { AppCard } from '../../components/ui/app-card';
import { useEcoTrackApp } from '../../hooks/use-ecotrack-app';
import { RootStackParamList } from '../../navigation/types';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

type ActivityDetailScreenProps = NativeStackScreenProps<
  RootStackParamList,
  'ActivityDetail'
>;

export function ActivityDetailScreen({
  navigation,
  route,
}: ActivityDetailScreenProps) {
  const { activities } = useEcoTrackApp();
  const { activityId } = route.params;
  const activity = activities.find(entry => entry.id === activityId);

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <View style={styles.content}>
        <Pressable onPress={() => navigation.goBack()} style={styles.backButton}>
          <Text style={styles.backLabel}>Zurueck</Text>
        </Pressable>

        <Text style={styles.title} accessibilityRole="header">
          Aktivitaetsdetails
        </Text>
        <Text style={styles.subtitle}>
          Mehr Kontext und die verbuchten Punkte zu deiner Aktion.
        </Text>

        <AppCard>
          <Text style={styles.activityName}>{activity?.name ?? 'Unbekannte Aktivitaet'}</Text>
          <Text style={styles.activityMeta}>
            {activity?.category ?? 'Aktion'} | {activity?.timestamp ?? `ID ${activityId}`}
          </Text>
          <Text style={styles.points}>+{activity?.points ?? 0} Punkte</Text>
          <Text style={styles.impact}>
            {activity?.impact ?? 'Impact wird verfuegbar, sobald Live-Daten angebunden sind.'}
          </Text>
        </AppCard>
      </View>
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
  backButton: {
    alignSelf: 'flex-start',
    paddingVertical: Spacing.sm,
    marginBottom: Spacing.md,
  },
  backLabel: {
    color: Colors.primaryDark,
    fontSize: 14,
    fontWeight: '700',
  },
  title: {
    color: Colors.text,
    fontSize: 32,
    lineHeight: 38,
    fontWeight: '800',
    marginBottom: Spacing.sm,
  },
  subtitle: {
    color: Colors.textMuted,
    fontSize: 16,
    lineHeight: 24,
    marginBottom: Spacing.lg,
  },
  activityName: {
    color: Colors.text,
    fontSize: 26,
    lineHeight: 32,
    fontWeight: '800',
    marginBottom: Spacing.sm,
  },
  activityMeta: {
    color: Colors.textMuted,
    fontSize: 15,
    lineHeight: 22,
    marginBottom: Spacing.lg,
  },
  points: {
    color: Colors.primaryDark,
    fontSize: 34,
    fontWeight: '800',
    marginBottom: Spacing.sm,
  },
  impact: {
    color: Colors.secondary,
    fontSize: 16,
    lineHeight: 24,
  },
});
