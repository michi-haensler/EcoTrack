---
title: "Generate Tests (TypeScript)"
category: "Testing"
description: "Generiert Tests für React Components und Hooks"
---

# Generate Tests Prompt (TypeScript)

Generiere Tests für die angegebene TypeScript/React-Datei.

## Anforderungen

### Test-Framework
- Vitest + React Testing Library

### Test-Pattern
- **AAA Pattern**: Arrange - Act - Assert
- **Benennung**: `should_<expected>_when_<condition>`
- **Coverage-Ziel**: 80%

### Test-Arten

#### Für Components:
1. **Rendering Tests**
   - Component rendert ohne Fehler
   - Props werden angezeigt

2. **Interaction Tests**
   - Click Handlers
   - Form Submission
   - State Updates

3. **Integration Tests**
   - API Calls mit MSW
   - TanStack Query Hooks

#### Für Hooks:
1. **Return Values**
2. **Side Effects**
3. **Error Handling**

## Beispiel-Output

### Component Test

```typescript
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { UserCard } from './user-card';

describe('UserCard', () => {
  it('should_displayUserInfo_when_rendered', () => {
    // Arrange
    const user = { id: '1', name: 'Test', points: 100 };
    
    // Act
    render(<UserCard user={user} onSelect={vi.fn()} />);
    
    // Assert
    expect(screen.getByText('Test')).toBeInTheDocument();
    expect(screen.getByText('100 Punkte')).toBeInTheDocument();
  });
  
  it('should_callOnSelect_when_clicked', () => {
    // Arrange
    const onSelect = vi.fn();
    const user = { id: '1', name: 'Test', points: 100 };
    
    render(<UserCard user={user} onSelect={onSelect} />);
    
    // Act
    fireEvent.click(screen.getByRole('button'));
    
    // Assert
    expect(onSelect).toHaveBeenCalledWith('1');
  });
});
```

### Hook Test

```typescript
import { renderHook, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { useActivities } from './use-activities';

describe('useActivities', () => {
  it('should_fetchData_when_mounted', async () => {
    // Arrange
    const mockData = [{ id: '1', name: 'Activity' }];
    vi.spyOn(api, 'getActivities').mockResolvedValue(mockData);
    
    // Act
    const { result } = renderHook(() => useActivities('user-1'), { wrapper });
    
    // Assert
    await waitFor(() => {
      expect(result.current.data).toEqual(mockData);
      expect(result.current.isLoading).toBe(false);
    });
  });
});
```
