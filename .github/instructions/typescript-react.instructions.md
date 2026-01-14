---
applyTo: "admin-web/**/*.{ts,tsx}"
description: "TypeScript & React Standards für Admin-Web"
---

## TypeScript & React Standards

### TypeScript Best Practices

#### Type Safety
- **Niemals** `any` verwenden (außer bei untyped Third-Party Libraries)
- `unknown` statt `any` für unbekannte Typen
- Explizite Return Types für Funktionen
- Type Guards für Runtime-Checks

```typescript
// ✅ Richtig
interface User {
  id: string;
  name: string;
  role: 'student' | 'teacher' | 'admin';
}

function getUser(id: string): User | null {
  // ...
}

// ❌ Falsch
function getUser(id: any): any {
  // ...
}
```

#### Type Definitions
- `interface` für Objektstrukturen
- `type` für Unions, Intersections, Primitives
- Keine redundanten Type Aliases

```typescript
// Interfaces
interface EcoUser {
  id: string;
  userId: string;
  totalPoints: number;
  level: Level;
}

// Types
type Level = 'SETZLING' | 'JUNGBAUM' | 'BAUM' | 'ALTBAUM';
type Result<T> = { success: true; data: T } | { success: false; error: string };
```

#### Nullability
- Optional Chaining: `user?.profile?.name`
- Nullish Coalescing: `value ?? defaultValue`
- Keine `== null` Checks (außer explizit beide null/undefined)

```typescript
// ✅ Richtig
const name = user?.profile?.name ?? 'Anonymous';

// ❌ Falsch
const name = user && user.profile && user.profile.name || 'Anonymous';
```

### React Best Practices

#### Komponenten-Struktur
- **Funktionale Komponenten** mit Hooks (keine Class Components)
- Ein Component pro Datei
- Props Interface/Type vor Component Definition

```typescript
interface UserCardProps {
  user: EcoUser;
  onSelect: (userId: string) => void;
  className?: string;
}

export function UserCard({ user, onSelect, className }: UserCardProps) {
  return (
    <div className={cn('card', className)} onClick={() => onSelect(user.id)}>
      <h3>{user.userId}</h3>
      <p>{user.totalPoints} Punkte</p>
    </div>
  );
}
```

#### Hooks
- Hooks nur auf Top-Level aufrufen (keine Conditionals)
- Custom Hooks für wiederverwendbare Logik
- `use` Präfix für Custom Hooks

```typescript
// Custom Hook
function useEcoUser(userId: string) {
  const [user, setUser] = useState<EcoUser | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  
  useEffect(() => {
    async function fetchUser() {
      try {
        const data = await api.getUser(userId);
        setUser(data);
      } catch (err) {
        setError(err as Error);
      } finally {
        setLoading(false);
      }
    }
    
    fetchUser();
  }, [userId]);
  
  return { user, loading, error };
}
```

#### State Management
- `useState` für lokalen State
- TanStack Query für Server State
- Context API für globale Themes/Auth
- Zustand/Jotai für komplexen Client State (falls nötig)

```typescript
// TanStack Query
function useActivities(userId: string) {
  return useQuery({
    queryKey: ['activities', userId],
    queryFn: () => api.getActivities(userId),
    staleTime: 5 * 60 * 1000, // 5 Minuten
  });
}

// Component
function ActivitiesList({ userId }: Props) {
  const { data: activities, isLoading, error } = useActivities(userId);
  
  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;
  
  return (
    <ul>
      {activities?.map(activity => (
        <ActivityItem key={activity.id} activity={activity} />
      ))}
    </ul>
  );
}
```

#### Performance
- `useMemo` für teure Berechnungen
- `useCallback` für Callbacks in Props
- `React.memo` für reine Komponenten
- Virtual Lists für lange Listen

```typescript
const MemoizedUserCard = React.memo(UserCard);

function UserList({ users }: Props) {
  const sortedUsers = useMemo(
    () => [...users].sort((a, b) => b.totalPoints - a.totalPoints),
    [users]
  );
  
  const handleSelect = useCallback((userId: string) => {
    console.log('Selected:', userId);
  }, []);
  
  return (
    <div>
      {sortedUsers.map(user => (
        <MemoizedUserCard key={user.id} user={user} onSelect={handleSelect} />
      ))}
    </div>
  );
}
```

### Forms & Validation

- React Hook Form für komplexe Forms
- Zod für Runtime Validation + TypeScript Types

```typescript
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const logActivitySchema = z.object({
  actionDefinitionId: z.string().uuid(),
  quantity: z.number().min(1).max(100),
  notes: z.string().optional(),
});

type LogActivityForm = z.infer<typeof logActivitySchema>;

function LogActivityForm() {
  const { register, handleSubmit, formState: { errors } } = useForm<LogActivityForm>({
    resolver: zodResolver(logActivitySchema),
  });
  
  const onSubmit = (data: LogActivityForm) => {
    // ...
  };
  
  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input {...register('actionDefinitionId')} />
      {errors.actionDefinitionId && <span>{errors.actionDefinitionId.message}</span>}
      {/* ... */}
    </form>
  );
}
```

### Styling (Tailwind CSS)

- Utility Classes bevorzugen
- `cn()` Helper für Conditional Classes
- Design Tokens über CSS Variables
- Komponenten-Varianten mit cva (class-variance-authority)

```typescript
import { cn } from '@/lib/utils';
import { cva, type VariantProps } from 'class-variance-authority';

const buttonVariants = cva(
  'inline-flex items-center justify-center rounded-md font-medium transition-colors',
  {
    variants: {
      variant: {
        primary: 'bg-green-600 text-white hover:bg-green-700',
        secondary: 'bg-gray-200 text-gray-900 hover:bg-gray-300',
      },
      size: {
        sm: 'h-8 px-3 text-sm',
        md: 'h-10 px-4 text-base',
        lg: 'h-12 px-6 text-lg',
      },
    },
    defaultVariants: {
      variant: 'primary',
      size: 'md',
    },
  }
);

interface ButtonProps extends VariantProps<typeof buttonVariants> {
  children: React.ReactNode;
  className?: string;
}

export function Button({ variant, size, className, children, ...props }: ButtonProps) {
  return (
    <button className={cn(buttonVariants({ variant, size }), className)} {...props}>
      {children}
    </button>
  );
}
```

### API Integration

#### Fetch Wrapper
```typescript
class ApiClient {
  private baseUrl: string;
  private token: string | null = null;
  
  constructor(baseUrl: string) {
    this.baseUrl = baseUrl;
  }
  
  setToken(token: string) {
    this.token = token;
  }
  
  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const headers = {
      'Content-Type': 'application/json',
      ...(this.token && { Authorization: `Bearer ${this.token}` }),
      ...options.headers,
    };
    
    const response = await fetch(`${this.baseUrl}${endpoint}`, {
      ...options,
      headers,
    });
    
    if (!response.ok) {
      throw new ApiError(response.status, await response.text());
    }
    
    return response.json();
  }
  
  async get<T>(endpoint: string) {
    return this.request<T>(endpoint);
  }
  
  async post<T>(endpoint: string, data: unknown) {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }
}

export const api = new ApiClient(import.meta.env.VITE_API_URL);
```

### Error Handling

```typescript
class ApiError extends Error {
  constructor(public status: number, message: string) {
    super(message);
    this.name = 'ApiError';
  }
}

function ErrorBoundary({ error }: { error: Error }) {
  if (error instanceof ApiError) {
    return <div>API Error {error.status}: {error.message}</div>;
  }
  
  return <div>Unexpected Error: {error.message}</div>;
}
```

### Testing

#### Component Tests (Vitest + React Testing Library)
```typescript
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';

describe('UserCard', () => {
  it('should call onSelect when clicked', () => {
    const onSelect = vi.fn();
    const user = { id: '1', userId: 'test', totalPoints: 100, level: 'BAUM' };
    
    render(<UserCard user={user} onSelect={onSelect} />);
    
    fireEvent.click(screen.getByRole('button'));
    
    expect(onSelect).toHaveBeenCalledWith('1');
  });
});
```

### File Organization

```
src/
├── components/        # Wiederverwendbare UI-Komponenten
│   ├── ui/           # Basis-UI-Komponenten (Button, Input, etc.)
│   └── features/     # Feature-spezifische Komponenten
├── pages/            # Route-Komponenten
├── hooks/            # Custom Hooks
├── lib/              # Utilities, Helpers
│   ├── api.ts
│   ├── utils.ts
│   └── cn.ts
├── types/            # TypeScript Types
└── config/           # Konfiguration
```

### Naming

- PascalCase: Komponenten, Interfaces, Types
- camelCase: Variablen, Funktionen, Hooks
- UPPER_CASE: Konstanten
- kebab-case: Dateinamen

```
UserCard.tsx
useEcoUser.ts
api-client.ts
types.ts
```
