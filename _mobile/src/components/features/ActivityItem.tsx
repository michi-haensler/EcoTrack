import { Platform, StyleSheet, Text, TouchableOpacity } from 'react-native';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

interface Activity {
  id: string;
  name: string;
  points: number;
}

interface ActivityItemProps {
  activity: Activity;
  onPress?: (id: string) => void;
}

export function ActivityItem({ activity, onPress }: ActivityItemProps) {
  return (
    <TouchableOpacity
      style={styles.container}
      onPress={() => onPress?.(activity.id)}
      testID="activity-item"
      accessibilityRole="button"
      accessibilityLabel={`Aktivitaet ${activity.name}`}
      accessibilityHint="Tippe, um Details zur Aktivitaet anzuzeigen"
    >
      <Text style={styles.name}>{activity.name}</Text>
      <Text style={styles.points}>{activity.points} Punkte</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: Colors.background,
    borderRadius: 8,
    padding: Spacing.sm,
    marginBottom: Spacing.xs,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    ...Platform.select({
      android: { elevation: 2 },
      ios: {
        shadowColor: Colors.shadow,
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.1,
        shadowRadius: 2,
      },
      default: {},
    }),
  },
  name: {
    color: Colors.text,
    fontSize: 16,
  },
  points: {
    color: Colors.primary,
    fontWeight: 'bold',
    fontSize: 16,
  },
});
