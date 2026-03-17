import React from 'react';
import {
  FlatList,
  Pressable,
  RefreshControl,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { SafeAreaView } from 'react-native-safe-area-context';
import { ActivityItem } from '../../components/features/ActivityItem';
import { AppCard } from '../../components/ui/app-card';
import { useEcoTrackApp } from '../../hooks/use-ecotrack-app';
import { RootStackParamList } from '../../navigation/types';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';
import { formatCategoryLabel, formatUnitLabel } from '../../utils/ecotrack-formatters';

const ALL_FILTER = 'ALLE';

export function ActivitiesScreen() {
  const {
    activities,
    catalog,
    createActivityEntry,
    dataError,
    isCreatingActivity,
    isDataBusy,
    refreshData,
  } = useEcoTrackApp();
  const [activeFilter, setActiveFilter] = React.useState<string>(ALL_FILTER);
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const filters = React.useMemo(
    () => [
      ALL_FILTER,
      ...Array.from(new Set(catalog.map(activity => activity.category))).sort(),
    ],
    [catalog],
  );

  const filteredActivities =
    activeFilter === ALL_FILTER
      ? activities
      : activities.filter(activity => activity.category === activeFilter);

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <FlatList
        ListHeaderComponent={
          <View style={styles.headerWrap}>
            <Text style={styles.title}>Aktionen</Text>
            <Text style={styles.subtitle}>
              Erfasse Aktionen direkt im Emulator und synchronisiere sie mit deinem Backend-Konto.
            </Text>

            <AppCard style={styles.quickActionsCard}>
              <Text style={styles.filtersTitle}>Aktion erfassen</Text>
              <Text style={styles.cardHint}>
                Ein Tipp erfasst jeweils 1 Einheit. Die Punktelogik kommt direkt aus dem Backend.
              </Text>
              <View style={styles.quickActionsGrid}>
                {catalog.map(action => (
                  <Pressable
                    key={action.actionDefinitionId}
                    onPress={() => createActivityEntry(action.actionDefinitionId, 1)}
                    style={({ pressed }) => [
                      styles.quickActionTile,
                      pressed && styles.quickActionTilePressed,
                    ]}
                  >
                    <Text style={styles.quickActionName}>{action.name}</Text>
                    <Text style={styles.quickActionMeta}>
                      +{action.basePoints} pro {formatUnitLabel(action.unit)}
                    </Text>
                  </Pressable>
                ))}
              </View>
              {isCreatingActivity ? (
                <Text style={styles.statusText}>Aktion wird gespeichert...</Text>
              ) : null}
            </AppCard>

            <AppCard style={styles.filtersCard}>
              <Text style={styles.filtersTitle}>Filter</Text>
              <View style={styles.filtersRow}>
                {filters.map(filter => {
                  const isActive = activeFilter === filter;

                  return (
                    <Pressable
                      key={filter}
                      onPress={() => setActiveFilter(filter)}
                      style={[styles.filterChip, isActive && styles.filterChipActive]}
                    >
                      <Text style={[styles.filterChipText, isActive && styles.filterChipTextActive]}>
                        {formatCategoryLabel(filter)}
                      </Text>
                    </Pressable>
                  );
                })}
              </View>
            </AppCard>

            {dataError ? <Text style={styles.errorText}>{dataError}</Text> : null}
          </View>
        }
        data={filteredActivities}
        renderItem={({ item }) => (
          <ActivityItem
            activity={item}
            onPress={(activityId) => navigation.navigate('ActivityDetail', { activityId })}
          />
        )}
        keyExtractor={item => item.id}
        accessible={true}
        accessibilityRole="list"
        accessibilityLabel="Liste der Aktivitaeten"
        ListEmptyComponent={
          <AppCard>
            <Text style={styles.emptyTitle}>Noch keine Aktionen</Text>
            <Text style={styles.emptyCopy}>
              Fuege oben eine erste nachhaltige Aktion hinzu, damit sie hier erscheint.
            </Text>
          </AppCard>
        }
        refreshControl={
          <RefreshControl
            refreshing={isDataBusy}
            onRefresh={refreshData}
            tintColor={Colors.primary}
          />
        }
        contentContainerStyle={styles.content}
        showsVerticalScrollIndicator={false}
        testID="activities-list"
      />
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
  headerWrap: {
    marginBottom: Spacing.lg,
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
  filtersCard: {
    padding: Spacing.md,
    marginBottom: Spacing.md,
  },
  quickActionsCard: {
    padding: Spacing.md,
    marginBottom: Spacing.md,
  },
  filtersTitle: {
    color: Colors.text,
    fontSize: 16,
    fontWeight: '700',
    marginBottom: Spacing.md,
  },
  cardHint: {
    color: Colors.textMuted,
    fontSize: 14,
    lineHeight: 21,
    marginBottom: Spacing.md,
  },
  quickActionsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginHorizontal: -Spacing.xs,
  },
  quickActionTile: {
    width: '50%',
    paddingHorizontal: Spacing.xs,
    marginBottom: Spacing.sm,
  },
  quickActionTilePressed: {
    opacity: 0.85,
  },
  quickActionName: {
    color: Colors.text,
    fontSize: 14,
    fontWeight: '700',
    backgroundColor: Colors.surfaceMuted,
    borderWidth: 1,
    borderColor: Colors.border,
    borderRadius: 18,
    paddingHorizontal: Spacing.md,
    paddingTop: Spacing.md,
    paddingBottom: Spacing.xs,
    overflow: 'hidden',
  },
  quickActionMeta: {
    color: Colors.primaryDark,
    fontSize: 12,
    fontWeight: '700',
    backgroundColor: Colors.surfaceMuted,
    borderWidth: 1,
    borderTopWidth: 0,
    borderColor: Colors.border,
    borderBottomLeftRadius: 18,
    borderBottomRightRadius: 18,
    paddingHorizontal: Spacing.md,
    paddingBottom: Spacing.md,
  },
  statusText: {
    color: Colors.primaryDark,
    fontSize: 13,
    fontWeight: '700',
  },
  filtersRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  filterChip: {
    marginRight: Spacing.sm,
    marginBottom: Spacing.sm,
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.sm,
    borderRadius: 16,
    backgroundColor: Colors.surfaceMuted,
  },
  filterChipActive: {
    backgroundColor: Colors.primarySoft,
  },
  filterChipText: {
    color: Colors.secondary,
    fontSize: 13,
    fontWeight: '700',
  },
  filterChipTextActive: {
    color: Colors.primaryDark,
  },
  emptyTitle: {
    color: Colors.text,
    fontSize: 18,
    fontWeight: '800',
    marginBottom: Spacing.sm,
  },
  emptyCopy: {
    color: Colors.textMuted,
    fontSize: 15,
    lineHeight: 22,
  },
  errorText: {
    color: Colors.error,
    fontSize: 14,
    lineHeight: 20,
  },
});
