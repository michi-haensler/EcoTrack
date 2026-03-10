import { StyleSheet, Text, View } from 'react-native';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

interface PointsSummaryProps {
  userId: string;
}

export function PointsSummary({ userId }: PointsSummaryProps) {
  const points = 120;

  return (
    <View
      style={styles.container}
      accessible={true}
      accessibilityLabel={`Punkteuebersicht fuer Benutzer ${userId}`}
    >
      <Text style={styles.label} accessibilityRole="header">Deine Punkte</Text>
      <Text style={styles.points} accessibilityRole="text">{points}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: Colors.primary,
    borderRadius: 12,
    padding: Spacing.lg,
    marginBottom: Spacing.md,
    alignItems: 'center',
  },
  label: {
    color: Colors.background,
    fontSize: 16,
    marginBottom: Spacing.xs,
  },
  points: {
    color: Colors.background,
    fontSize: 32,
    fontWeight: 'bold',
  },
});
