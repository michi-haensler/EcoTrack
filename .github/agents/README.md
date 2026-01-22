# EcoTrack AI Agents

Spezialisierte KI-Assistenten für das EcoTrack-Projekt, optimiert für verschiedene Aufgabenbereiche im Entwicklungsprozess.

---

## 🔄 Agent-Workflow

Der folgende Workflow zeigt, wie die Agents zusammenarbeiten:

```
┌─────────────────────────────────────────────────────────────┐
│                    Requirements Engineer                     │
│              (User Stories & Akzeptanzkriterien)             │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     Software Architect                       │
│               (Architektur & API Definition)                 │
└───────────┬─────────────────────────────────┬───────────────┘
            │                                 │
            ▼                                 ▼
┌───────────────────────┐    ┌────────────────────────────────┐
│   Backend Developer   │    │      Frontend (CDD-Agents)     │
│   (Java/Spring Boot)  │    │  ┌────────────────────────────┐│
└───────────┬───────────┘    │  │ UI Component Developer     ││
            │                │  │ (Buttons, Cards, Inputs)   ││
            │                │  └─────────────┬──────────────┘│
            │                │                │               │
            │                │  ┌─────────────▼──────────────┐│
            │                │  │Feature Component Developer ││
            │                │  │(Listen, Forms, Data)       ││
            │                │  └─────────────┬──────────────┘│
            │                │                │               │
            │                │  ┌─────────────▼──────────────┐│
            │                │  │    Mobile Developer        ││
            │                │  │(Screens, Navigation)       ││
            │                │  └────────────────────────────┘│
            │                └────────────────────────────────┘
            │                                 │
            └────────────────┬────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                       Test Engineer                          │
│              (Unit, Integration & E2E Tests)                 │
└─────────────────────────────────────────────────────────────┘
```

---

## 📋 Agents im Detail

### 1️⃣ Requirements Engineer
**Datei:** [requirements-engineer.agent.md](requirements-engineer.agent.md)

| Aufgabe | Beschreibung |
|---------|--------------|
| User Stories | Erstellt und verfeinert User Stories |
| Akzeptanzkriterien | Definiert messbare Erfolgskriterien |
| Requirements-Analyse | Analysiert und dokumentiert Anforderungen |

**Aktivieren bei:** Neue Features spezifizieren, Anforderungen klären

**Handoff:** ➡️ Software Architect

---

### 2️⃣ Software Architect
**Datei:** [software-architect.agent.md](software-architect.agent.md)

| Aufgabe | Beschreibung |
|---------|--------------|
| Architektur | Entwirft technische Strukturen |
| ADRs | Dokumentiert Architekturentscheidungen |
| API-Contracts | Definiert Schnittstellen (OpenAPI) |
| Modularisierung | Plant Modul-Grenzen und Kommunikation |

**Aktivieren bei:** Technische Designs, API-Definition, Architektur-Fragen

**Handoff:** ➡️ Backend Developer ODER Frontend (CDD-Agents)

---

### 3️⃣ Backend Developer
**Datei:** [backend-developer.agent.md](backend-developer.agent.md)

| Aufgabe | Beschreibung |
|---------|--------------|
| Java/Spring Boot | Implementiert Backend-Code |
| DDD & Hexagonal | Folgt Domain-Driven Design |
| REST APIs | Erstellt Controller und Endpoints |
| Domain Events | Implementiert Event-basierte Kommunikation |

**Bounded Contexts:**
| Modul | Domain-Typ | Architektur |
|-------|-----------|-------------|
| `module-scoring` | Core Domain | Hexagonal (ActivityEntry, PointsLedger) |
| `module-challenge` | Core Domain | Hexagonal (Challenge, ChallengeGoal) |
| `module-userprofile` | Supporting | CRUD (EcoUser, Name) |
| `module-administration` | Generic | ACL/Keycloak |

**Arbeitsbereich:**
```
_server/module-*/src/main/java/
```

**Aktivieren bei:** Java-Klassen, REST APIs, Use Cases, Repositories

**Handoff:** ➡️ Test Engineer

---

### 4️⃣ Frontend: CDD-Agents (Component-Driven Development)

Die Frontend-Entwicklung folgt dem CDD-Ansatz mit drei spezialisierten Agents:

```
┌────────────────────────────────────────────────────────────┐
│  UI Component Developer                                     │
│  ↳ Atomare, wiederverwendbare UI-Bausteine                 │
│  ↳ _admin-web/src/components/ui/                           │
│  ↳ _mobile/src/components/ui/                              │
├────────────────────────────────────────────────────────────┤
│                            ▼                               │
├────────────────────────────────────────────────────────────┤
│  Feature Component Developer                                │
│  ↳ Business-Logik, Datenabruf, TanStack Query              │
│  ↳ _admin-web/src/components/features/                     │
│  ↳ _mobile/src/components/features/                        │
├────────────────────────────────────────────────────────────┤
│                            ▼                               │
├────────────────────────────────────────────────────────────┤
│  Mobile Developer                                           │
│  ↳ Screens, Navigation, Layout-Orchestrierung              │
│  ↳ _admin-web/src/pages/  |  _mobile/src/screens/          │
└────────────────────────────────────────────────────────────┘
```

#### 4a) UI Component Developer
**Datei:** [cdd-ui-components.agent.md](cdd-ui-components.agent.md)

| Aspekt | Details |
|--------|---------|
| Komponenten | Button, Input, Card, Badge, PointsDisplay |
| Logik | ❌ Keine Business-Logik |
| Styling | Tailwind CSS + cva (class-variance-authority) |
| Features | forwardRef, Accessibility (ARIA), Variants |

**EcoTrack-Beispiele:** PointsBadge, LevelIndicator, TreeIcon, ActivityIcon

**Aktivieren bei:** "Erstelle einen Button", "Card-Komponente", "Badge für Punkte-Anzeige"

---

#### 4b) Feature Component Developer
**Datei:** [cdd-feature-components.agent.md](cdd-feature-components.agent.md)

| Aspekt | Details |
|--------|---------|
| Komponenten | ActivityList, ChallengeCard, Leaderboard |
| Logik | ✅ Business-Logik + Datenabruf |
| Data Fetching | TanStack Query Hooks |
| States | Loading, Error, Empty, Success |

**EcoTrack-Beispiele:** ActivityEntryList, ChallengeOverview, RankingTable, ProgressTree

**Aktivieren bei:** "Aktivitätsliste erstellen", "Challenge-Übersicht", "Leaderboard implementieren"

---

#### 4c) Mobile Developer
**Datei:** [mobile-developer.agent.md](mobile-developer.agent.md)

| Aspekt | Details |
|--------|---------|
| Mobile | React Navigation v6, Stack/Tab Navigator |
| Admin-Web | React Router v6, Lazy Loading |
| Aufgabe | Screens, Navigation, Layout-Orchestrierung |

**EcoTrack-Beispiele:** HomeScreen, ChallengeScreen, LeaderboardScreen, ProfileScreen

**Aktivieren bei:** "Screen erstellen", "Dashboard-Seite erstellen", "Navigation einrichten"

---

### 5️⃣ Test Engineer
**Datei:** [test-engineer.agent.md](test-engineer.agent.md)

| Aufgabe | Beschreibung |
|---------|--------------|
| Unit Tests | 70% - Isolierte Komponenten/Funktionen |
| Integration Tests | 20% - Zusammenspiel von Modulen |
| E2E Tests | 10% - Komplette User Flows |

**Technologien:**
| Bereich | Tools |
|---------|-------|
| Backend | JUnit 5, Mockito, Spring Boot Test |
| Frontend | Vitest, React Testing Library, MSW |

**Aktivieren bei:** Tests schreiben, Coverage prüfen, Akzeptanzkriterien verifizieren

---

## 🔧 Zusätzliche Agents (Querschnitt)

Diese Agents sind nicht Teil des Hauptflusses, können aber bei Bedarf aktiviert werden:

### Hooks Developer
**Datei:** [cdd-hooks.agent.md](cdd-hooks.agent.md)

| Hook-Typ | EcoTrack-Beispiele |
|----------|-----------|
| Query Hooks | `useActivities`, `useChallenges`, `useLeaderboard` |
| Mutation Hooks | `useLogActivity`, `useJoinChallenge` |
| Form Hooks | `useActivityForm`, `useLoginForm` |
| Utility Hooks | `useEcoUser`, `usePoints`, `useLevel` |

**Arbeitsbereich:**
```
_admin-web/src/hooks/
_mobile/src/hooks/
```

**Aktivieren bei:** "Hook für API-Abfrage", "useAuth implementieren", "Custom Hook erstellen"

**Handoff:** ➡️ Feature Component Developer oder Test Engineer

---

## 🎯 Schnellreferenz

| Agent | Datei | Hauptaufgabe |
|-------|-------|--------------|
| Requirements Engineer | [requirements-engineer.agent.md](requirements-engineer.agent.md) | User Stories & Anforderungen |
| Software Architect | [software-architect.agent.md](software-architect.agent.md) | Architektur & APIs |
| Backend Developer | [backend-developer.agent.md](backend-developer.agent.md) | Java/Spring Boot |
| UI Component Developer | [cdd-ui-components.agent.md](cdd-ui-components.agent.md) | Buttons, Cards, Inputs |
| Feature Component Developer | [cdd-feature-components.agent.md](cdd-feature-components.agent.md) | Listen, Forms, Data |
| Mobile Developer | [mobile-developer.agent.md](mobile-developer.agent.md) | Screens, Navigation |
| Test Engineer | [test-engineer.agent.md](test-engineer.agent.md) | Unit/Integration/E2E Tests |
| Hooks Developer | [cdd-hooks.agent.md](cdd-hooks.agent.md) | Custom React Hooks (Querschnitt) |

---

## 📝 Verwendung in VS Code

Agents können in GitHub Copilot Chat aktiviert werden:

```
@workspace Aktiviere den CDD UI Components Developer und erstelle eine Badge-Komponente
```

Oder durch Referenzierung der Agent-Datei:

```
Lies .github/agents/cdd-feature-components.agent.md und implementiere eine ActivityList
```

---

## 📚 Weiterführende Dokumentation

- [Coding Standards](../copilot-instructions.md)
- [TypeScript/React Instructions](../instructions/typescript-react.instructions.md)
- [Testing Instructions](../instructions/testing.instructions.md)
- [Java Backend Instructions](../instructions/java-backend.instructions.md)
