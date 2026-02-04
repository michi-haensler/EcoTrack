---
applyTo: "**/*.test.{ts,tsx}"
description: "Testing Standards für Admin-Web"
---

## TypeScript Testing Standards (Vitest)

### Allgemeine Testprinzipien

#### AAA Pattern (Arrange-Act-Assert)
```typescript
test('should_calculateTotal_when_itemsProvided', () => {
  // Arrange - Setup
  const items = [{ price: 10 }, { price: 20 }];
  const calculator = new PriceCalculator();
  
  // Act - Ausführung
  const result = calculator.calculateTotal(items);
  
  // Assert - Überprüfung
  expect(result).toBe(30);
});
```

#### Test-Benennung
Format: `should_<expected>_when_<condition>`

```
✅ should_returnUser_when_validIdProvided
✅ should_displayError_when_apiFails
❌ testUser
❌ test1
```

### Component Tests (React Testing Library)

```typescript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

describe('UserCard', () => {
  const wrapper = ({ children }: { children: React.ReactNode }) => {
    const queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    });
    return (
      <QueryClientProvider client={queryClient}>
        {children}
      </QueryClientProvider>
    );
  };
  
  it('should_displayUserInfo_when_rendered', () => {
    // Arrange
    const user = {
      id: '1',
      userId: 'test-user',
      totalPoints: 100,
      level: 'BAUM' as const,
    };
    
    // Act
    render(<UserCard user={user} onSelect={vi.fn()} />, { wrapper });
    
    // Assert
    expect(screen.getByText('test-user')).toBeInTheDocument();
    expect(screen.getByText('100 Punkte')).toBeInTheDocument();
  });
  
  it('should_callOnSelect_when_clicked', () => {
    // Arrange
    const onSelect = vi.fn();
    const user = { id: '1', userId: 'test', totalPoints: 100, level: 'BAUM' as const };
    
    render(<UserCard user={user} onSelect={onSelect} />, { wrapper });
    
    // Act
    fireEvent.click(screen.getByRole('button'));
    
    // Assert
    expect(onSelect).toHaveBeenCalledWith('1');
  });
});
```

### Hook Tests

```typescript
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

describe('useEcoUser', () => {
  const wrapper = ({ children }: { children: React.ReactNode }) => {
    const queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    });
    return (
      <QueryClientProvider client={queryClient}>
        {children}
      </QueryClientProvider>
    );
  };
  
  it('should_loadUser_when_hookCalled', async () => {
    // Arrange
    const mockUser = { id: '1', name: 'Test' };
    vi.spyOn(api, 'getUser').mockResolvedValue(mockUser);
    
    // Act
    const { result } = renderHook(() => useEcoUser('1'), { wrapper });
    
    // Assert
    await waitFor(() => {
      expect(result.current.user).toEqual(mockUser);
      expect(result.current.loading).toBe(false);
    });
  });
});
```

### API Tests (MSW)

```typescript
import { describe, it, expect, beforeEach, afterEach, afterAll } from 'vitest';
import { setupServer } from 'msw/node';
import { http, HttpResponse } from 'msw';

const server = setupServer();

beforeEach(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('API Client', () => {
  it('should_fetchUser_when_validId', async () => {
    // Arrange
    const mockUser = { id: '1', name: 'Test' };
    
    server.use(
      http.get('/api/users/1', () => {
        return HttpResponse.json(mockUser);
      })
    );
    
    // Act
    const user = await api.getUser('1');
    
    // Assert
    expect(user).toEqual(mockUser);
  });
  
  it('should_throwError_when_userNotFound', async () => {
    // Arrange
    server.use(
      http.get('/api/users/999', () => {
        return new HttpResponse(null, { status: 404 });
      })
    );
    
    // Act & Assert
    await expect(api.getUser('999')).rejects.toThrow(ApiError);
  });
});
```

### Test Data Factory

```typescript
// Test Data Factory
function createTestUser(overrides?: Partial<User>): User {
  return {
    id: '1',
    userId: 'test-user',
    totalPoints: 0,
    level: 'SETZLING',
    ...overrides,
  };
}

// Usage
const user = createTestUser({ totalPoints: 100 });
```

### Test Doubles

#### Mocks
```typescript
const mockRepository = {
  save: vi.fn().mockResolvedValue({ id: '1' }),
  findById: vi.fn().mockResolvedValue(null),
};
```

#### Spies
```typescript
const spy = vi.spyOn(api, 'getUser');
expect(spy).toHaveBeenCalledWith('1');
```

### Coverage-Ziele

- Unit Tests: 70-80%
- Integration Tests: 20-30%

### Was testen?

✅ Business Logic
✅ User Interactions
✅ API Integration
✅ Error Handling
✅ Conditional Rendering

❌ Triviale Props-Weitergabe
❌ Styling
❌ Third-Party Libraries
