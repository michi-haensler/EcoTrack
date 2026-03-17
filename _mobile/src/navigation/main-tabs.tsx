import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { StyleSheet } from 'react-native';
import { ActivitiesScreen } from '../screens/activities/activities-screen';
import { HomeScreen } from '../screens/home/home-screen';
import { ProfileScreen } from '../screens/profile/profile-screen';
import { Colors } from '../theme/colors';
import { MainTabParamList } from './types';

const Tab = createBottomTabNavigator<MainTabParamList>();

export function MainTabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,
        tabBarActiveTintColor: Colors.primary,
        tabBarInactiveTintColor: Colors.textSoft,
        tabBarIconStyle: { display: 'none' },
        tabBarLabelStyle: styles.tabBarLabel,
        tabBarItemStyle: styles.tabBarItem,
        tabBarStyle: styles.tabBar,
        sceneStyle: styles.scene,
      }}
    >
      <Tab.Screen
        name="Home"
        component={HomeScreen}
        options={{ title: 'Start' }}
      />
      <Tab.Screen
        name="Activities"
        component={ActivitiesScreen}
        options={{ title: 'Aktionen' }}
      />
      <Tab.Screen
        name="Profile"
        component={ProfileScreen}
        options={{ title: 'Profil' }}
      />
    </Tab.Navigator>
  );
}

const styles = StyleSheet.create({
  scene: {
    backgroundColor: Colors.background,
  },
  tabBar: {
    height: 84,
    paddingTop: 10,
    paddingBottom: 18,
    backgroundColor: 'rgba(255,255,255,0.94)',
    borderTopWidth: 0,
    elevation: 0,
    shadowColor: Colors.shadow,
    shadowOffset: { width: 0, height: -6 },
    shadowOpacity: 0.08,
    shadowRadius: 18,
  },
  tabBarItem: {
    marginHorizontal: 6,
    borderRadius: 18,
  },
  tabBarLabel: {
    fontSize: 13,
    fontWeight: '700',
  },
});
