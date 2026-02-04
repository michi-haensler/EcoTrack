---
title: "Generate JavaDoc"
category: "Documentation"
description: "Generiert JavaDoc für Klassen und Methoden"
---

# Generate JavaDoc Prompt

Generiere JavaDoc für den ausgewählten Java-Code.

## Standards

### Klassen-Dokumentation
```java
/**
 * [Kurze einzeilige Beschreibung der Klasse]
 *
 * <p>[Ausführliche Beschreibung]
 *
 * @author EcoTrack Team
 * @since 1.0
 * @see [Related Classes]
 */
```

### Methoden-Dokumentation
```java
/**
 * [Kurze Beschreibung was die Methode tut]
 *
 * <p>[Detaillierte Beschreibung]
 *
 * @param paramName [Beschreibung]
 * @return [Beschreibung des Return Values]
 * @throws ExceptionType [Wann wird diese Exception geworfen?]
 */
```

### Beispiel

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
public class LogActivityService implements LogActivityUseCase {
    
    /**
     * Loggt eine neue Aktivität für einen User.
     *
     * @param command das Command mit userId, actionId und quantity
     * @return den gespeicherten Activity Entry als DTO
     * @throws EntityNotFoundException wenn die Action nicht existiert
     */
    @Override
    public ActivityEntryDto execute(LogActivityCommand command) {
        // Implementation
    }
}
```

## Was dokumentieren?

✅ Public APIs (Klassen, Public Methods)
✅ Use Cases / Services
✅ Komplexe Business Logic
✅ Domain Events

❌ Triviale Getter/Setter
❌ Self-explanatory Code
❌ Tests
