# ADR-XXX: [Titel der Architekturentscheidung]

**Status**: [Vorgeschlagen | Akzeptiert | Abgelehnt | Überholt | Deprecated]

**Datum**: YYYY-MM-DD

**Autor**: [Name]

---

## Kontext

**Was ist das Problem oder die Fragestellung?**

Beschreibe den technischen/architektonischen Kontext, der diese Entscheidung erforderlich macht:
- Welches Problem lösen wir?
- Warum ist diese Entscheidung wichtig?
- Welche Einschränkungen/Rahmenbedingungen gibt es?
- Was sind die treibenden Faktoren?

## Entscheidung

**Was haben wir entschieden?**

Beschreibe klar und präzise die getroffene Entscheidung:
- Welche Lösung/Architektur/Technologie wird gewählt?
- Wie wird diese umgesetzt?
- Welche wichtigen Details sind zu beachten?

## Begründung

**Warum haben wir uns dafür entschieden?**

Erkläre die Gründe für diese Entscheidung:
- Welche Vorteile bietet diese Lösung?
- Welche Anforderungen erfüllt sie?
- Warum ist sie besser als die Alternativen?
- Welche Trade-offs wurden akzeptiert?

## Konsequenzen

### Positiv

**Was sind die Vorteile?**

- ✅ [Vorteil 1]
- ✅ [Vorteil 2]
- ✅ [Vorteil 3]

### Negativ

**Was sind die Nachteile?**

- ❌ [Nachteil 1]
- ❌ [Nachteil 2]
- ❌ [Nachteil 3]

### Risiken

**Welche Risiken gibt es?**

- ⚠️ [Risiko 1 + Mitigation]
- ⚠️ [Risiko 2 + Mitigation]

### Technische Schuld

**Welche technische Schuld entsteht?**

- 💰 [Technische Schuld 1]
- 💰 [Technische Schuld 2]

## Alternativen

**Welche Alternativen wurden erwogen?**

### Alternative A: [Name]

**Beschreibung**: [Kurzbeschreibung]

**Pro**:
- [Vorteil 1]
- [Vorteil 2]

**Contra**:
- [Nachteil 1]
- [Nachteil 2]

**Warum nicht gewählt**: [Begründung]

### Alternative B: [Name]

**Beschreibung**: [Kurzbeschreibung]

**Pro**:
- [Vorteil 1]

**Contra**:
- [Nachteil 1]

**Warum nicht gewählt**: [Begründung]

## Betroffene Module/Komponenten

- `module-scoring`
- `module-challenge`
- `admin-web`
- `mobile`

## Metriken/Erfolgs-Kriterien

Wie messen wir, ob diese Entscheidung erfolgreich war?

- [Metrik 1: z.B. Performance < 100ms]
- [Metrik 2: z.B. Code Coverage > 80%]
- [Metrik 3: z.B. Entwicklungszeit reduziert um 20%]

## Weitere Referenzen

- [Link zu related ADRs]
- [Link zu Dokumentation]
- [Link zu Diskussionen/Issues]
- [Link zu externen Ressourcen]

---

## Beispiel: ADR ausgefüllt

# ADR-001: Hexagonal Architecture für Core Domains

**Status**: Akzeptiert

**Datum**: 2024-01-15

**Autor**: Architecture Team

---

## Kontext

EcoTrack besteht aus mehreren Modulen mit unterschiedlicher Komplexität:
- **Scoring** und **Challenge** sind Core Domains mit komplexer Business Logic
- **UserProfile** ist eine Supporting Domain mit CRUD-Operationen
- **Administration** ist eine Generic Domain (ACL zu Keycloak)

Wir müssen entscheiden, welche Architektur für die verschiedenen Module geeignet ist:
- Soll jedes Modul die gleiche Architektur verwenden?
- Wie stellen wir Testbarkeit sicher?
- Wie vermeiden wir Framework-Lock-in?
- Wie halten wir die Komplexität beherrschbar?

## Entscheidung

Wir verwenden **Hexagonal Architecture** (Ports & Adapters) für **Core Domains** (Scoring, Challenge).

Für Supporting/Generic Domains (UserProfile, Administration) verwenden wir einen **vereinfachten CRUD-Ansatz**.

### Struktur für Core Domains:

```
module-scoring/
├── domain/          # Framework-frei, Pure Java
│   ├── model/       # Entities, Value Objects
│   ├── event/       # Domain Events
│   └── port/
│       ├── in/      # Use Case Interfaces
│       └── out/     # Repository, Event Publisher Interfaces
├── application/     # Use Case Implementierungen
│   ├── service/     # Use Case Services
│   ├── dto/         # DTOs
│   └── mapper/      # Mapper
└── adapter/
    ├── in/
    │   └── rest/    # REST Controllers
    └── out/
        ├── persistence/  # JPA Repositories
        └── event/        # Event Publisher
```

### Domain Layer Rules:
- ❌ Keine Spring-Annotationen
- ❌ Keine JPA-Annotationen
- ❌ Keine Framework-Abhängigkeiten
- ✅ Pure Java
- ✅ Business Logic zentriert

## Begründung

### Testbarkeit
- Domain Layer ist 100% testbar ohne Spring
- Unit Tests laufen in Millisekunden
- Keine DB/Framework Setup nötig

### Framework-Unabhängigkeit
- Business Logic kann ohne Spring ausgeführt werden
- Framework-Wechsel möglich (theoretisch)
- Domain überlebt Framework-Upgrades

### Klare Grenzen
- Ports definieren Schnittstellen
- Adapter sind austauschbar
- Dependency Rule: Abhängigkeiten zeigen nach innen

### Team-Parallelisierung
- Domain kann unabhängig entwickelt werden
- Frontend/Backend Teams können parallel arbeiten
- Mockbare Interfaces für Development

## Konsequenzen

### Positiv

- ✅ Business Logic ist Framework-unabhängig
- ✅ Domain Tests laufen ohne Spring (schnell!)
- ✅ Klare Trennung: Business Logic vs. Infrastruktur
- ✅ Austauschbare Adapters (z.B. JPA → MongoDB)
- ✅ Bessere Wartbarkeit durch klare Struktur

### Negativ

- ❌ Mehr Boilerplate Code (Interfaces, Mapper, DTOs)
- ❌ Steile Lernkurve für Team
- ❌ Mehr Files/Packages (komplexere Navigation)
- ❌ Overhead für einfache CRUD-Operationen

### Risiken

- ⚠️ **Risiko**: Team überfordert mit Architektur  
  **Mitigation**: Training, Pair Programming, Code Reviews

- ⚠️ **Risiko**: Zu viel Boilerplate verlangsamt Entwicklung  
  **Mitigation**: Nur für Core Domains, nicht überall

### Technische Schuld

- 💰 Existierende Module müssen refactored werden
- 💰 MapStruct Mapper müssen erstellt werden
- 💰 Dokumentation muss geschrieben werden

## Alternativen

### Alternative A: Überall Hexagonal Architecture

**Beschreibung**: Alle Module verwenden Hexagonal Architecture, inklusive UserProfile und Administration.

**Pro**:
- Konsistente Architektur
- Überall gleiche Patterns

**Contra**:
- Overkill für CRUD-Module
- Unnötige Komplexität
- Langsamere Entwicklung für triviale Features

**Warum nicht gewählt**: Zu komplex für Supporting/Generic Domains. DDD empfiehlt unterschiedliche Architekturen je nach Domain-Typ.

### Alternative B: Überall CRUD (Spring Data REST)

**Beschreibung**: Alle Module als einfache CRUD-Services mit Spring Data REST.

**Pro**:
- Schnelle Entwicklung
- Wenig Boilerplate
- Einfach zu lernen

**Contra**:
- Business Logic vermischt mit Infrastruktur
- Schwer testbar (benötigt Spring Context)
- Framework-Lock-in
- Komplexe Business Logic wird unübersichtlich

**Warum nicht gewählt**: Scoring und Challenge haben komplexe Business Logic, die Framework-unabhängig bleiben muss.

### Alternative C: Layered Architecture

**Beschreibung**: Klassische 3-Layer Architecture (Controller → Service → Repository).

**Pro**:
- Team kennt es bereits
- Weniger Boilerplate als Hexagonal
- Standard in Spring Boot

**Contra**:
- Keine klare Trennung Domain/Infrastruktur
- Schwieriger zu testen (braucht oft Spring Context)
- Business Logic oft in Service-Layer vermischt mit Technical Concerns

**Warum nicht gewählt**: Nicht Framework-unabhängig genug für Core Domains.

## Betroffene Module/Komponenten

- `module-scoring` (Core Domain) → Hexagonal
- `module-challenge` (Core Domain) → Hexagonal
- `module-userprofile` (Supporting) → CRUD
- `module-administration` (Generic) → ACL Pattern

## Metriken/Erfolgs-Kriterien

- Unit Test Execution Time: < 5 Sekunden für Domain Tests
- Code Coverage: > 80% für Domain Layer
- Cyclomatic Complexity: < 10 für Domain Classes
- Team Onboarding: Neue Entwickler können innerhalb 2 Wochen Code contributen

## Weitere Referenzen

- [Hexagonal Architecture (Alistair Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture (Robert C. Martin)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [DDD Domain Types](https://learn.microsoft.com/en-us/archive/msdn-magazine/2009/february/best-practice-an-introduction-to-domain-driven-design)
- Internal: `docs/architecture/hexagonal-architecture.md`

---

**Change Log:**
- 2024-01-15: Initial ADR created
- 2024-02-20: Added metrics section
