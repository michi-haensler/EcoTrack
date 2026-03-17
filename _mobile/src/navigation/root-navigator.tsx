import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { useEcoTrackApp } from '../hooks/use-ecotrack-app';
import { EmailStatusScreen } from '../screens/auth/email-status-screen';
import { LoginScreen } from '../screens/auth/login-screen';
import { RegisterScreen } from '../screens/auth/register-screen';
import { ResetPasswordScreen } from '../screens/auth/reset-password-screen';
import { WelcomeScreen } from '../screens/auth/welcome-screen';
import { ActivityDetailScreen } from '../screens/activities/activity-detail-screen';
import { MainTabs } from './main-tabs';
import { RootStackParamList } from './types';

const Stack = createNativeStackNavigator<RootStackParamList>();

export function RootNavigator() {
  const { isAuthenticated } = useEcoTrackApp();

  return (
    <NavigationContainer>
      <Stack.Navigator
        key={isAuthenticated ? 'authenticated' : 'guest'}
        initialRouteName={isAuthenticated ? 'Main' : 'Welcome'}
        screenOptions={{
          animation: 'slide_from_right',
          contentStyle: { backgroundColor: '#FCFCFD' },
          headerShown: false,
        }}
      >
        {isAuthenticated ? (
          <>
            <Stack.Screen name="Main" component={MainTabs} options={{ headerShown: false }} />
            <Stack.Screen name="ActivityDetail" component={ActivityDetailScreen} />
          </>
        ) : (
          <>
            <Stack.Screen name="Welcome" component={WelcomeScreen} />
            <Stack.Screen name="Login" component={LoginScreen} />
            <Stack.Screen name="Register" component={RegisterScreen} />
            <Stack.Screen name="ResetPassword" component={ResetPasswordScreen} />
            <Stack.Screen name="EmailStatus" component={EmailStatusScreen} />
          </>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}
