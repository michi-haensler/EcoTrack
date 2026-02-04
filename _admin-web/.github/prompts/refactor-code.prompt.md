---
title: "Refactor Code (TypeScript)"
category: "Code Quality"
description: "Refaktoriert TypeScript/React-Code"
---

# Refactor Code Prompt (TypeScript)

Refaktoriere den ausgewählten TypeScript/React-Code.

## Refactoring-Prinzipien

1. **Funktionalität beibehalten**
2. **Kleine Schritte**
3. **Tests grün halten**

## Refactoring-Patterns

### Extract Component
```typescript
// Vorher
function UserList({ users }: Props) {
  return (
    <ul>
      {users.map(user => (
        <li key={user.id}>
          <div className="card">
            <h3>{user.name}</h3>
            <p>{user.points} Punkte</p>
            <button onClick={() => selectUser(user.id)}>
              Auswählen
            </button>
          </div>
        </li>
      ))}
    </ul>
  );
}

// Nachher
function UserCard({ user, onSelect }: UserCardProps) {
  return (
    <div className="card">
      <h3>{user.name}</h3>
      <p>{user.points} Punkte</p>
      <button onClick={() => onSelect(user.id)}>
        Auswählen
      </button>
    </div>
  );
}

function UserList({ users, onSelectUser }: Props) {
  return (
    <ul>
      {users.map(user => (
        <li key={user.id}>
          <UserCard user={user} onSelect={onSelectUser} />
        </li>
      ))}
    </ul>
  );
}
```

### Extract Custom Hook
```typescript
// Vorher
function ProfilePage({ userId }: Props) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    api.getUser(userId)
      .then(setUser)
      .catch(setError)
      .finally(() => setLoading(false));
  }, [userId]);
  
  // ...
}

// Nachher
function useUser(userId: string) {
  return useQuery({
    queryKey: ['user', userId],
    queryFn: () => api.getUser(userId),
  });
}

function ProfilePage({ userId }: Props) {
  const { data: user, isLoading, error } = useUser(userId);
  // ...
}
```

### Use cva for Variants
```typescript
// Vorher
function Button({ variant, size, children }: Props) {
  const classes = `btn ${variant === 'primary' ? 'btn-primary' : 'btn-secondary'} ${size === 'lg' ? 'btn-lg' : 'btn-sm'}`;
  return <button className={classes}>{children}</button>;
}

// Nachher
const buttonVariants = cva('btn', {
  variants: {
    variant: {
      primary: 'btn-primary',
      secondary: 'btn-secondary',
    },
    size: {
      sm: 'btn-sm',
      lg: 'btn-lg',
    },
  },
  defaultVariants: {
    variant: 'primary',
    size: 'sm',
  },
});

function Button({ variant, size, children }: ButtonProps) {
  return (
    <button className={buttonVariants({ variant, size })}>
      {children}
    </button>
  );
}
```

### Replace Conditional Rendering
```typescript
// Vorher
function DataView({ data, loading, error }: Props) {
  if (loading) {
    return <Spinner />;
  }
  
  if (error) {
    return <ErrorMessage error={error} />;
  }
  
  if (!data || data.length === 0) {
    return <EmptyState />;
  }
  
  return <DataList data={data} />;
}

// Nachher (mit Pattern Matching Helper)
function DataView({ data, loading, error }: Props) {
  return match({ data, loading, error })
    .with({ loading: true }, () => <Spinner />)
    .with({ error: P.not(null) }, ({ error }) => <ErrorMessage error={error} />)
    .with({ data: P.when(d => !d?.length) }, () => <EmptyState />)
    .otherwise(({ data }) => <DataList data={data} />);
}
```

## Output Format

### Zusammenfassung
[Was wurde geändert]

### Angewandte Patterns
- [Pattern]: [Begründung]

### Vorher/Nachher
```typescript
// Vorher
[Original]

// Nachher
[Refactored]
```

### Verbesserungen
- ✅ [Verbesserung]
