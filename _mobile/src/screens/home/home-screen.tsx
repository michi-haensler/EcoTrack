import { RefreshControl, ScrollView, StyleSheet, Text, View } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { SafeAreaView } from 'react-native-safe-area-context';
import { ActivityList } from '../../components/features/ActivityList';
import { PointsSummary } from '../../components/features/PointsSummary';
import { AppCard } from '../../components/ui/app-card';
import { useEcoTrackApp } from '../../hooks/use-ecotrack-app';
import { RootStackParamList } from '../../navigation/types';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';
import { formatLevelLabel, getFirstName } from '../../utils/ecotrack-formatters';

export function HomeScreen() {
  const { activities, dataError, isDataBusy, profile, progress, refreshData } = useEcoTrackApp();
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const userId = profile?.ecoUserId ?? 'demo-user';
  const firstName = getFirstName(profile?.name.displayName);
  const nextLevelPoints = progress?.pointsToNextLevel ?? 0;
  const currentLevel = formatLevelLabel(progress?.currentLevel);

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <ScrollView
        contentContainerStyle={styles.content}
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl
            refreshing={isDataBusy}
            onRefresh={refreshData}
            tintColor={Colors.primary}
          />
        }
      >
        <View style={styles.header}>
          <Text style={styles.greeting}>Hallo {firstName}</Text>
          <Text style={styles.title}>Dein Oeko-Fortschritt</Text>
          <Text style={styles.subtitle}>
            Behalte Punkte, Impact und die letzten Aktionen an einem Ort im Blick.
          </Text>
        </View>

        <PointsSummary
          userId={userId}
          progress={progress}
          activityCount={activities.length}
        />

        <View style={styles.highlightsRow}>
          <AppCard style={styles.highlightCard}>
            <Text style={styles.highlightLabel}>Naechstes Ziel</Text>
            <Text style={styles.highlightValue}>{nextLevelPoints} Punkte</Text>
            <Text style={styles.highlightHint}>bis Level-Up</Text>
          </AppCard>
          <AppCard style={styles.highlightCard}>
            <Text style={styles.highlightLabel}>Aktuelles Level</Text>
            <Text style={styles.highlightValue}>{currentLevel}</Text>
            <Text style={styles.highlightHint}>
              {profile?.className ?? profile?.schoolName ?? 'Ohne Zuordnung'}
            </Text>
          </AppCard>
        </View>

        {dataError ? <Text style={styles.errorText}>{dataError}</Text> : null}

        <ActivityList
          userId={userId}
          activities={activities}
          isLoading={isDataBusy && activities.length === 0}
          error={dataError}
          onActivityPress={(activityId) =>
            navigation.navigate('ActivityDetail', { activityId })
          }
        />
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
    padding: Spacing.md,
    paddingBottom: Spacing.xxl,
  },
  header: {
    marginBottom: Spacing.lg,
  },
  greeting: {
    color: Colors.primaryDark,
    fontSize: 15,
    fontWeight: '700',
    marginBottom: Spacing.sm,
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
  },
  highlightsRow: {
    flexDirection: 'row',
    marginBottom: Spacing.lg,
  },
  highlightCard: {
    flex: 1,
    padding: Spacing.md,
    marginHorizontal: Spacing.xs,
  },
  highlightLabel: {
    color: Colors.textMuted,
    fontSize: 13,
    fontWeight: '600',
    marginBottom: Spacing.xs,
  },
  highlightValue: {
    color: Colors.text,
    fontSize: 24,
    fontWeight: '800',
    marginBottom: Spacing.xs,
  },
  highlightHint: {
    color: Colors.primaryDark,
    fontSize: 13,
    fontWeight: '600',
  },
  errorText: {
    color: Colors.error,
    fontSize: 14,
    lineHeight: 20,
    marginBottom: Spacing.lg,
  },
});
