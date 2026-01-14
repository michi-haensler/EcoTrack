---
title: "Refactor Code"
category: "Code Quality"
description: "Refaktoriert Code für bessere Lesbarkeit, Wartbarkeit und Performance unter Beibehaltung der Funktionalität"
intent: "Verbessere Code-Qualität ohne Behaviour zu ändern"
context: "Legacy Code, Code Smells, Performance Issues, Komplexe Methoden"
variables:
  - name: "selection"
    description: "Der zu refaktorierende Code"
    required: true
  - name: "focus"
    description: "Refactoring-Fokus: readability, performance, architecture, naming (default: readability)"
    required: false
    default: "readability"
---

# Refactor Code Prompt

Refaktoriere den folgenden Code mit Fokus auf: **${focus}**

\`\`\`
${selection}
\`\`\`

## Refactoring-Prinzipien

### Grundregeln
1. **Funktionalität beibehalten**: Behaviour darf sich nicht ändern
2. **Kleine Schritte**: Inkrementelle Änderungen
3. **Tests grün halten**: Nach jedem Schritt testen
4. **Lesbarkeit über Cleverness**: Code für Menschen schreiben

## Refactoring-Patterns

### 1. Readability (Standard)

#### Extract Method
**Problem**: Lange Methode (> 20 Zeilen), schwer zu verstehen
```java
// Vorher
public void processOrder(Order order) {
    // Validation
    if (order == null) throw new IllegalArgumentException();
    if (order.getItems().isEmpty()) throw new IllegalArgumentException();
    // Calculate total
    int total = 0;
    for (Item item : order.getItems()) {
        total += item.getPrice() * item.getQuantity();
    }
    // Apply discount
    if (order.getCustomer().isPremium()) {
        total = total * 0.9;
    }
    // Save
    repository.save(order);
}

// Nachher
public void processOrder(Order order) {
    validateOrder(order);
    int total = calculateTotal(order);
    int finalTotal = applyDiscount(total, order.getCustomer());
    order.setTotal(finalTotal);
    repository.save(order);
}

private void validateOrder(Order order) {
    if (order == null || order.getItems().isEmpty()) {
        throw new IllegalArgumentException("Invalid order");
    }
}

private int calculateTotal(Order order) {
    return order.getItems().stream()
        .mapToInt(item -> item.getPrice() * item.getQuantity())
        .sum();
}

private int applyDiscount(int total, Customer customer) {
    return customer.isPremium() ? (int) (total * 0.9) : total;
}
```

#### Early Return
**Problem**: Nested Ifs, schwer zu folgen
```java
// Vorher
public String getDiscount(User user) {
    if (user != null) {
        if (user.isPremium()) {
            if (user.getPoints() > 100) {
                return "20%";
            } else {
                return "10%";
            }
        } else {
            return "0%";
        }
    } else {
        return "0%";
    }
}

// Nachher
public String getDiscount(User user) {
    if (user == null || !user.isPremium()) {
        return "0%";
    }
    
    return user.getPoints() > 100 ? "20%" : "10%";
}
```

#### Extract Constant
**Problem**: Magic Numbers/Strings
```typescript
// Vorher
if (user.points > 100) {
    level = "ALTBAUM";
}

// Nachher
const ALTBAUM_THRESHOLD = 100;
const ALTBAUM_LEVEL = "ALTBAUM";

if (user.points > ALTBAUM_THRESHOLD) {
    level = ALTBAUM_LEVEL;
}
```

#### Rename Variable
**Problem**: Unklare Namen
```java
// Vorher
int d = 30; // days
List<ActivityEntry> ae = repo.find(d);

// Nachher
int daysToFetch = 30;
List<ActivityEntry> recentActivities = repository.findRecent(daysToFetch);
```

### 2. Performance

#### Replace Loop with Stream
```java
// Vorher
List<ActivityEntry> filtered = new ArrayList<>();
for (ActivityEntry entry : entries) {
    if (entry.getPoints() > 10) {
        filtered.add(entry);
    }
}

// Nachher
List<ActivityEntry> filtered = entries.stream()
    .filter(entry -> entry.getPoints() > 10)
    .collect(Collectors.toList());
```

#### Lazy Initialization
```java
// Vorher
private ExpensiveObject obj = new ExpensiveObject(); // Immer erstellt

// Nachher
private ExpensiveObject obj;

public ExpensiveObject getObj() {
    if (obj == null) {
        obj = new ExpensiveObject();
    }
    return obj;
}
```

#### Use Cache
```typescript
// Vorher
function getActionDefinition(id: string) {
    return api.get(`/actions/${id}`); // Jedes Mal API Call
}

// Nachher
const cache = new Map<string, ActionDefinition>();

function getActionDefinition(id: string) {
    if (cache.has(id)) {
        return cache.get(id)!;
    }
    
    const action = await api.get(`/actions/${id}`);
    cache.set(id, action);
    return action;
}
```

### 3. Architecture

#### Extract Interface
```java
// Vorher
@Service
public class EmailService {
    public void send(String to, String subject, String body) {
        // Implementation
    }
}

// Nachher
public interface NotificationService {
    void send(String to, String subject, String body);
}

@Service
public class EmailNotificationService implements NotificationService {
    @Override
    public void send(String to, String subject, String body) {
        // Implementation
    }
}
```

#### Replace Conditional with Polymorphism
```java
// Vorher
public int calculatePoints(String activityType, int quantity) {
    if (activityType.equals("BIKE")) {
        return quantity * 10;
    } else if (activityType.equals("WALK")) {
        return quantity * 5;
    } else if (activityType.equals("BUS")) {
        return quantity * 3;
    }
    return 0;
}

// Nachher
interface ActivityType {
    int calculatePoints(int quantity);
}

class BikeActivity implements ActivityType {
    public int calculatePoints(int quantity) {
        return quantity * 10;
    }
}

class WalkActivity implements ActivityType {
    public int calculatePoints(int quantity) {
        return quantity * 5;
    }
}
```

#### Move to Domain Layer
```java
// Vorher (Business Logic im Service)
@Service
public class LogActivityService {
    public int calculatePoints(ActivityEntry entry, ActionDefinition action) {
        return entry.getQuantity() * action.getPoints();
    }
}

// Nachher (Business Logic in Domain)
public class ActivityEntry {
    public int calculatePoints(ActionDefinition action) {
        return this.quantity * action.getPoints();
    }
}

@Service
public class LogActivityService {
    public ActivityEntryDto execute(LogActivityCommand command) {
        // ...
        int points = entry.calculatePoints(action); // Delegiert an Domain
        // ...
    }
}
```

### 4. Naming

#### Class Naming
```
✅ LogActivityService (Verb + Subject + Service)
✅ ActivityEntry (Noun)
✅ CreateChallengeCommand (Verb + Subject + Type)
✅ ActivityLoggedEvent (Subject + Verb(Past) + Event)

❌ ActivityProcessor (Was macht es?)
❌ Manager, Handler (Zu generisch)
❌ Util, Helper (Zu vage)
```

#### Method Naming
```
✅ calculatePoints() (Verb)
✅ isValid() (Boolean: is/has/can)
✅ findById() (Query: find/get/fetch)
✅ createActivity() (Command: create/update/delete)

❌ process() (Zu vage)
❌ doStuff() (Nicht aussagekräftig)
❌ getData() (Was für Daten?)
```

## Output Format

Liefere das Refactoring-Ergebnis wie folgt:

### Zusammenfassung
[Kurze Beschreibung was geändert wurde]

### Angewandte Patterns
- [Pattern 1]: [Begründung]
- [Pattern 2]: [Begründung]

### Vorher/Nachher
```[language]
// Vorher
[Original Code]

// Nachher
[Refactored Code]
```

### Verbesserungen
- ✅ [Verbesserung 1]
- ✅ [Verbesserung 2]

### Weitere Optimierungen (Optional)
- 💡 [Vorschlag 1]
- 💡 [Vorschlag 2]

### Tests
[Welche Tests müssen angepasst werden?]

## Beispiel-Output

### Zusammenfassung
Extrahierte lange Methode in kleinere, sprechende Methoden. Verwendete Early Returns für bessere Lesbarkeit.

### Angewandte Patterns
- **Extract Method**: `processOrder()` war zu lang (45 Zeilen)
- **Early Return**: Nested Ifs reduziert
- **Extract Constant**: Magic Numbers entfernt

### Vorher/Nachher
```java
// Vorher
public void processOrder(Order order) {
    if (order != null) {
        if (!order.getItems().isEmpty()) {
            int total = 0;
            for (Item item : order.getItems()) {
                total += item.getPrice() * item.getQuantity();
            }
            if (order.getCustomer().isPremium()) {
                total = (int) (total * 0.9);
            }
            order.setTotal(total);
            repository.save(order);
        }
    }
}

// Nachher
private static final double PREMIUM_DISCOUNT = 0.9;

public void processOrder(Order order) {
    validateOrder(order);
    int total = calculateTotal(order);
    int finalTotal = applyDiscount(total, order.getCustomer());
    order.setTotal(finalTotal);
    repository.save(order);
}

private void validateOrder(Order order) {
    if (order == null || order.getItems().isEmpty()) {
        throw new IllegalArgumentException("Invalid order");
    }
}

private int calculateTotal(Order order) {
    return order.getItems().stream()
        .mapToInt(item -> item.getPrice() * item.getQuantity())
        .sum();
}

private int applyDiscount(int total, Customer customer) {
    return customer.isPremium() 
        ? (int) (total * PREMIUM_DISCOUNT)
        : total;
}
```

### Verbesserungen
- ✅ Cyclomatic Complexity reduziert (von 5 auf 2)
- ✅ Lesbarkeit verbessert durch sprechende Methoden
- ✅ Single Responsibility: Jede Methode hat klaren Fokus
- ✅ Magic Number entfernt (0.9 → PREMIUM_DISCOUNT)

### Weitere Optimierungen
- 💡 `calculateTotal()` könnte nach `Order` Domain Class verschoben werden
- 💡ählige` Discount Strategy Pattern verwenden für flexiblere Rabatte

### Tests
- Unit Tests für `validateOrder()`, `calculateTotal()`, `applyDiscount()` hinzufügen
- Bestehende Tests für `processOrder()` sollten weiterhin grün sein

---

Starte jetzt das Refactoring!
