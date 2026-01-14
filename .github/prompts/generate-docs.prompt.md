---
title: "Generate Documentation"
category: "Documentation"
description: "Generiert oder aktualisiert JavaDoc/JSDoc Dokumentation für Klassen, Methoden und Funktionen"
intent: "Automatisiere die Erstellung von Code-Dokumentation"
context: "Public APIs, komplexe Methoden, Services, Components"
variables:
  - name: "selection"
    description: "Die selektierte Klasse/Methode/Funktion"
    required: true
---

# Generate Documentation Prompt

Generiere professionelle Dokumentation für den folgenden Code:

\`\`\`
${selection}
\`\`\`

## Dokumentations-Standards

### Java (JavaDoc)

#### Klassen-Dokumentation
```java
/**
 * [Kurze einzeilige Beschreibung der Klasse]
 *
 * <p>[Ausführliche mehrzeilige Beschreibung, wenn nötig]
 * 
 * <p>[Use Cases, Beispiele, wichtige Hinweise]
 *
 * @author [Author Name]
 * @since 1.0
 * @see [Related Classes]
 */
```

#### Methoden-Dokumentation
```java
/**
 * [Kurze einzeilige Beschreibung was die Methode tut]
 *
 * <p>[Detaillierte Beschreibung, wenn komplex]
 *
 * @param paramName [Beschreibung des Parameters]
 * @param anotherParam [Beschreibung des anderen Parameters]
 * @return [Beschreibung des Return Values]
 * @throws ExceptionType [Wann wird diese Exception geworfen?]
 * @see [Related Methods/Classes]
 */
```

#### Beispiel:
```java
/**
 * Service für das Logging von Nachhaltigkeits-Aktivitäten.
 *
 * <p>Dieser Service implementiert den Use Case "Aktivität loggen" und ist Teil
 * des Scoring Bounded Context. Er koordiniert das Speichern von Aktivitäten,
 * die Berechnung von Punkten und das Publishing von Domain Events.
 *
 * <p>Verwendung:
 * <pre>{@code
 * LogActivityCommand command = new LogActivityCommand(userId, actionId, 5);
 * ActivityEntryDto result = service.execute(command);
 * }</pre>
 *
 * @author EcoTrack Team
 * @since 1.0
 * @see LogActivityUseCase
 * @see ActivityEntry
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LogActivityService implements LogActivityUseCase {
    
    /**
     * Loggt eine neue Aktivität für einen User.
     *
     * <p>Diese Methode:
     * <ul>
     *   <li>Validiert die Action Definition
     *   <li>Erstellt einen neuen Activity Entry
     *   <li>Berechnet die Points
     *   <li>Speichert den Entry in der Datenbank
     *   <li>Published ein ActivityLoggedEvent
     * </ul>
     *
     * @param command das Command Object mit userId, actionId und quantity
     * @return den gespeicherten Activity Entry als DTO mit berechneten Points
     * @throws EntityNotFoundException wenn die Action Definition nicht existiert
     * @throws IllegalArgumentException wenn quantity <= 0
     */
    @Override
    public ActivityEntryDto execute(LogActivityCommand command) {
        // Implementation
    }
}
```

### TypeScript (JSDoc)

#### Komponenten-Dokumentation
```typescript
/**
 * [Kurze Beschreibung der Component]
 *
 * [Ausführliche Beschreibung, wenn nötig]
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

#### Funktionen/Hooks-Dokumentation
```typescript
/**
 * [Kurze Beschreibung was die Funktion/Hook tut]
 *
 * [Detaillierte Beschreibung]
 *
 * @param paramName - [Beschreibung des Parameters]
 * @param options - [Beschreibung der Options]
 * @returns [Beschreibung des Return Values]
 * @throws [Welche Errors können auftreten?]
 *
 * @example
 * ```typescript
 * const { data, isLoading } = useActivities('user-123');
 * ```
 */
```

#### Beispiel:
```typescript
/**
 * Activity Card Component zur Anzeige einer geloggten Aktivität.
 *
 * Zeigt Aktivitätsname, Beschreibung, Points, Quantity und Zeitstempel an.
 * Unterstützt Click-Handling für Navigation zu Detail-Ansicht.
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
 * Verwendet TanStack Query für Caching und automatische Refetches.
 * Daten werden für 5 Minuten gecached.
 *
 * @param userId - Die ID des Users dessen Aktivitäten geladen werden sollen
 * @returns Query result mit activities, loading state und error
 *
 * @example
 * ```typescript
 * const { data: activities, isLoading, error } = useActivities('user-123');
 *
 * if (isLoading) return <Spinner />;
 * if (error) return <ErrorMessage error={error} />;
 * ```
 */
export function useActivities(userId: string) {
  // Implementation
}
```

## Was dokumentieren?

### Immer dokumentieren:
- ✅ Public APIs (Klassen, Interfaces, Public Methods)
- ✅ Use Cases / Services
- ✅ Komplexe Business Logic
- ✅ Custom Hooks
- ✅ Feature Components
- ✅ Algorithmen

### Optional dokumentieren:
- 💡 Private Helper Methods (wenn komplex)
- 💡 Type Definitions (wenn nicht selbsterklärend)
- 💡 Base UI Components (wenn API unklar)

### Nicht dokumentieren:
- ❌ Triviale Getter/Setter
- ❌ Self-explanatory Code
- ❌ Tests (außer komplexe Test Setups)
- ❌ DTOs/Interfaces (wenn Namen klar sind)

## Dokumentations-Stil

### Klar & Präzise
- Kurze Sätze
- Aktive Sprache ("Erstellt einen User" statt "Ein User wird erstellt")
- Technische Begriffe konsistent verwenden

### Nützlich
- **Warum** wird etwas gemacht (nicht nur **was**)
- Wichtige Annahmen/Constraints erwähnen
- Side Effects dokumentieren
- Performance-Implikationen erwähnen

### Beispiele
- Code-Beispiele für komplexe APIs
- Use Cases beschreiben
- Typische Fehlerszenarien

## Output

Generiere die Dokumentation im entsprechenden Format (JavaDoc/JSDoc) und füge sie über der Klasse/Methode/Funktion ein.

**Wichtig**: 
- Keine generischen Phrasen ("This method does something")
- Fokus auf Value für Developer
- Beschreibe **warum** nicht nur **was**

Starte jetzt!
