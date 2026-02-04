---
title: "Refactor Code (Java)"
category: "Code Quality"
description: "Refaktoriert Java-Code für bessere Lesbarkeit und Wartbarkeit"
---

# Refactor Code Prompt (Java)

Refaktoriere den ausgewählten Java-Code.

## Refactoring-Prinzipien

1. **Funktionalität beibehalten**: Behaviour darf sich nicht ändern
2. **Kleine Schritte**: Inkrementelle Änderungen
3. **Tests grün halten**: Nach jedem Schritt testen

## Refactoring-Patterns

### Extract Method
```java
// Vorher
public void processOrder(Order order) {
    if (order == null) throw new IllegalArgumentException();
    if (order.getItems().isEmpty()) throw new IllegalArgumentException();
    int total = 0;
    for (Item item : order.getItems()) {
        total += item.getPrice() * item.getQuantity();
    }
    // ...
}

// Nachher
public void processOrder(Order order) {
    validateOrder(order);
    int total = calculateTotal(order);
    // ...
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
```

### Early Return
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
        }
    }
    return "0%";
}

// Nachher
public String getDiscount(User user) {
    if (user == null || !user.isPremium()) {
        return "0%";
    }
    return user.getPoints() > 100 ? "20%" : "10%";
}
```

### Extract Constant
```java
// Vorher
if (user.points > 100) {
    level = "ALTBAUM";
}

// Nachher
private static final int ALTBAUM_THRESHOLD = 100;
private static final String ALTBAUM_LEVEL = "ALTBAUM";

if (user.points > ALTBAUM_THRESHOLD) {
    level = ALTBAUM_LEVEL;
}
```

### Move to Domain Layer
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
```

## Output Format

### Zusammenfassung
[Was wurde geändert]

### Angewandte Patterns
- [Pattern]: [Begründung]

### Vorher/Nachher
```java
// Vorher
[Original]

// Nachher
[Refactored]
```

### Verbesserungen
- ✅ [Verbesserung]
