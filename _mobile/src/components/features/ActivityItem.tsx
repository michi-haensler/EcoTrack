import { Platform, Pressable, StyleSheet, Text, View } from 'react-native';
import { ActivitySummary } from './activity-types';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';
import { formatCategoryLabel } from '../../utils/ecotrack-formatters';

interface ActivityItemProps {
  activity: ActivitySummary;
  onPress?: (id: string) => void;
}

function createMonogram(name: string) {
  return name
    .split(' ')
    .slice(0, 2)
    .map(part => part.charAt(0).toUpperCase())
    .join('');
}

export function ActivityItem({ activity, onPress }: ActivityItemProps) {
  return (
    <Pressable
      accessibilityHint="Tippe, um Details zur Aktivitaet anzuzeigen"
      accessibilityLabel={`Aktivitaet ${activity.name}`}
      accessibilityRole="button"
      onPress={() => onPress?.(activity.id)}
      style={({ pressed }) => [styles.container, pressed && styles.containerPressed]}
      testID="activity-item"
    >
      <View style={styles.leading}>
        <View style={styles.avatar}>
          <Text style={styles.avatarText}>{createMonogram(activity.name)}</Text>
        </View>
        <View style={styles.copy}>
          <Text style={styles.name}>{activity.name}</Text>
          <Text style={styles.meta}>
            {formatCategoryLabel(activity.category)} | {activity.timestamp ?? 'Heute'}
          </Text>
          {activity.impact ? <Text style={styles.impact}>{activity.impact}</Text> : null}
        </View>
      </View>

      <View style={styles.pointsBadge}>
        <Text style={styles.pointsText}>+{activity.points}</Text>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: Colors.surface,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: Colors.border,
    padding: Spacing.md,
    marginBottom: Spacing.sm,
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
  containerPressed: {
    opacity: 0.88,
    transform: [{ scale: 0.995 }],
  },
  leading: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    marginRight: Spacing.md,
  },
  avatar: {
    width: 46,
    height: 46,
    borderRadius: 23,
    backgroundColor: Colors.primarySoft,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: Spacing.md,
  },
  avatarText: {
    color: Colors.primaryDark,
    fontSize: 14,
    fontWeight: '800',
  },
  copy: {
    flex: 1,
  },
  name: {
    color: Colors.text,
    fontSize: 16,
    fontWeight: '700',
    marginBottom: Spacing.xs,
  },
  meta: {
    color: Colors.textMuted,
    fontSize: 13,
    marginBottom: Spacing.xs,
  },
  impact: {
    color: Colors.primaryDark,
    fontSize: 13,
    fontWeight: '600',
  },
  pointsBadge: {
    minWidth: 64,
    height: 36,
    borderRadius: 18,
    backgroundColor: Colors.primarySoft,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: Spacing.md,
  },
  pointsText: {
    color: Colors.primaryDark,
    fontSize: 15,
    fontWeight: '800',
  },
});
