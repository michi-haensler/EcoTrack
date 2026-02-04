---
title: "Generate JSDoc"
category: "Documentation"
description: "Generiert JSDoc für TypeScript-Code"
---

# Generate JSDoc Prompt

Generiere JSDoc für den ausgewählten TypeScript/React-Code.

## Standards

### Komponenten-Dokumentation
```typescript
/**
 * [Kurze Beschreibung der Component]
 *
 * [Ausführliche Beschreibung]
 *
 * @example
 * ```tsx
 * <ActivityCard
 *   activity={activity}
 *   onSelect={(id) => console.log(id)}
 * />
 * ```
 */
```

### Funktionen/Hooks-Dokumentation
```typescript
/**
 * [Kurze Beschreibung]
 *
 * @param paramName - [Beschreibung]
 * @returns [Beschreibung]
 * @throws [Welche Errors?]
 *
 * @example
 * ```typescript
 * const { data, isLoading } = useActivities('user-123');
 * ```
 */
```

### Beispiele

```typescript
/**
 * Activity Card Component zur Anzeige einer geloggten Aktivität.
 *
 * Zeigt Aktivitätsname, Points, Quantity und Zeitstempel an.
 *
 * @example
 * ```tsx
 * <ActivityCard
 *   activity={activity}
 *   onSelect={(id) => navigate(`/activities/${id}`)}
 * />
 * ```
 */
export function ActivityCard({ activity, onSelect }: ActivityCardProps) {
  // Implementation
}

/**
 * Hook zum Laden von Aktivitäten eines Users.
 *
 * Verwendet TanStack Query für Caching.
 *
 * @param userId - Die ID des Users
 * @returns Query result mit activities, loading state und error
 *
 * @example
 * ```typescript
 * const { data: activities, isLoading } = useActivities('user-123');
 * ```
 */
export function useActivities(userId: string) {
  // Implementation
}
```

## Was dokumentieren?

✅ Custom Hooks
✅ Feature Components
✅ Utility Functions
✅ Komplexe Props Interfaces

❌ Triviale Helper
❌ Self-explanatory Code
❌ Tests
