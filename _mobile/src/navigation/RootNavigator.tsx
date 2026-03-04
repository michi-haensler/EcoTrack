/**
 * RootNavigator — Wählt Auth-Stack oder App-Stack (MS-01)
 */

import { isAuthenticated, useAuthStore } from '@/stores/authStore';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { StyleSheet, Text, View } from 'react-native';
import { AuthNavigator } from './AuthNavigator';
import type { RootStackParamList } from './types';

const Stack = createNativeStackNavigator<RootStackParamList>();

/** Platzhalter-Home (volle Implementierung in späteren Meilensteinen) */
function HomeScreen() {
  const user = useAuthStore((s) => s.session?.user);
  const logout = useAuthStore((s) => s.logout);

  return (
    <View style={styles.home}>
      <Text style={styles.homeTitle}>🌿 EcoTrack</Text>
      <Text style={styles.homeText}>
        Willkommen, {user?.firstName ?? ''}!
      </Text>
      <Text style={styles.homeText} onPress={() => void logout()}>
        Abmelden
      </Text>
    </View>
  );
}

export function RootNavigator() {
  const authenticated = useAuthStore(isAuthenticated);

  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      {authenticated ? (
        <Stack.Screen name="Home" component={HomeScreen} />
      ) : (
        <Stack.Screen name="Auth" component={AuthNavigator} />
      )}
    </Stack.Navigator>
  );
}

const styles = StyleSheet.create({
  home: { flex: 1, justifyContent: 'center', alignItems: 'center', gap: 16 },
  homeTitle: { fontSize: 32 },
  homeText: { fontSize: 16, color: '#374151' },
});
