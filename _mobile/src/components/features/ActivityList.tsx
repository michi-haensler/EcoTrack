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
}

const mockActivities: Activity[] = [
  { id: '1', name: 'Radfahren', points: 10 },
  { id: '2', name: 'Laufen', points: 15 },
];

export function ActivityList({ userId }: ActivityListProps) {
  return (
    <View
      style={styles.container}
      accessible={true}
      accessibilityLabel={`Aktivitäten für Benutzer ${userId}`}
    >
      <Text style={styles.title} accessibilityRole="header">Aktivitäten</Text>
      {mockActivities.map(activity => (
        <ActivityItem key={activity.id} activity={activity} />
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
});
