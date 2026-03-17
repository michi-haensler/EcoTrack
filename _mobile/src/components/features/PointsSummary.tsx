import { StyleSheet, Text, View } from 'react-native';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';
import { ProgressSnapshotResponse } from '../../types/ecotrack-api';
import { formatLevelLabel } from '../../utils/ecotrack-formatters';

interface PointsSummaryProps {
  userId: string;
  progress?: ProgressSnapshotResponse | null;
  activityCount?: number;
}

export function PointsSummary({
  userId,
  progress = null,
  activityCount = 0,
}: PointsSummaryProps) {
  const points = progress?.totalPoints ?? 0;
  const nextLevelProgress = (progress?.progressPercentage ?? 0) / 100;
  const pointsToNextLevel = progress?.pointsToNextLevel ?? 100;
  const levelLabel = formatLevelLabel(progress?.currentLevel);

  return (
    <View
      accessible={true}
      accessibilityLabel={`Punkteuebersicht fuer Benutzer ${userId}`}
      style={styles.container}
    >
      <View style={styles.topRow}>
        <View>
          <Text style={styles.eyebrow}>Deine Punkte</Text>
          <Text style={styles.points}>{points}</Text>
        </View>
        <View style={styles.deltaBadge}>
          <Text style={styles.deltaText}>{levelLabel}</Text>
        </View>
      </View>

      <Text style={styles.caption}>
        Noch {pointsToNextLevel} Punkte bis zum naechsten Level.
      </Text>

      <View style={styles.progressTrack}>
        <View style={[styles.progressValue, { width: `${nextLevelProgress * 100}%` }]} />
      </View>

      <View style={styles.statsRow}>
        <View style={styles.statCard}>
          <Text style={styles.statLabel}>Aktivitaeten</Text>
          <Text style={styles.statValue}>{activityCount}</Text>
        </View>
        <View style={styles.statCard}>
          <Text style={styles.statLabel}>Baum-Level</Text>
          <Text style={styles.statValue}>{levelLabel}</Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: Colors.text,
    borderRadius: 28,
    padding: Spacing.xl,
    marginBottom: Spacing.lg,
  },
  topRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: Spacing.md,
  },
  eyebrow: {
    color: '#CDE8DA',
    fontSize: 14,
    fontWeight: '700',
    marginBottom: Spacing.sm,
  },
  points: {
    color: Colors.surface,
    fontSize: 44,
    fontWeight: '800',
    lineHeight: 48,
  },
  deltaBadge: {
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.sm,
    borderRadius: 16,
    backgroundColor: 'rgba(15, 184, 108, 0.18)',
  },
  deltaText: {
    color: '#E7FFF3',
    fontSize: 13,
    fontWeight: '700',
  },
  caption: {
    color: '#BFD2C7',
    fontSize: 15,
    lineHeight: 22,
    marginBottom: Spacing.md,
  },
  progressTrack: {
    height: 10,
    borderRadius: 5,
    backgroundColor: 'rgba(255,255,255,0.12)',
    overflow: 'hidden',
    marginBottom: Spacing.lg,
  },
  progressValue: {
    height: '100%',
    borderRadius: 5,
    backgroundColor: Colors.primary,
  },
  statsRow: {
    flexDirection: 'row',
  },
  statCard: {
    flex: 1,
    backgroundColor: 'rgba(255,255,255,0.08)',
    borderRadius: 18,
    padding: Spacing.md,
  },
  statLabel: {
    color: '#BFD2C7',
    fontSize: 13,
    marginBottom: Spacing.xs,
  },
  statValue: {
    color: Colors.surface,
    fontSize: 18,
    fontWeight: '800',
  },
});
