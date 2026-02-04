---
applyTo: "**/*.test.{ts,tsx}"
description: "Testing Standards für React Native Mobile App"
---

## React Native Testing Standards

### Allgemeine Testprinzipien

#### AAA Pattern
```typescript
test('should_displayActivity_when_provided', () => {
  // Arrange - Setup
  const activity = { id: '1', name: 'Radfahren', points: 10 };
  
  // Act - Ausführung
  const { getByText } = render(<ActivityItem activity={activity} />);
  
  // Assert - Überprüfung
  expect(getByText('Radfahren')).toBeTruthy();
});
```

#### Test-Benennung
Format: `should_<expected>_when_<condition>`

### Screen Tests

```typescript
import { render, fireEvent, waitFor } from '@testing-library/react-native';
import { NavigationContainer } from '@react-navigation/native';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

describe('ActivitiesScreen', () => {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  
  const wrapper = ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      <NavigationContainer>
        {children}
      </NavigationContainer>
    </QueryClientProvider>
  );
  
  it('should_displayActivities_when_loaded', async () => {
    // Arrange
    const mockActivities = [
      { id: '1', name: 'Radfahren', points: 10 },
    ];
    vi.spyOn(api, 'getActivities').mockResolvedValue(mockActivities);
    
    // Act
    const { getByText } = render(
      <ActivitiesScreen userId="1" />,
      { wrapper }
    );
    
    // Assert
    await waitFor(() => {
      expect(getByText('Radfahren')).toBeTruthy();
    });
  });
  
  it('should_showRefresh_when_pullDown', async () => {
    const { getByTestId } = render(
      <ActivitiesScreen userId="1" />,
      { wrapper }
    );
    
    const flatList = getByTestId('activities-list');
    fireEvent(flatList, 'refresh');
    
    await waitFor(() => {
      expect(api.getActivities).toHaveBeenCalledTimes(2);
    });
  });
});
```

### Component Tests

```typescript
import { render, fireEvent } from '@testing-library/react-native';

describe('ActivityItem', () => {
  it('should_displayActivityInfo_when_rendered', () => {
    // Arrange
    const activity = { id: '1', name: 'Radfahren', points: 10 };
    
    // Act
    const { getByText } = render(<ActivityItem activity={activity} />);
    
    // Assert
    expect(getByText('Radfahren')).toBeTruthy();
    expect(getByText('10 Punkte')).toBeTruthy();
  });
  
  it('should_callOnPress_when_tapped', () => {
    // Arrange
    const onPress = jest.fn();
    const activity = { id: '1', name: 'Radfahren', points: 10 };
    
    const { getByTestId } = render(
      <ActivityItem activity={activity} onPress={onPress} />
    );
    
    // Act
    fireEvent.press(getByTestId('activity-item'));
    
    // Assert
    expect(onPress).toHaveBeenCalledWith('1');
  });
});
```

### Hook Tests

```typescript
import { renderHook, waitFor } from '@testing-library/react-native';

describe('useActivities', () => {
  it('should_fetchData_when_called', async () => {
    // Arrange
    const mockData = [{ id: '1', name: 'Activity' }];
    vi.spyOn(api, 'getActivities').mockResolvedValue(mockData);
    
    // Act
    const { result } = renderHook(() => useActivities('user-1'), { wrapper });
    
    // Assert
    await waitFor(() => {
      expect(result.current.data).toEqual(mockData);
    });
  });
});
```

### Navigation Mocking

```typescript
const mockNavigation = {
  navigate: jest.fn(),
  goBack: jest.fn(),
  setOptions: jest.fn(),
};

const mockRoute = {
  params: { userId: '123' },
};

it('should_navigate_when_buttonPressed', () => {
  const { getByText } = render(
    <ProfileScreen 
      navigation={mockNavigation as any} 
      route={mockRoute as any} 
    />
  );
  
  fireEvent.press(getByText('Go to Settings'));
  
  expect(mockNavigation.navigate).toHaveBeenCalledWith('Settings');
});
```

### Test Data Factory

```typescript
function createTestActivity(overrides?: Partial<Activity>): Activity {
  return {
    id: '1',
    name: 'Test Activity',
    points: 10,
    quantity: 1,
    timestamp: new Date().toISOString(),
    ...overrides,
  };
}

// Usage
const activity = createTestActivity({ points: 50 });
```

### AsyncStorage Mocking

```typescript
jest.mock('@react-native-async-storage/async-storage', () => ({
  setItem: jest.fn(),
  getItem: jest.fn(),
  removeItem: jest.fn(),
}));

import AsyncStorage from '@react-native-async-storage/async-storage';

it('should_saveData_when_called', async () => {
  await storage.setItem('key', { data: 'value' });
  
  expect(AsyncStorage.setItem).toHaveBeenCalledWith(
    'key',
    JSON.stringify({ data: 'value' })
  );
});
```

### Coverage-Ziele

- Unit Tests: 70-80%
- Integration Tests: 20-30%

### Was testen?

✅ Screen Logic
✅ User Interactions
✅ Navigation
✅ API Integration
✅ Error Handling

❌ Styling
❌ Platform-spezifische APIs (mocken)
❌ Third-Party Libraries
