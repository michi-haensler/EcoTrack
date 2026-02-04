---
title: "Refactor Code (React Native)"
category: "Code Quality"
description: "Refaktoriert React Native-Code"
---

# Refactor Code Prompt (React Native)

Refaktoriere den ausgewählten React Native-Code.

## Refactoring-Patterns

### Extract Component
```typescript
// Vorher
function ActivitiesList({ activities }: Props) {
  return (
    <FlatList
      data={activities}
      renderItem={({ item }) => (
        <View style={styles.item}>
          <Text style={styles.name}>{item.name}</Text>
          <Text style={styles.points}>{item.points} Punkte</Text>
        </View>
      )}
    />
  );
}

// Nachher
const ActivityItem = React.memo(({ activity }: { activity: Activity }) => (
  <View style={styles.item}>
    <Text style={styles.name}>{activity.name}</Text>
    <Text style={styles.points}>{activity.points} Punkte</Text>
  </View>
));

function ActivitiesList({ activities }: Props) {
  const renderItem = useCallback(({ item }: { item: Activity }) => (
    <ActivityItem activity={item} />
  ), []);
  
  return (
    <FlatList
      data={activities}
      renderItem={renderItem}
      keyExtractor={item => item.id}
    />
  );
}
```

### Extract Custom Hook
```typescript
// Vorher
function ProfileScreen({ userId }: Props) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    api.getUser(userId)
      .then(setUser)
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

function ProfileScreen({ userId }: Props) {
  const { data: user, isLoading } = useUser(userId);
  // ...
}
```

### Use Design Tokens
```typescript
// Vorher
const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: '#fff',
  },
  title: {
    color: '#10b981',
    fontSize: 24,
  },
});

// Nachher
import { Colors, Spacing, Typography } from '@/theme';

const styles = StyleSheet.create({
  container: {
    padding: Spacing.md,
    backgroundColor: Colors.background,
  },
  title: {
    color: Colors.primary,
    fontSize: Typography.sizes.xl,
  },
});
```

### Optimize FlatList
```typescript
// Vorher
<FlatList
  data={data}
  renderItem={({ item }) => <Item item={item} />}
/>

// Nachher
const MemoizedItem = React.memo(Item);

<FlatList
  data={data}
  renderItem={useCallback(({ item }) => (
    <MemoizedItem item={item} />
  ), [])}
  keyExtractor={useCallback((item) => item.id, [])}
  removeClippedSubviews
  maxToRenderPerBatch={10}
  windowSize={5}
  getItemLayout={(data, index) => ({
    length: ITEM_HEIGHT,
    offset: ITEM_HEIGHT * index,
    index,
  })}
/>
```

## Output Format

### Zusammenfassung
[Was wurde geändert]

### Angewandte Patterns
- [Pattern]: [Begründung]

### Verbesserungen
- ✅ [Verbesserung]
