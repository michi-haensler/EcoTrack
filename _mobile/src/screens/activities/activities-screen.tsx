import React from 'react';
import { FlatList, RefreshControl, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { ActivityItem } from '../../components/features/ActivityItem';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

const mockActivities = [
  { id: '1', name: 'Radfahren', points: 10 },
  { id: '2', name: 'Laufen', points: 15 },
];

export function ActivitiesScreen() {
  const [refreshing, setRefreshing] = React.useState(false);

  const onRefresh = () => {
    setRefreshing(true);
    setTimeout(() => setRefreshing(false), 1000);
  };

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <FlatList
        data={mockActivities}
        renderItem={({ item }) => <ActivityItem activity={item} />}
        keyExtractor={item => item.id}
        accessible={true}
        accessibilityRole="list"
        accessibilityLabel="Liste der Aktivitäten"
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            tintColor={Colors.primary}
          />
        }
        contentContainerStyle={styles.content}
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
  },
});
