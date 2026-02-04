---
title: "Generate Tests (React Native)"
category: "Testing"
description: "Generiert Tests für React Native Screens und Components"
---

# Generate Tests Prompt (React Native)

Generiere Tests für die angegebene React Native-Datei.

## Anforderungen

### Test-Framework
- Jest + React Native Testing Library

### Test-Pattern
- **AAA Pattern**: Arrange - Act - Assert
- **Benennung**: `should_<expected>_when_<condition>`
- **Coverage-Ziel**: 80%

### Test-Arten

#### Für Screens:
1. **Rendering Tests**
2. **Interaction Tests**
3. **Navigation Tests**
4. **API Integration Tests**

#### Für Components:
1. **Props Rendering**
2. **User Interactions**
3. **Callbacks**

## Beispiel-Output

### Screen Test

```typescript
import { render, fireEvent, waitFor } from '@testing-library/react-native';
import { NavigationContainer } from '@react-navigation/native';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

describe('ActivitiesScreen', () => {
  const wrapper = ({ children }) => (
    <QueryClientProvider client={queryClient}>
      <NavigationContainer>
        {children}
      </NavigationContainer>
    </QueryClientProvider>
  );
  
  it('should_displayActivities_when_loaded', async () => {
    // Arrange
    vi.spyOn(api, 'getActivities').mockResolvedValue(mockActivities);
    
    // Act
    const { getByText } = render(<ActivitiesScreen />, { wrapper });
    
    // Assert
    await waitFor(() => {
      expect(getByText('Radfahren')).toBeTruthy();
    });
  });
});
```

### Component Test

```typescript
import { render, fireEvent } from '@testing-library/react-native';

describe('ActivityItem', () => {
  it('should_callOnPress_when_tapped', () => {
    // Arrange
    const onPress = jest.fn();
    
    // Act
    const { getByTestId } = render(
      <ActivityItem activity={activity} onPress={onPress} />
    );
    fireEvent.press(getByTestId('activity-item'));
    
    // Assert
    expect(onPress).toHaveBeenCalledWith('1');
  });
});
```

### Navigation Mock

```typescript
const mockNavigation = {
  navigate: jest.fn(),
  goBack: jest.fn(),
};

it('should_navigate_when_buttonPressed', () => {
  const { getByText } = render(
    <ProfileScreen navigation={mockNavigation as any} />
  );
  
  fireEvent.press(getByText('Settings'));
  
  expect(mockNavigation.navigate).toHaveBeenCalledWith('Settings');
});
```
