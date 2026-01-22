# EcoTrack Context Documentation

Dieses Dokument bietet AI Agents einen schnellen Überblick über alle verfügbaren Informationsquellen im EcoTrack-Projekt.

---

## 📋 Inhaltsverzeichnis

1. [Projekt-Übersicht](#projekt-übersicht)
2. [Architektur & Design](#architektur--design)
3. [Coding Standards](#coding-standards)
4. [API & Schnittstellen](#api--schnittstellen)
5. [Testing](#testing)
6. [Deployment & DevOps](#deployment--devops)
7. [Externe Ressourcen](#externe-ressourcen)
8. [Wann welche Quelle nutzen?](#wann-welche-quelle-nutzen)

---

## Projekt-Übersicht

### README.md
**Pfad**: `/README.md`  
**Inhalt**: 
- Projekt-Vision & Ziele
- High-Level Architektur
- Modul-Übersicht
- Getting Started
- Technologie-Stack

**Wann nutzen**: 
- Für allgemeinen Projekt-Überblick
- Bei Onboarding neuer Entwickler
- Für Setup-Anweisungen

### CHANGELOG.md
**Pfad**: `/CHANGELOG.md`  
**Inhalt**:
- Release Notes
- Breaking Changes
- Feature-Historie

**Wann nutzen**:
- Für Versions-History
- Bei Migrations-Fragen
- Für Breaking Changes

---

## Architektur & Design

### Coding Instructions
**Pfad**: `.github/copilot-instructions.md`  
**Inhalt**:
- Globale Coding-Standards
- Naming Conventions
- Git Workflow
- Testing-Prinzipien

**Wann nutzen**:
- Als Referenz für alle Code-Erstellungen
- Bei Code Review
- Für konsistenten Code-Stil

### Modul-spezifische Architecture
**Pfad**: `server/README.md`  
**Inhalt**:
- Backend-Architektur
- Modul-Struktur (Bounded Contexts)
- Hexagonal Architecture Details
- DDD-Patterns

**Wann nutzen**:
- Bei Backend-Implementierungen
- Für Architektur-Entscheidungen
- Bei Modul-Grenzen-Fragen

### ADRs (Architecture Decision Records)
**Pfad**: `.github/templates/architecture-decision-record.md` (Template)  
**Location**: `docs/architecture/decisions/` (wenn vorhanden)  
**Inhalt**:
- Architekturentscheidungen
- Rationale & Trade-offs
- Konsequenzen

**Wann nutzen**:
- Vor wichtigen Architektur-Änderungen
- Für Verständnis bestehender Entscheidungen
- Als Template für neue ADRs

---

## Coding Standards

### Language-Specific Instructions

#### Java/Spring Boot
**Pfad**: `.github/instructions/java-backend.instructions.md`  
**Inhalt**:
- Spring Boot Best Practices
- Hexagonal Architecture Details
- JPA/Hibernate Standards
- Domain Events
- MapStruct Mapping

**Wann nutzen**:
- Bei Java/Backend-Implementierungen
- Für Use Case Services
- Für Domain Models

#### TypeScript/React (Admin-Web)
**Pfad**: `.github/instructions/typescript-react.instructions.md`  
**Inhalt**:
- TypeScript Best Practices
- React Patterns (Hooks, State Management)
- TanStack Query
- Forms & Validation (Zod)
- Tailwind CSS

**Wann nutzen**:
- Bei Admin-Web Komponenten
- Für React State Management
- Für API Integration

#### React Native (Mobile)
**Pfad**: `.github/instructions/react-native.instructions.md`  
**Inhalt**:
- React Native Best Practices
- Platform-spezifischer Code
- Performance Optimierungen
- AsyncStorage
- Navigation

**Wann nutzen**:
- Bei Mobile App Features
- Für Platform-spezifische Implementierungen
- Für Performance-Optimierungen

#### Testing
**Pfad**: `.github/instructions/testing.instructions.md`  
**Inhalt**:
- AAA Pattern
- JUnit 5 (Java)
- Vitest (TypeScript)
- Test Coverage Ziele
- Test Data Factories

**Wann nutzen**:
- Beim Schreiben von Tests
- Für Test-Qualität
- Bei Coverage-Fragen

---

## Custom Agents

### Agent Definitions
**Pfad**: `.github/agents/*.agent.md`

#### Requirements Engineer
**Pfad**: `.github/agents/requirements-engineer.agent.md`  
**Wann aktivieren**:
- Für User Story Erstellung
- Bei Requirements-Analyse
- Für Akzeptanzkriterien
- **Handoff zu**: Architect

#### Software Architect
**Pfad**: `.github/agents/software-architect.agent.md`  
**Wann aktivieren**:
- Für Architektur-Design
- Bei ADR-Erstellung
- Für Modul-Schnittstellen
- **Handoff zu**: Backend/Frontend Developer

#### Backend Developer
**Pfad**: `.github/agents/backend-developer.agent.md`  
**Wann aktivieren**:
- Für Java/Spring Boot Implementierungen
- Bei Use Case Services
- Für REST APIs
- **Handoff zu**: Tester

#### Frontend Developer (Legacy)
**Pfad**: `.github/agents/frontend-developer.agent.md`  
**Wann aktivieren**:
- Für allgemeine Frontend-Architekturfragen
- **Empfehlung**: Für konkrete Aufgaben die CDD-Agents verwenden

### CDD (Component-Driven Development) Agents

Die CDD-Agents sind spezialisiert auf verschiedene Ebenen der Komponenten-Hierarchie.
**Übersicht**: `.github/agents/README.md`

#### CDD UI Components Developer
**Pfad**: `.github/agents/cdd-ui-components.agent.md`  
**Wann aktivieren**:
- Für Button, Card, Input, Badge erstellen
- Bei UI-Kit Erweiterungen
- Für atomare, wiederverwendbare Komponenten
- **Handoff zu**: Feature Components Developer

#### CDD Feature Components Developer
**Pfad**: `.github/agents/cdd-feature-components.agent.md`  
**Wann aktivieren**:
- Für Listen mit Datenanbindung (ActivityList)
- Bei Formularen (CreateActivityForm)
- Für Komponenten mit Business-Logik
- **Handoff zu**: Page/Screen Developer

#### CDD Page/Screen Developer
**Pfad**: `.github/agents/cdd-page-screen.agent.md`  
**Wann aktivieren**:
- Für neue Seiten (Admin-Web)
- Bei neuen Screens (Mobile)
- Für Routing & Navigation
- **Handoff zu**: Tester

#### CDD Hooks Developer
**Pfad**: `.github/agents/cdd-hooks.agent.md`  
**Wann aktivieren**:
- Für TanStack Query Hooks
- Bei Custom Hooks (useDebounce, useAuth)
- Für wiederverwendbare Logik
- **Handoff zu**: Feature Components Developer

#### Test Engineer
**Pfad**: `.github/agents/test-engineer.agent.md`  
**Wann aktivieren**:
- Für Unit/Integration Tests
- Bei Test Coverage
- Für Akzeptanzkriterien-Verifikation

---

## Templates

### User Story Template
**Pfad**: `.github/templates/user-story.md`  
**Wann nutzen**:
- Bei neuen Features
- Für Requirements Documentation
- Mit Requirements Engineer Agent

### ADR Template
**Pfad**: `.github/templates/architecture-decision-record.md`  
**Wann nutzen**:
- Bei wichtigen Architektur-Entscheidungen
- Framework/Library-Wahl
- Performance/Security Trade-offs
- Mit Software Architect Agent

### Test Plan Template
**Pfad**: `.github/templates/test-plan.md`  
**Wann nutzen**:
- Für strukturierte Test-Planung
- Bei komplexen Features
- Für Test Coverage Tracking
- Mit Test Engineer Agent

### Code Review Checklist
**Pfad**: `.github/templates/code-review-checklist.md`  
**Wann nutzen**:
- Bei Pull Requests
- Für systematisches Review
- Code-Qualität sicherstellen

---

## Custom Prompts

### Generate Tests
**Pfad**: `.github/prompts/generate-tests.prompt.md`  
**Variablen**: `file`, `coverage`  
**Wann nutzen**:
- Automatische Test-Generierung
- Für Unit Tests
- AAA Pattern

**Aufruf**:
```
Generiere Tests für `path/to/MyService.java` mit 85% Coverage
```

### Code Review
**Pfad**: `.github/prompts/code-review.prompt.md`  
**Variablen**: `file`, `focus`  
**Wann nutzen**:
- Systematisches Code Review
- Vor Merge
- Fokus auf: architecture, security, performance, testing

**Aufruf**:
```
Review `src/service/LogActivityService.java` mit Fokus auf architecture
```

### Generate Documentation
**Pfad**: `.github/prompts/generate-docs.prompt.md`  
**Variablen**: `selection`  
**Wann nutzen**:
- JavaDoc/JSDoc erstellen
- Public APIs dokumentieren
- Komplexe Methoden erklären

**Aufruf**:
```
Dokumentiere diese Klasse: [Code selektieren]
```

### Refactor Code
**Pfad**: `.github/prompts/refactor-code.prompt.md`  
**Variablen**: `selection`, `focus`  
**Wann nutzen**:
- Code-Qualität verbessern
- Lesbarkeit erhöhen
- Performance optimieren

**Aufruf**:
```
Refaktoriere diesen Code mit Fokus auf readability: [Code selektieren]
```

---

## API & Schnittstellen

### REST API Documentation
**Location**: `server/src/main/java/.../adapter/in/rest/`  
**Inhalt**:
- REST Controller
- Request/Response DTOs
- API Endpoints

**Wann nutzen**:
- Für API-Integration (Frontend)
- Bei Backend-API-Implementierung
- Für Request/Response Contracts

### Domain Events
**Location**: `server/src/main/java/.../domain/event/`  
**Inhalt**:
- Domain Event Definitionen
- Inter-Modul-Kommunikation

**Wann nutzen**:
- Für Modul-Kommunikation
- Bei Event-Driven Architecture
- Für Async-Prozesse

---

## Testing

### Test Factories
**Location**: `server/src/test/java/.../factory/` (wenn vorhanden)  
**Inhalt**:
- Test Data Builders
- Mock Objects

**Wann nutzen**:
- Für wiederverwendbare Testdaten
- Bei Unit/Integration Tests
- Für konsistente Mocks

---

## Deployment & DevOps

### Docker Compose
**Pfad**: `docker-compose.yml`, `docker-compose.dev.yml`  
**Inhalt**:
- Service-Definitionen
- Postgres, Keycloak
- Entwicklungs-Umgebung

**Wann nutzen**:
- Für lokales Setup
- Bei Docker-Problemen
- Für Service-Dependencies

### GitHub Workflows
**Pfad**: `.github/workflows/`  
**Inhalt**:
- CI/CD Pipelines
- Test Automation
- Build & Deploy

**Wann nutzen**:
- Für CI/CD-Konfiguration
- Bei Build-Problemen
- Für Automation

---

## Externe Ressourcen

### Spring Boot
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

**Wann nutzen**:
- Spring Boot Features
- JPA Queries
- Configuration

### React & TypeScript
- [React Docs](https://react.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/handbook/intro.html)
- [TanStack Query](https://tanstack.com/query/latest/docs/framework/react/overview)

**Wann nutzen**:
- React Patterns
- TypeScript Types
- Query Hooks

### React Native
- [React Native Docs](https://reactnative.dev/docs/getting-started)
- [React Navigation](https://reactnavigation.org/docs/getting-started)

**Wann nutzen**:
- Mobile Features
- Navigation
- Platform APIs

### Testing
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Vitest](https://vitest.dev/)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)

**Wann nutzen**:
- Testing Features
- Mocking
- Assertions

### DDD & Clean Architecture
- [Domain-Driven Design (Evans)](https://www.domainlanguage.com/ddd/)
- [Hexagonal Architecture (Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture (Martin)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

**Wann nutzen**:
- Architektur-Patterns
- DDD-Verständnis
- Bounded Contexts

---

## Wann welche Quelle nutzen?

### User Story erstellen
1. **Requirements Engineer Agent** aktivieren
2. **User Story Template** (`.github/templates/user-story.md`)
3. **Coding Instructions** für Standards

### Backend Feature implementieren
1. **Software Architect Agent** für Design
2. **Backend Developer Agent** für Implementierung
3. **Java Instructions** (`.github/instructions/java-backend.instructions.md`)
4. **Server README** (`server/README.md`) für Architektur
5. **Generate Tests Prompt** für Unit Tests

### Frontend Component erstellen
1. **Frontend Developer Agent** aktivieren
2. **TypeScript/React Instructions** (`.github/instructions/typescript-react.instructions.md`)
3. **API Documentation** aus Backend
4. **Generate Tests Prompt** für Component Tests

### Code Review durchführen
1. **Code Review Prompt** (`.github/prompts/code-review.prompt.md`)
2. **Code Review Checklist** (`.github/templates/code-review-checklist.md`)
3. **Coding Instructions** als Referenz

### Test Coverage erhöhen
1. **Test Engineer Agent** aktivieren
2. **Generate Tests Prompt**
3. **Testing Instructions** (`.github/instructions/testing.instructions.md`)

### Architekturentscheidung treffen
1. **Software Architect Agent** aktivieren
2. **ADR Template** (`.github/templates/architecture-decision-record.md`)
3. **Bestehende ADRs** lesen
4. **DDD/Clean Architecture** Ressourcen

### Code Refactoring
1. **Refactor Code Prompt** (`.github/prompts/refactor-code.prompt.md`)
2. **Coding Instructions** für Standards
3. **Language-Specific Instructions**

---

## Quick Reference Card

| Task | Agent | Template/Prompt | Instructions |
|------|-------|-----------------|--------------|
| User Story | Requirements Engineer | user-story.md | - |
| Architecture Design | Software Architect | architecture-decision-record.md | - |
| Backend Feature | Backend Developer | - | java-backend.instructions.md |
| Frontend Component | Frontend Developer | - | typescript-react.instructions.md / react-native.instructions.md |
| Testing | Test Engineer | test-plan.md | testing.instructions.md |
| Code Review | - | code-review.prompt.md | copilot-instructions.md |
| Generate Tests | - | generate-tests.prompt.md | testing.instructions.md |
| Documentation | - | generate-docs.prompt.md | - |
| Refactoring | - | refactor-code.prompt.md | - |

---

**Hinweis**: Diese Datei ist ein Living Document. Bei Änderungen an der Projektstruktur oder neuen Ressourcen bitte aktualisieren.

**Letzte Aktualisierung**: 2024-01-15
