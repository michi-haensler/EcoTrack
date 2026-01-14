# EcoTrack Prompt-Bibliothek

Organisierte Sammlung von wiederverwendbaren Prompts für das EcoTrack-Projekt.

---

## 📚 Kategorien

### 1. Testing
Prompts für Test-Erstellung und Qualitätssicherung.

#### `generate-tests.prompt.md`
**Mode**: agent  
**Variablen**: `file`, `coverage`  
**Verwendung**: Generiert Unit Tests nach AAA-Pattern

```
Generiere Tests für `src/service/LogActivityService.java` mit 85% Coverage
```

**Wann nutzen**:
- Service/Use Case ohne Tests
- Component ohne Tests
- Coverage < 80%

---

### 2. Code Quality
Prompts für Code-Verbesserung und Refactoring.

#### `refactor-code.prompt.md`
**Mode**: edit  
**Variablen**: `selection`, `focus`  
**Verwendung**: Refaktoriert Code nach Clean Code Prinzipien

```
Refaktoriere diesen Code mit Fokus auf readability: [Code selektieren]
```

**Focus-Optionen**:
- `readability` (default): Lesbarkeit verbessern
- `performance`: Performance optimieren
- `architecture`: Architektur-Patterns anwenden
- `naming`: Namen verbessern

**Wann nutzen**:
- Lange Methoden (> 20 Zeilen)
- Nested Ifs
- Magic Numbers
- Code Smells

#### `code-review.prompt.md`
**Mode**: ask  
**Variablen**: `file`, `focus`  
**Verwendung**: Systematisches Code Review

```
Review `src/service/LogActivityService.java` mit Fokus auf architecture
```

**Focus-Optionen**:
- `all` (default): Vollständiges Review
- `architecture`: Architektur & Design
- `security`: Security-Aspekte
- `performance`: Performance
- `testing`: Test Coverage & Qualität

**Wann nutzen**:
- Vor Pull Request Merge
- Nach Feature-Implementierung
- Bei Code-Qualitäts-Problemen

---

### 3. Documentation
Prompts für Dokumentations-Erstellung.

#### `generate-docs.prompt.md`
**Mode**: edit  
**Variablen**: `selection`  
**Verwendung**: Generiert JavaDoc/JSDoc Dokumentation

```
Dokumentiere diese Klasse: [Code selektieren]
```

**Wann nutzen**:
- Public APIs ohne Dokumentation
- Komplexe Business Logic
- Use Cases / Services
- Custom Hooks

---

## 🚀 Prompt-Verwendung

### In VS Code

1. **Prompt öffnen**: VS Code Command Palette → "Open Prompt"
2. **Variablen setzen**: Im Prompt-Editor Variablen angeben
3. **Ausführen**: Prompt ausführen (Mode: agent/ask/edit)

### In GitHub Copilot Chat

```
@workspace Verwende .github/prompts/generate-tests.prompt.md für src/service/LogActivityService.java
```

### Mit Agents

Agents können Prompts automatisch verwenden:
```
@requirements-engineer Erstelle User Story für Challenge-Feature
→ Agent verwendet user-story.md Template
```

---

## 📋 Prompt-Metadaten

Jeder Prompt enthält folgende Metadaten:

```yaml
---
mode: agent | ask | edit
title: "Prompt Titel"
category: "Testing | Code Quality | Documentation | ..."
description: "Kurzbeschreibung"
intent: "Wann nutzen?"
context: "Wo anwendbar?"
variables:
  - name: "var1"
    description: "Beschreibung"
    required: true | false
    default: "Defaultwert"
---
```

---

## 🎯 Best Practices

### Prompt-Erstellung

1. **Klare Intention**: Was soll der Prompt erreichen?
2. **Strukturiert**: YAML Frontmatter + Markdown Body
3. **Beispiele**: Code-Beispiele für erwarteten Output
4. **Variablen**: Parametrisierbar für Wiederverwendbarkeit
5. **Kontext**: "Wann nutzen?" klar definieren

### Prompt-Organisation

```
.github/prompts/
├── README.md                    # Diese Datei
├── generate-tests.prompt.md     # Testing
├── code-review.prompt.md        # Code Quality
├── refactor-code.prompt.md      # Code Quality
├── generate-docs.prompt.md      # Documentation
└── (weitere Prompts...)
```

**Naming**: `<verb>-<noun>.prompt.md`  
Beispiele: `generate-tests`, `refactor-code`, `review-security`

---

## 📈 Metrics & Tracking

### Prompt-Nutzung tracken

(Optional: Analytics für Prompt-Verwendung)

```yaml
# .github/prompts/.tracking.yml
prompts:
  - name: generate-tests
    usage_count: 42
    last_used: 2024-01-15
    avg_satisfaction: 4.5/5
```

---

## 🔄 Prompt-Wartung

### Review-Prozess

1. **Quarterly Review**: Alle 3 Monate Prompts prüfen
2. **Feedback sammeln**: Team-Feedback zu Prompt-Qualität
3. **Verbesserungen**: Basierend auf Usage Patterns
4. **Neue Prompts**: Bei wiederkehrenden Tasks

### Changelog

Änderungen an Prompts in `CHANGELOG.md` dokumentieren:

```markdown
## [1.2.0] - 2024-01-15

### Prompts
- Added: `generate-docs.prompt.md` für Dokumentation
- Updated: `generate-tests.prompt.md` - Coverage-Variable hinzugefügt
- Fixed: `refactor-code.prompt.md` - Beispiele korrigiert
```

---

## 📚 Weitere Prompt-Ideen

### Geplant

- [ ] `generate-migration.prompt.md` - Liquibase/Flyway Migrations
- [ ] `review-security.prompt.md` - Security-focused Review
- [ ] `optimize-performance.prompt.md` - Performance-Optimierung
- [ ] `generate-api-docs.prompt.md` - OpenAPI/Swagger Docs
- [ ] `create-adr.prompt.md` - Architecture Decision Record
- [ ] `fix-lint-errors.prompt.md` - ESLint/Checkstyle Fixes
- [ ] `update-dependencies.prompt.md` - Dependency Updates
- [ ] `create-e2e-test.prompt.md` - E2E Test Generation

### Community-Contributed

Team-Mitglieder können eigene Prompts beitragen:

1. Prompt erstellen (siehe Best Practices)
2. In `.github/prompts/` ablegen
3. Pull Request mit Beschreibung
4. Team-Review
5. Merge & README aktualisieren

---

## 🆘 Troubleshooting

### Prompt funktioniert nicht

**Problem**: Prompt liefert unerwartete Ergebnisse

**Lösungen**:
1. **Variablen prüfen**: Sind alle required Variablen gesetzt?
2. **Context prüfen**: Hat Agent genug Kontext? (siehe `CONTEXT.md`)
3. **Mode prüfen**: Ist der richtige Mode gewählt? (agent/ask/edit)
4. **Prompt verbessern**: Beispiele hinzufügen, klarer formulieren

### Agent versteht Prompt nicht

**Problem**: Agent interpretiert Prompt falsch

**Lösungen**:
1. **Expliziter sein**: Mehr Details, weniger Annahmen
2. **Beispiele hinzufügen**: Show, don't tell
3. **Schrittweise**: Prompt in kleinere Schritte aufteilen
4. **Feedback loop**: Prompt iterativ verbessern

---

## 📧 Kontakt & Feedback

Fragen, Verbesserungsvorschläge oder Bug-Reports:
- Issue erstellen: [GitHub Issues](link)
- Team-Chat: #ecotrack-dev
- Pull Request: Direkt beitragen!

---

**Version**: 1.0  
**Letzte Aktualisierung**: 2024-01-15  
**Maintainer**: EcoTrack Team
