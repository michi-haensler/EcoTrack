import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { RootNavigator } from './src/navigation/root-navigator';
import { EcoTrackAppProvider } from './src/stores/ecotrack-app-provider';

export default function App() {
  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaProvider>
        <EcoTrackAppProvider>
          <RootNavigator />
        </EcoTrackAppProvider>
      </SafeAreaProvider>
    </GestureHandlerRootView>
  );
}
