---
applyTo: "**/*.test.{ts,tsx,java},**/*Test.{ts,tsx,java},**/*test.{ts,tsx,java}"
description: "Testing Standards für alle Tests"
---

## Testing Standards

### Allgemeine Testprinzipien

#### AAA Pattern (Arrange-Act-Assert)
Alle Tests folgen diesem Muster:

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
✅ should_throwException_when_userNotFound
✅ should_calculatePoints_when_activityLogged
❌ testUser
❌ test1
```

#### Test-Isolation
- Jeder Test ist unabhängig
- Keine Reihenfolge-Abhängigkeiten
- Deterministisch (keine Random, keine Timestamps)
- Setup/Teardown mit beforeEach/afterEach

### Java Testing (JUnit 5)

#### Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class LogActivityServiceTest {
    
    @Mock
    private ActivityEntryRepository activityRepository;
    
    @Mock
    private ActionDefinitionRepository actionRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private LogActivityService service;
    
    @Test
    void should_saveActivity_when_validCommand() {
        // Arrange
        LogActivityCommand command = LogActivityCommand.builder()
                .ecoUserId(UUID.randomUUID())
                .actionDefinitionId(UUID.randomUUID())
                .quantity(5)
                .build();
        
        ActionDefinition action = ActionDefinition.builder()
                .id(command.actionDefinitionId())
                .points(10)
                .build();
        
        when(actionRepository.findById(command.actionDefinitionId()))
                .thenReturn(Optional.of(action));
        when(activityRepository.save(any())).thenReturn(savedEntry);
        
        // Act
        ActivityEntryDto result = service.execute(command);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.points()).isEqualTo(50);
        verify(eventPublisher).publish(any(ActivityLoggedEvent.class));
    }
    
    @Test
    void should_throwException_when_actionNotFound() {
        // Arrange
        LogActivityCommand command = createCommand();
        when(actionRepository.findById(any())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Action not found");
    }
}
```

#### Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScoringControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void should_return201_when_activityLogged() throws Exception {
        // Arrange
        LogActivityRequest request = new LogActivityRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                5,
                "Test"
        );
        
        // Act & Assert
        mockMvc.perform(post("/api/scoring/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.points").value(50));
    }
    
    @Test
    void should_return404_when_actionNotFound() throws Exception {
        LogActivityRequest request = new LogActivityRequest(
                UUID.randomUUID(),
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                5,
                null
        );
        
        mockMvc.perform(post("/api/scoring/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
```

#### Repository Tests
```java
@DataJpaTest
@ActiveProfiles("test")
class ActivityEntryRepositoryTest {
    
    @Autowired
    private ActivityEntryJpaRepository repository;
    
    @Test
    void should_saveActivity_when_validEntity() {
        // Arrange
        ActivityEntryJpaEntity entity = new ActivityEntryJpaEntity();
        entity.setEcoUserId(UUID.randomUUID());
        entity.setQuantity(5);
        
        // Act
        ActivityEntryJpaEntity saved = repository.save(entity);
        
        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
```

### TypeScript/JavaScript Testing (Vitest)

#### Component Tests (React Testing Library)
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

#### Hook Tests
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

#### API Tests
```typescript
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
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

### React Native Testing

```typescript
import { render, fireEvent, waitFor } from '@testing-library/react-native';

describe('ActivitiesScreen', () => {
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
});
```

### Test Doubles

#### Mocks
Für Abhängigkeiten mit komplexem Verhalten:
```typescript
const mockRepository = {
  save: vi.fn().mockResolvedValue({ id: '1' }),
  findById: vi.fn().mockResolvedValue(null),
};
```

#### Stubs
Für vorhersehbare Antworten:
```typescript
const stubEventPublisher = {
  publish: () => {},
};
```

#### Spies
Für Verhaltensüberwachung:
```typescript
const spy = vi.spyOn(api, 'getUser');
expect(spy).toHaveBeenCalledWith('1');
```

### Test Coverage

#### Ziele
- Unit Tests: 70-80%
- Integration Tests: 20-30%
- E2E Tests: 5-10%

#### Was testen?
✅ Business Logic
✅ Edge Cases
✅ Error Handling
✅ User Interactions
✅ API Integration

❌ Triviale Getter/Setter
❌ Framework-Code
❌ Third-Party Libraries

### Best Practices

1. **Ein Konzept pro Test**
   - Fokus auf eine Funktionalität
   - Klare Assertion

2. **Aussagekräftige Namen**
   - Was wird getestet?
   - Unter welchen Bedingungen?
   - Was wird erwartet?

3. **Setup minimieren**
   - Factory-Funktionen für Testdaten
   - Wiederverwendbare Fixtures

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

4. **Asynchronität**
   - await für async Operationen
   - waitFor für UI Updates
   - flush Promises bei Bedarf

5. **Snapshot Tests sparsam**
   - Nur für stabile Strukturen
   - Review bei Änderungen
   - Kein Ersatz für echte Assertions
