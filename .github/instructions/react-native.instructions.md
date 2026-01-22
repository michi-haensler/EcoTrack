---
applyTo: "_mobile/**/*.{ts,tsx,js,jsx}"
description: "React Native Standards für Mobile App"
---

## React Native Standards

### React Native Best Practices

#### Platform-spezifischer Code
```typescript
import { Platform, StyleSheet } from 'react-native';

// Platform Select
const styles = StyleSheet.create({
  container: {
    padding: Platform.select({
      ios: 20,
      android: 16,
      default: 16,
    }),
  },
});

// Platform Check
if (Platform.OS === 'ios') {
  // iOS-spezifischer Code
}

// Platform Extensions (.ios.ts / .android.ts)
// UserCard.ios.tsx
// UserCard.android.tsx
```

#### Performance

##### Optimierung
- `React.memo` für Listen-Items
- `FlatList` statt `ScrollView` für lange Listen
- `getItemLayout` bei gleicher Item-Höhe
- `removeClippedSubviews` für sehr lange Listen
- Bilder mit `resizeMode` optimieren

```typescript
const MemoizedActivityItem = React.memo(ActivityItem);

function ActivitiesList({ activities }: Props) {
  const renderItem = useCallback(({ item }: { item: Activity }) => (
    <MemoizedActivityItem activity={item} />
  ), []);
  
  const keyExtractor = useCallback((item: Activity) => item.id, []);
  
  return (
    <FlatList
      data={activities}
      renderItem={renderItem}
      keyExtractor={keyExtractor}
      removeClippedSubviews
      maxToRenderPerBatch={10}
      windowSize={5}
    />
  );
}
```

#### Navigation (React Navigation)

```typescript
// Navigation Types
export type RootStackParamList = {
  Home: undefined;
  Profile: { userId: string };
  Activity: { activityId: string };
};

// Screen Component mit Typed Navigation
import { NativeStackScreenProps } from '@react-navigation/native-stack';

type ProfileScreenProps = NativeStackScreenProps<RootStackParamList, 'Profile'>;

function ProfileScreen({ route, navigation }: ProfileScreenProps) {
  const { userId } = route.params;
  
  return (
    <View>
      <Button
        title="Go Back"
        onPress={() => navigation.goBack()}
      />
    </View>
  );
}
```

#### AsyncStorage
- Für kleine Datenmengen (< 6MB)
- Serialisierung mit JSON
- Error Handling wichtig

```typescript
import AsyncStorage from '@react-native-async-storage/async-storage';

class StorageService {
  async setItem<T>(key: string, value: T): Promise<void> {
    try {
      await AsyncStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
      console.error('Failed to save to storage:', error);
      throw error;
    }
  }
  
  async getItem<T>(key: string): Promise<T | null> {
    try {
      const value = await AsyncStorage.getItem(key);
      return value ? JSON.parse(value) : null;
    } catch (error) {
      console.error('Failed to load from storage:', error);
      return null;
    }
  }
  
  async removeItem(key: string): Promise<void> {
    await AsyncStorage.removeItem(key);
  }
}

export const storage = new StorageService();
```

### Styling

#### StyleSheet über Inline Styles
```typescript
// ✅ Richtig
const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 8,
  },
});

// ❌ Falsch
<View style={{ padding: 16, backgroundColor: '#fff' }}>
```

#### Design Tokens
```typescript
// constants/colors.ts
export const Colors = {
  primary: '#10b981',
  secondary: '#6b7280',
  background: '#ffffff',
  text: '#111827',
  error: '#ef4444',
} as const;

// constants/spacing.ts
export const Spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
} as const;

// Usage
const styles = StyleSheet.create({
  container: {
    padding: Spacing.md,
    backgroundColor: Colors.background,
  },
});
```

#### Responsive Design
```typescript
import { Dimensions } from 'react-native';

const { width, height } = Dimensions.get('window');

const styles = StyleSheet.create({
  container: {
    width: width * 0.9,
    maxWidth: 500,
  },
});
```

### State Management (TanStack Query)

```typescript
// API Query
function useActivities(userId: string) {
  return useQuery({
    queryKey: ['activities', userId],
    queryFn: () => api.getActivities(userId),
    staleTime: 5 * 60 * 1000,
    retry: 2,
  });
}

// Mutation
function useLogActivity() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: LogActivityCommand) => api.logActivity(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['activities'] });
    },
  });
}

// Component
function ActivitiesScreen({ userId }: Props) {
  const { data, isLoading, error, refetch } = useActivities(userId);
  const logActivity = useLogActivity();
  
  if (isLoading) return <ActivityIndicator />;
  if (error) return <ErrorView error={error} onRetry={refetch} />;
  
  return (
    <FlatList
      data={data}
      renderItem={({ item }) => <ActivityItem activity={item} />}
      refreshing={isLoading}
      onRefresh={refetch}
    />
  );
}
```

### Forms

```typescript
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const logActivitySchema = z.object({
  actionDefinitionId: z.string(),
  quantity: z.number().min(1),
  notes: z.string().optional(),
});

type LogActivityForm = z.infer<typeof logActivitySchema>;

function LogActivityScreen() {
  const { control, handleSubmit, formState: { errors } } = useForm<LogActivityForm>({
    resolver: zodResolver(logActivitySchema),
  });
  
  const logActivity = useLogActivity();
  
  const onSubmit = (data: LogActivityForm) => {
    logActivity.mutate(data);
  };
  
  return (
    <View style={styles.container}>
      <Controller
        control={control}
        name="quantity"
        render={({ field: { onChange, value } }) => (
          <TextInput
            value={String(value)}
            onChangeText={(text) => onChange(Number(text))}
            keyboardType="numeric"
            placeholder="Anzahl"
          />
        )}
      />
      {errors.quantity && <Text style={styles.error}>{errors.quantity.message}</Text>}
      
      <Button title="Aktivität loggen" onPress={handleSubmit(onSubmit)} />
    </View>
  );
}
```

### Error Handling

```typescript
interface ErrorViewProps {
  error: Error;
  onRetry?: () => void;
}

function ErrorView({ error, onRetry }: ErrorViewProps) {
  const message = error instanceof ApiError
    ? `API Error ${error.status}: ${error.message}`
    : 'Ein unerwarteter Fehler ist aufgetreten';
  
  return (
    <View style={styles.container}>
      <Text style={styles.errorText}>{message}</Text>
      {onRetry && (
        <Button title="Erneut versuchen" onPress={onRetry} />
      )}
    </View>
  );
}
```

### Testing

```typescript
import { render, fireEvent, waitFor } from '@testing-library/react-native';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

describe('ActivitiesScreen', () => {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  
  const wrapper = ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
  
  it('should display activities', async () => {
    const { getByText } = render(<ActivitiesScreen userId="1" />, { wrapper });
    
    await waitFor(() => {
      expect(getByText('Fahrrad fahren')).toBeTruthy();
    });
  });
});
```

### File Organization

```
mobile/
├── src/
│   ├── components/     # Wiederverwendbare Komponenten
│   │   ├── ui/        # Basis-UI (Button, Input, Card)
│   │   └── features/  # Feature-spezifisch
│   ├── screens/       # Screen-Komponenten
│   ├── navigation/    # Navigation Setup
│   ├── hooks/         # Custom Hooks
│   ├── services/      # API, Storage
│   ├── types/         # TypeScript Types
│   ├── constants/     # Colors, Spacing, etc.
│   └── utils/         # Helper Functions
└── App.tsx
```

### Permissions

```typescript
import { PermissionsAndroid, Platform } from 'react-native';

async function requestLocationPermission() {
  if (Platform.OS === 'android') {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION
    );
    return granted === PermissionsAndroid.RESULTS.GRANTED;
  }
  // iOS: Permissions in Info.plist
  return true;
}
```

### Deep Linking

```typescript
const linking = {
  prefixes: ['ecotrack://'],
  config: {
    screens: {
      Home: '',
      Profile: 'profile/:userId',
      Activity: 'activity/:activityId',
    },
  },
};

// In NavigationContainer
<NavigationContainer linking={linking}>
  {/* ... */}
</NavigationContainer>
```

### Best Practices Zusammenfassung

- ✅ TypeScript strict mode aktivieren
- ✅ FlatList für Listen
- ✅ StyleSheet über Inline Styles
- ✅ Design Tokens verwenden
- ✅ TanStack Query für Server State
- ✅ AsyncStorage für lokale Daten
- ✅ Error Boundaries
- ✅ Platform-spezifischen Code isolieren
- ❌ Keine globalen Variablen
- ❌ Keine console.log in Production
- ❌ Keine ungetypten any
