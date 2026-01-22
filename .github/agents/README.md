# EcoTrack AI Agents - Übersicht

Diese Dokumentation beschreibt alle verfügbaren AI Agents für das EcoTrack-Projekt. Agents sind spezialisierte KI-Assistenten, die für bestimmte Aufgabenbereiche optimiert sind.

## 🎯 Schnellübersicht

| Agent | Zuständigkeit | Wann aktivieren? |
|-------|--------------|------------------|
| [Requirements Engineer](#requirements-engineer) | User Stories, Anforderungen | Neue Features planen |
| [Software Architect](#software-architect) | Architektur, ADRs, APIs | Technische Designs |
| [Backend Developer](#backend-developer) | Java/Spring Boot Code | Backend implementieren |
| [Frontend Developer](#frontend-developer-legacy) | React/RN Übersicht | Allgemeine Frontend-Fragen |
| [CDD UI Components](#cdd-ui-components-developer) | Button, Card, Input | UI-Bausteine erstellen |
| [CDD Feature Components](#cdd-feature-components-developer) | Listen, Formulare | Features mit Business-Logik |
| [CDD Page/Screen](#cdd-pagescreen-developer) | Seiten, Navigation | Routing & Layouts |
| [CDD Hooks](#cdd-hooks-developer) | Custom Hooks, Queries | Wiederverwendbare Logik |
| [Test Engineer](#test-engineer) | Unit/Integration Tests | Qualitätssicherung |

---

## 📋 Detaillierte Agent-Beschreibungen

### Requirements Engineer
**Datei:** [requirements-engineer.agent.md](requirements-engineer.agent.md)

#### Was macht dieser Agent?
Analysiert und dokumentiert Anforderungen, erstellt User Stories im standardisierten Format und definiert Akzeptanzkriterien.

#### Wann aktivieren?
- ✅ Neue Features spezifizieren
- ✅ User Stories erstellen
- ✅ Akzeptanzkriterien definieren
- ✅ Requirements verfeinern

#### Handoff
➡️ Übergibt an: **Software Architect**

---

### Software Architect
**Datei:** [software-architect.agent.md](software-architect.agent.md)

#### Was macht dieser Agent?
Entwirft technische Architekturen, erstellt ADRs (Architecture Decision Records), definiert API-Schnittstellen und sorgt für konsistente Modularisierung.

#### Wann aktivieren?
- ✅ Technische Architektur entwerfen
- ✅ API-Contracts definieren
- ✅ Modul-Schnittstellen planen
- ✅ Architekturentscheidungen dokumentieren (ADRs)

#### Handoff
➡️ Übergibt an: **Backend Developer** oder **CDD Feature Components Developer**

---

### Backend Developer
**Datei:** [backend-developer.agent.md](backend-developer.agent.md)

#### Was macht dieser Agent?
Implementiert Java/Spring Boot Code nach DDD und Hexagonal Architecture. Erstellt Use Cases, Repositories, REST-Controller und Domain Events.

#### Wann aktivieren?
- ✅ Java-Klassen implementieren
- ✅ REST APIs erstellen
- ✅ Use Case Services schreiben
- ✅ Domain Events implementieren

#### Handoff
➡️ Übergibt an: **Test Engineer**

---

### Frontend Developer (Legacy)
**Datei:** [frontend-developer.agent.md](frontend-developer.agent.md)

#### Was macht dieser Agent?
Allgemeiner Frontend-Agent für Übersichtsfragen. **Für konkrete Implementierungen die spezialisierten CDD-Agents verwenden!**

#### Wann aktivieren?
- ✅ Allgemeine Frontend-Architekturfragen
- ✅ Übersicht über Komponenten-Struktur

#### Empfehlung
🔄 Für konkrete Aufgaben → CDD-Agents verwenden

---

## 🧩 Component-Driven Development (CDD) Agents

Die CDD-Agents sind spezialisiert auf verschiedene Ebenen der Komponenten-Hierarchie:

```
┌──────────────────────────────────────────────────────────────┐
│                     Page/Screen (Routing)                     │
│                    ↳ cdd-page-screen.agent.md                 │
├──────────────────────────────────────────────────────────────┤
│                 Feature Components (Business Logic)           │
│                ↳ cdd-feature-components.agent.md              │
├──────────────────────────────────────────────────────────────┤
│              UI Components (Presentational, Reusable)         │
│                  ↳ cdd-ui-components.agent.md                 │
├──────────────────────────────────────────────────────────────┤
│                    Custom Hooks (Logic Layer)                 │
│                      ↳ cdd-hooks.agent.md                     │
└──────────────────────────────────────────────────────────────┘
```

### CDD UI Components Developer
**Datei:** [cdd-ui-components.agent.md](cdd-ui-components.agent.md)

#### Was macht dieser Agent?
Erstellt wiederverwendbare, atomare UI-Bausteine wie Buttons, Inputs, Cards und Badges. Diese Komponenten haben **keine Business-Logik** und sind rein präsentational.

#### Arbeitsbereich
```
_admin-web/src/components/ui/
_admin-web/src/components/common/
_mobile/src/components/ui/
_mobile/src/components/common/
```

#### Wann aktivieren?
- ✅ "Erstelle einen Button mit verschiedenen Variants"
- ✅ "Ich brauche eine Card-Komponente"
- ✅ "Die Input-Komponente braucht Error-States"
- ✅ "Badge für Punkte-Anzeige erstellen"

#### Technologien
- Tailwind CSS + cva (class-variance-authority)
- TypeScript strict mode
- forwardRef für DOM-Zugriff
- Accessibility (ARIA)

---

### CDD Feature Components Developer
**Datei:** [cdd-feature-components.agent.md](cdd-feature-components.agent.md)

#### Was macht dieser Agent?
Erstellt Feature-Komponenten, die **Business-Logik und Datenabruf** enthalten. Diese Komponenten nutzen TanStack Query Hooks und kombinieren UI-Komponenten zu funktionalen Features.

#### Arbeitsbereich
```
_admin-web/src/components/features/
_mobile/src/components/features/
```

#### Wann aktivieren?
- ✅ "Erstelle eine Aktivitätsliste"
- ✅ "Formular zum Loggen von Aktivitäten"
- ✅ "Challenge-Übersicht mit API-Anbindung"
- ✅ "Leaderboard-Tabelle implementieren"

#### Unterschied zu UI Components
| Aspekt | UI Components | Feature Components |
|--------|---------------|-------------------|
| Business-Logik | ❌ Keine | ✅ Enthält |
| Datenabruf | ❌ Nein | ✅ TanStack Query |
| States | Props only | Loading, Error, Empty |

---

### CDD Page/Screen Developer
**Datei:** [cdd-page-screen.agent.md](cdd-page-screen.agent.md)

#### Was macht dieser Agent?
Erstellt die oberste Ebene der UI – **Pages** (Admin-Web) und **Screens** (Mobile). Verantwortlich für Routing, Navigation und Layout-Orchestrierung.

#### Arbeitsbereich
```
_admin-web/src/pages/
_admin-web/src/routes/
_mobile/src/screens/
_mobile/src/navigation/
```

#### Wann aktivieren?
- ✅ "Erstelle die Dashboard-Seite"
- ✅ "Neuer Screen für Challenges"
- ✅ "Routing für das Feature einrichten"
- ✅ "Layout für Admin-Bereich"

#### Technologien
- **Admin-Web:** React Router v6, Lazy Loading
- **Mobile:** React Navigation v6, Stack/Tab Navigator

---

### CDD Hooks Developer
**Datei:** [cdd-hooks.agent.md](cdd-hooks.agent.md)

#### Was macht dieser Agent?
Erstellt **Custom React Hooks** – wiederverwendbare Logik für Datenabruf, Formulare und Utilities. Kapselt komplexe Logik außerhalb von Komponenten.

#### Arbeitsbereich
```
_admin-web/src/hooks/
_mobile/src/hooks/
```

#### Wann aktivieren?
- ✅ "Erstelle einen Hook für Aktivitäten-Abfrage"
- ✅ "useAuth Hook implementieren"
- ✅ "API-Calls in Hooks kapseln"
- ✅ "useDebounce Utility Hook"

#### Hook-Typen
| Typ | Beispiele |
|-----|-----------|
| Query Hooks | useActivities, useUser |
| Mutation Hooks | useCreateActivity |
| Form Hooks | useActivityForm |
| Utility Hooks | useDebounce, useLocalStorage |

---

### Test Engineer
**Datei:** [test-engineer.agent.md](test-engineer.agent.md)

#### Was macht dieser Agent?
Implementiert Tests nach der Test-Pyramide (70% Unit, 20% Integration, 10% E2E). Verifiziert Akzeptanzkriterien und sorgt für Code-Qualität.

#### Wann aktivieren?
- ✅ Unit Tests schreiben
- ✅ Integration Tests erstellen
- ✅ Test Coverage prüfen
- ✅ Akzeptanzkriterien verifizieren

#### Technologien
- **Backend:** JUnit 5, Mockito, Spring Boot Test
- **Frontend:** Vitest, React Testing Library, MSW

---

## 🔄 Typischer Workflow

```
1️⃣ Requirements Engineer
   → User Story erstellen
   
2️⃣ Software Architect  
   → API Contract & Architektur
   
3️⃣ Backend Developer
   → Java Implementation
   
4️⃣ CDD Hooks Developer
   → TanStack Query Hooks
   
5️⃣ CDD UI Components Developer
   → Benötigte UI-Bausteine
   
6️⃣ CDD Feature Components Developer
   → Feature mit Business-Logik
   
7️⃣ CDD Page/Screen Developer
   → Integration in Page/Screen
   
8️⃣ Test Engineer
   → Tests für alle Ebenen
```

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
