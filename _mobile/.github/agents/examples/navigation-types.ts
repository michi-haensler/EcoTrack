// ============================================================
// Navigation Types Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Typisierung für
// React Navigation in TypeScript.
// ============================================================

import type { BottomTabScreenProps } from '@react-navigation/bottom-tabs';
import type { CompositeScreenProps } from '@react-navigation/native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

// -----------------------------
// Root Stack (Modal Screens)
// -----------------------------
export type RootStackParamList = {
  /** Main Tab Navigator */
  Main: undefined;
  /** Auth Flow */
  Auth: undefined;
  /** Activity Detail Modal */
  ActivityDetail: { activityId: string };
  /** Log Activity Modal */
  LogActivity: undefined;
  /** Challenge Detail Modal */
  ChallengeDetail: { challengeId: string };
};

// -----------------------------
// Main Tab Navigator
// -----------------------------
export type MainTabParamList = {
  Home: undefined;
  Activities: undefined;
  Challenges: undefined;
  Profile: undefined;
};

// -----------------------------
// Auth Stack
// -----------------------------
export type AuthStackParamList = {
  Login: undefined;
  Register: undefined;
  ForgotPassword: undefined;
};

// -----------------------------
// Screen Props Types
// -----------------------------

// Home Screen - In Tab Navigator, kann zu Root Stack navigieren
export type HomeScreenProps = CompositeScreenProps<
  BottomTabScreenProps<MainTabParamList, 'Home'>,
  NativeStackScreenProps<RootStackParamList>
>;

// Activities Screen
export type ActivitiesScreenProps = CompositeScreenProps<
  BottomTabScreenProps<MainTabParamList, 'Activities'>,
  NativeStackScreenProps<RootStackParamList>
>;

// Challenges Screen
export type ChallengesScreenProps = CompositeScreenProps<
  BottomTabScreenProps<MainTabParamList, 'Challenges'>,
  NativeStackScreenProps<RootStackParamList>
>;

// Profile Screen
export type ProfileScreenProps = CompositeScreenProps<
  BottomTabScreenProps<MainTabParamList, 'Profile'>,
  NativeStackScreenProps<RootStackParamList>
>;

// Activity Detail - Modal Screen
export type ActivityDetailScreenProps = NativeStackScreenProps<
  RootStackParamList, 
  'ActivityDetail'
>;

// Challenge Detail - Modal Screen
export type ChallengeDetailScreenProps = NativeStackScreenProps<
  RootStackParamList, 
  'ChallengeDetail'
>;

// Login Screen
export type LoginScreenProps = NativeStackScreenProps<
  AuthStackParamList, 
  'Login'
>;

// -----------------------------
// Verwendung in Screen
// -----------------------------
/*
export function HomeScreen({ navigation, route }: HomeScreenProps) {
  // Navigation zu Modal
  navigation.navigate('ActivityDetail', { activityId: '123' });
  
  // Navigation innerhalb Tabs
  navigation.navigate('Activities');
}

export function ActivityDetailScreen({ navigation, route }: ActivityDetailScreenProps) {
  // Route Params extrahieren
  const { activityId } = route.params;
  
  // Zurück navigieren
  navigation.goBack();
}
*/
