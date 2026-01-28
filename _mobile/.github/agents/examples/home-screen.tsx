// ============================================================
// Home Screen Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung eines
// React Native Screens mit Navigation und Feature Components.
// ============================================================

import { ActivityList } from '@/components/features/activities';
import { QuickActions } from '@/components/features/quick-actions';
import { PointsSummary } from '@/components/features/scoring';
import { useCurrentUser } from '@/hooks/use-auth';
import type { HomeScreenProps } from '@/navigation/types';
import {
    RefreshControl,
    ScrollView,
    StyleSheet,
    Text,
    View
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

// -----------------------------
// Screen Component
// -----------------------------
export function HomeScreen({ navigation }: HomeScreenProps) {
  const { data: user, refetch, isRefetching } = useCurrentUser();

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl
            refreshing={isRefetching}
            onRefresh={refetch}
            tintColor="#059669"
            colors={['#059669']} // Android
          />
        }
      >
        {/* Welcome Header */}
        <View style={styles.header}>
          <Text style={styles.greeting}>
            Willkommen, {user?.name}! 🌱
          </Text>
          <Text style={styles.subtitle}>
            Hier ist deine Nachhaltigkeits-Übersicht
          </Text>
        </View>

        {/* Points Summary */}
        <View style={styles.section}>
          <PointsSummary userId={user?.id} />
        </View>

        {/* Quick Actions */}
        <View style={styles.section}>
          <QuickActions
            onLogActivity={() => navigation.navigate('LogActivity')}
            onViewChallenges={() => navigation.navigate('Challenges')}
          />
        </View>

        {/* Recent Activities */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Letzte Aktivitäten</Text>
          <ActivityList
            userId={user?.id}
            limit={5}
            onSelectActivity={(activity) => 
              navigation.navigate('ActivityDetail', { 
                activityId: activity.id 
              })
            }
          />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

// -----------------------------
// Styles
// -----------------------------
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f9fafb',
  },
  scrollContent: {
    padding: 16,
    paddingBottom: 32,
  },
  header: {
    marginBottom: 24,
  },
  greeting: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#111827',
  },
  subtitle: {
    fontSize: 14,
    color: '#6b7280',
    marginTop: 4,
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 12,
  },
});
