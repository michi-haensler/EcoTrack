import { StyleSheet, Text, View } from 'react-native';
import { AppCard } from '../ui/app-card';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';
import { ActivityItem } from './ActivityItem';
import { ActivitySummary, mockActivities } from './activity-types';

interface ActivityListProps {
  userId: string;
  activities?: ActivitySummary[];
  isLoading?: boolean;
  error?: string | null;
  onActivityPress?: (id: string) => void;
}

export function ActivityList({
  userId,
  activities = mockActivities,
  isLoading = false,
  error = null,
  onActivityPress,
}: ActivityListProps) {
  if (isLoading) {
    return (
      <AppCard>
        <Text style={styles.title} accessibilityRole="header">
          Letzte Aktionen
        </Text>
        <Text
          accessibilityLabel={`Aktivitaeten fuer Benutzer ${userId} werden geladen`}
          style={styles.stateText}
        >
          Aktivitaeten werden geladen...
        </Text>
      </AppCard>
    );
  }

  if (error) {
    return (
      <AppCard>
        <Text style={styles.title} accessibilityRole="header">
          Letzte Aktionen
        </Text>
        <Text
          accessibilityLabel={`Fehler bei Aktivitaeten fuer Benutzer ${userId}: ${error}`}
          accessibilityRole="alert"
          style={[styles.stateText, styles.errorText]}
        >
          {error}
        </Text>
      </AppCard>
    );
  }

  return (
    <AppCard accessibilityLabel={`Aktivitaeten fuer Benutzer ${userId}`}>
      <Text style={styles.title} accessibilityRole="header">
        Letzte Aktionen
      </Text>
      <Text style={styles.subtitle}>
        Deine juengsten nachhaltigen Entscheidungen im Ueberblick.
      </Text>

      {activities.length === 0 ? (
        <Text style={styles.stateText}>Noch keine Aktionen erfasst.</Text>
      ) : (
        <View style={styles.list}>
          {activities.slice(0, 3).map(activity => (
            <ActivityItem key={activity.id} activity={activity} onPress={onActivityPress} />
          ))}
        </View>
      )}
    </AppCard>
  );
}

const styles = StyleSheet.create({
  title: {
    color: Colors.text,
    fontSize: 22,
    fontWeight: '800',
    marginBottom: Spacing.sm,
  },
  subtitle: {
    color: Colors.textMuted,
    fontSize: 14,
    lineHeight: 21,
    marginBottom: Spacing.lg,
  },
  list: {
    marginTop: Spacing.xs,
  },
  stateText: {
    color: Colors.textMuted,
    fontSize: 16,
    lineHeight: 24,
  },
  errorText: {
    color: Colors.error,
  },
});
