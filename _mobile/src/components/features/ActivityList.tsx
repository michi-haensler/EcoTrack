import { StyleSheet, Text, View } from 'react-native';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';
import { ActivityItem } from './ActivityItem';

interface Activity {
  id: string;
  name: string;
  points: number;
}

interface ActivityListProps {
  userId: string;
  isLoading?: boolean;
  error?: string | null;
  onActivityPress?: (id: string) => void;
}

const mockActivities: Activity[] = [
  { id: '1', name: 'Radfahren', points: 10 },
  { id: '2', name: 'Laufen', points: 15 },
];

export function ActivityList({
  userId,
  isLoading = false,
  error = null,
  onActivityPress,
}: ActivityListProps) {
  if (isLoading) {
    return (
      <View
        style={styles.container}
        accessible={true}
        accessibilityLabel={`Aktivitaeten fuer Benutzer ${userId} werden geladen`}
      >
        <Text style={styles.title} accessibilityRole="header">Aktivitaeten</Text>
        <Text style={styles.stateText} accessibilityRole="text">Aktivitaeten werden geladen...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View
        style={styles.container}
        accessible={true}
        accessibilityLabel={`Fehler bei Aktivitaeten fuer Benutzer ${userId}: ${error}`}
      >
        <Text style={styles.title} accessibilityRole="header">Aktivitaeten</Text>
        <Text style={styles.stateText} accessibilityRole="alert">{error}</Text>
      </View>
    );
  }

  return (
    <View
      style={styles.container}
      accessible={true}
      accessibilityLabel={`Aktivitaeten fuer Benutzer ${userId}`}
    >
      <Text style={styles.title} accessibilityRole="header">Aktivitaeten</Text>
      {mockActivities.map(activity => (
        <ActivityItem key={activity.id} activity={activity} onPress={onActivityPress} />
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: Colors.secondary,
    borderRadius: 12,
    padding: Spacing.md,
  },
  title: {
    color: Colors.background,
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: Spacing.sm,
  },
  stateText: {
    color: Colors.background,
    fontSize: 16,
  },
});
