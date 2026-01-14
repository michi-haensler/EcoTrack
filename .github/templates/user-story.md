# User Story: [Titel]

## Beschreibung

**Als** [Rolle: Student/Teacher/Admin]  
**möchte ich** [Funktion/Feature]  
**damit** [Nutzen/Geschäftswert]

## Akzeptanzkriterien

Verwende GIVEN-WHEN-THEN Format:

- [ ] **GIVEN** [Vorbedingung/Kontext]  
      **WHEN** [Aktion/Event]  
      **THEN** [Erwartetes Ergebnis]  
      **AND** [Zusätzliche Erwartung]

- [ ] **GIVEN** [Weitere Vorbedingung]  
      **WHEN** [Weitere Aktion]  
      **THEN** [Weiteres Ergebnis]

- [ ] **GIVEN** [Negativ-Szenario]  
      **WHEN** [Fehlerhafte Aktion]  
      **THEN** [Fehlerbehandlung]

## Bounded Context

Welcher Modul/Bounded Context ist betroffen?

- [ ] Scoring (Activity Logging, Points, Levels)
- [ ] Challenge (Challenges, Competitions)
- [ ] UserProfile (User Management, Profiles)
- [ ] Administration (School/Class Management, Keycloak)

## Plattform

Welche Plattformen sind betroffen?

- [ ] Mobile (React Native)
- [ ] Admin-Web (React + Vite)
- [ ] Backend (Spring Boot)

## Story Points

**Schätzung**: [1, 2, 3, 5, 8, 13]

| Points | Aufwand | Beispiel |
|--------|---------|----------|
| 1 | Trivial | Textänderung, Config-Update |
| 2 | Einfach | Neues Feld, einfache Validierung |
| 3 | Klein | CRUD-Operation, einfacher Endpoint |
| 5 | Mittel | Feature mit mehreren Komponenten |
| 8 | Groß | Komplexe Business Logic, Integration |
| 13 | Sehr groß | Neuer Bounded Context, Major Feature |

**Begründung**: [Warum diese Schätzung?]

## Abhängigkeiten

- [ ] Abhängig von: #[Issue-Nummer] - [Kurzbeschreibung]
- [ ] Blockiert: #[Issue-Nummer] - [Kurzbeschreibung]
- [ ] Related: #[Issue-Nummer] - [Kurzbeschreibung]

## Technische Notizen

### Backend
- **Domain Layer**: [Entities, Value Objects, die benötigt werden]
- **Use Cases**: [Welche Use Cases implementieren?]
- **REST API**: [Endpoints, die erstellt/geändert werden]
- **Events**: [Domain Events, die gepublished werden]

### Frontend
- **Components**: [Welche UI-Komponenten?]
- **State Management**: [TanStack Query Hooks]
- **Forms**: [React Hook Form + Zod Schema]

### Database
- **Tabellen**: [Neue oder geänderte Tabellen]
- **Migrationen**: [Liquibase Changeset]

## Design/UX

- **Wireframes**: [Link zu Figma/Sketch]
- **Mockups**: [Link oder Screenshots]
- **User Flow**: [Beschreibung oder Diagramm]

## Definition of Done

- [ ] Code implementiert
- [ ] Unit Tests geschrieben (> 80% Coverage)
- [ ] Integration Tests (falls nötig)
- [ ] Code Review durchgeführt
- [ ] Dokumentation aktualisiert
- [ ] Akzeptanzkriterien erfüllt
- [ ] Keine bekannten Bugs
- [ ] Deployed auf Dev-Umgebung
- [ ] Product Owner Approval

## Notizen

[Zusätzliche Informationen, offene Fragen, Diskussionen]

---

## Beispiel: User Story ausgefüllt

# User Story: Challenge-Erstellung durch Lehrer

## Beschreibung

**Als** Lehrer  
**möchte ich** eine Challenge für meine Klasse erstellen  
**damit** ich meine Schüler motivieren kann, mehr Nachhaltigkeits-Aktivitäten durchzuführen

## Akzeptanzkriterien

- [ ] **GIVEN** ein eingeloggter Lehrer im Admin-Web  
      **WHEN** er das Challenge-Formular ausfüllt (Titel, Beschreibung, Start-/Enddatum, Ziel-Points)  
      **THEN** wird eine neue Challenge im Status DRAFT erstellt  
      **AND** die Challenge erscheint in seiner Challenge-Liste

- [ ] **GIVEN** eine Challenge im Status DRAFT  
      **WHEN** der Lehrer sie aktiviert  
      **THEN** wechselt der Status zu ACTIVE  
      **AND** alle Schüler der Klasse erhalten eine Benachrichtigung

- [ ] **GIVEN** ungültige Daten (z.B. Enddatum vor Startdatum)  
      **WHEN** das Formular abgeschickt wird  
      **THEN** werden Validierungsfehler angezeigt  
      **AND** die Challenge wird nicht erstellt

## Bounded Context

- [ ] Scoring
- [x] Challenge
- [ ] UserProfile
- [x] Administration (für Klassen-Zuordnung)

## Plattform

- [ ] Mobile (Anzeige der Challenge)
- [x] Admin-Web (Challenge-Erstellung)
- [x] Backend (Challenge Domain)

## Story Points

**Schätzung**: 5

**Begründung**: 
- Form mit Validation (2)
- Domain Logic mit State Transitions (2)
- Event Publishing (1)
- REST API (bereits bekannt)

## Abhängigkeiten

- [x] Abhängig von: #42 - Keycloak Integration für Lehrer-Auth
- [ ] Blockiert: #56 - Push Notifications

## Technische Notizen

### Backend
- **Domain Layer**: 
  - `Challenge` Entity (AggregateRoot)
  - `ChallengeStatus` Enum (DRAFT, ACTIVE, COMPLETED)
  - `DateRange` Value Object
  - `PointsTarget` Value Object
- **Use Cases**: 
  - `CreateChallengeUseCase`
  - `ActivateChallengeUseCase`
- **REST API**: 
  - `POST /api/challenges`
  - `PUT /api/challenges/{id}/activate`
- **Events**: 
  - `ChallengeCreatedEvent`
  - `ChallengeActivatedEvent`

### Frontend
- **Components**: 
  - `CreateChallengeForm` (Form)
  - `ChallengeCard` (Display)
  - `ChallengeList` (Liste)
- **State Management**: 
  - `useCreateChallenge()` Hook
  - `useChallenges()` Hook
- **Forms**: 
  - Zod Schema für Validation

### Database
- **Tabellen**: 
  - `challenges` (id, title, description, start_date, end_date, target_points, status, school_class_id)
- **Migrationen**: 
  - Liquibase Changeset: `001-create-challenges-table.xml`

## Design/UX

- **Wireframes**: [Link to Figma]
- **User Flow**: Lehrer → Dashboard → "Neue Challenge" → Form → Erstellen → Aktivieren

## Definition of Done

- [x] Code implementiert
- [x] Unit Tests geschrieben (85% Coverage)
- [x] Integration Tests für REST API
- [x] Code Review durchgeführt
- [ ] Dokumentation aktualisiert
- [ ] Akzeptanzkriterien erfüllt
- [ ] Keine bekannten Bugs
- [ ] Deployed auf Dev-Umgebung
- [ ] Product Owner Approval
