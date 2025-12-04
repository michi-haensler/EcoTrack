# EcoTrack – User Stories & Domain Glossary

## User Stories & Domains

| User Story           | Context               | Typ        | Entities / Aggregate Roots / VOs                                                                 |
|----------------------|----------------------|------------|---------------------------------------------------------------------------------------------------|
| Register             | Identity & Access    | Generic    | User, EmailAddress, Password, Role, Session, UserStatus                                          |
| Aktion erfassen      | Scoring & Activity   | Core       | ActionCatalog, ActionDefinition, ActivityEntry, PointsLedger, Category, Unit, Quantity, ActivitySource, ActionHistory |
| Punkte & Fortschritt | Progress/Gamification| Core       | Level, LevelThreshold, Milestone, Percentage, TreeVisualization, ProgressSnapshot                |
| Rangliste            | Leaderboard          | Core       | RankingTable, RankingRow, Rank, PeriodType, PointsProjection                                     |
| Challenge anlegen    | Challenges           | Core       | Challenge, ChallengeGoal, ClassAssignment, ChallengeStatus, GoalUnit, TargetValue, ChallengeProgress, Title, Description |
| Lehrer-Dashboard     | Reporting/Dashboard  | Supporting | ClassSummary, ChallengeSummary, ActionStats, StudentSummary                                      |
| Nutzerverwaltung     | Administration       | Generic    | User, UserStatus, Class (Aggregate Root), School, Name, Role, AdminService                       |
| EcoUser-Profil       | User Profile         | Core       | EcoUser (Aggregate Root), Name, EmailAddress, Class- & School-Referenz                           |


---

## Glossar (Must-Have Domains)

---

### Bounded Context: User Profile

#### Begriff: EcoUser

**Definition:**  
Fachlicher Endnutzer von EcoTrack (z. B. Schüler oder Lehrkraft), der nachhaltige Aktionen erfasst, Punkte sammelt, an Challenges teilnimmt und in Ranglisten erscheint. Bündelt alle fachlich relevanten Profilinformationen: Zugehörigkeit zu Klasse und Schule, Punktestand, Gamification-Status (Level/Baum), ggf. Meilensteine oder Badges.

**Bounded Context:**  
User Profile

**Rolle im System:**  
Aggregate Root (fachliches Nutzerprofil). Andere Bounded Contexts (Scoring & Activity, Progress & Gamification, Leaderboard, Challenges, Reporting) referenzieren EcoUser ausschließlich über `ecoUserId`.

**Synonyme / Verwechslungsgefahr:**

- Synonyme: Endnutzer, Spieler, Teilnehmer, Schüler (in User Stories), Lehrkraft (als EcoUser mit Rolle `LEHRER`)
- Nicht verwechseln mit `User` (technischer Login-Account mit E-Mail, Passwort, Rollen)

**Beispiel:**

    EcoUser ecoUser = ecoUserRepository.load(ecoUserId);
    ecoUser.changeName(new Name("Anna", "Beispiel"));
    ecoUser.assignToClass(classId, schoolId);

**Repräsentation im Code:**  
Klasse `EcoUser` (Aggregate Root), Tabelle `eco_user`  
oder Projektion aus `user`, `class`, `school`, `points_ledger`, `progress_snapshot`.

**Owner:** Backend-Team / Domänenexperten Nachhaltigkeit  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** User Stories „Aktion erfassen“, „Punkte & Fortschritt“, „Rangliste“, „Challenge anlegen“, „Lehrer-Dashboard“


#### Begriff: Name

**Definition:**  
Vollständiger Name eines EcoUser bzw. User (Vorname, Nachname). Wird zur Anzeige im UI (z. B. Rangliste, Dashboard) verwendet.

**Bounded Context:**  
User Profile, Identity & Access, Administration

**Rolle im System:**  
Value Object

**Synonyme / Verwechslungsgefahr:**  
Anzeigename, Nutzername; nicht verwechseln mit Login-Namen (E-Mail-Adresse).

**Beispiel:**

    Name name = new Name("Anna", "Muster");
    ecoUser.changeName(name);

**Repräsentation im Code:**  
Klasse `Name` (Value Object) oder Felder `firstName`, `lastName` in `EcoUser` / `User`.

**Owner:** Admin-Team / Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Register“, „Nutzerverwaltung“, „Rangliste“


---

## Core Domain: Scoring & Activity

#### Begriff: ActionCatalog

**Definition:**  
Aggregate, das alle für eine Schule/Organisation gültigen `ActionDefinition`-Einträge enthält. Stellt sicher, dass nur zugelassene aktive Aktionen zur Punktevergabe verwendet werden und dient als fachliche Quelle für Aktionsauswahl.

**Bounded Context:**  
Scoring & Activity

**Rolle im System:**  
Aggregate Root

**Synonyme / Verwechslungsgefahr:**  
Aktionskatalog; nicht verwechseln mit `ActivityEntry`.

**Beispiel:**

    ActionCatalog catalog = actionCatalogRepository.load(schoolId);
    ActionDefinition def = catalog.findByName("Fahrrad statt Auto");

**Repräsentation im Code:**  
Klasse `ActionCatalog`, Tabelle `action_catalog`, 1:n zu `action_definition`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Aktion erfassen“


#### Begriff: ActionDefinition

**Definition:**  
Vordefinierter Aktionstyp (z. B. „Fahrrad statt Auto“, „Mehrwegbecher benutzt“) mit Name, Kategorie, Einheit, Basis-Punktwert und optionalem Aktiv-Zeitraum.

**Bounded Context:**  
Scoring & Activity

**Rolle im System:**  
Entity innerhalb `ActionCatalog`.

**Synonyme / Verwechslungsgefahr:**  
Aktionstyp, Punkte-Definition; nicht verwechseln mit `ActivityEntry`.

**Beispiel:**

    ActionDefinition def =
        new ActionDefinition("Fahrrad", Category.MOBILITAET, Unit.KM, 5);

**Repräsentation im Code:**  
Klasse `ActionDefinition`, Tabelle `action_definition`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Aktion erfassen“


#### Begriff: Category

**Definition:**  
Kategorie einer nachhaltigen Aktion (z. B. Mobilität, Konsum, Recycling, Energie). Dient Filterung, Auswertung und UI-Darstellung.

**Bounded Context:**  
Scoring & Activity

**Rolle im System:**  
Enum / Value Object

**Synonyme / Verwechslungsgefahr:**  
Aktionskategorie; nicht verwechseln mit Schulklasse (`Class`).

**Beispiel:**  
`Category.MOBILITAET`

**Repräsentation im Code:**  
Enum `Category`, Spalte `category` in `action_definition`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Aktion erfassen“


#### Begriff: Unit

**Definition:**  
Einheit, in der eine Aktion gemessen wird (z. B. Stück, Kilometer, Minuten, Kilogramm). Dient zur Interpretation der Menge (`Quantity`).

**Bounded Context:**  
Scoring & Activity

**Rolle im System:**  
Enum / Value Object

**Synonyme:**  
Einheit

**Beispiel:**  
`Unit.STUECK`, `Unit.KM`

**Repräsentation im Code:**  
Enum `Unit`, Spalte `unit` in `action_definition`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Aktion erfassen“


#### Begriff: Quantity

**Definition:**  
Menge einer erfassten Aktion (z. B. 3 Stück, 5 km). Stellt sicher, dass fachliche Regeln (z. B. > 0, ganzzahlig) eingehalten werden.

**Bounded Context:**  
Scoring & Activity

**Rolle im System:**  
Value Object

**Synonyme:**  
Menge

**Beispiel:**

    Quantity q = Quantity.of(5);

**Repräsentation im Code:**  
Klasse `Quantity` (Value Object), Spalte `quantity` in `activity_entry`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Aktion erfassen“


#### Begriff: ActivitySource

**Definition:**  
Quelle der Aktions-Erfassung (z. B. Mobile App, Web-Frontend, Import); dient Nachvollziehbarkeit und Analyse.

**Bounded Context:**  
Scoring & Activity

**Rolle im System:**  
Enum

**Beispiel:**  
`ActivitySource.APP`, `ActivitySource.WEB`

**Repräsentation im Code:**  
Enum `ActivitySource`, Spalte `source` in `activity_entry`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** technische Anforderungen


#### Begriff: ActivityEntry

**Definition:**  
Konkrete Aktions-Buchung eines EcoUser. Verweist auf `ActionDefinition`, enthält `Quantity`, berechnete Punkte, Zeitstempel, EcoUser-Referenz und optional Challenge-Bezug.

**Bounded Context:**  
Scoring & Activity

**Rolle im System:**  
Aggregate Root

**Synonyme:**  
Aktions-Buchung, Aktions-Eintrag

**Beispiel:**

    ActivityEntry entry =
        ActivityEntry.record(ecoUserId, def, Quantity.of(3), Instant.now());
    pointsLedger.addPoints(entry.getPoints());

**Repräsentation im Code:**  
Klasse `ActivityEntry`, Tabelle `activity_entry`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Aktion erfassen“, „Punkte & Fortschritt“


#### Begriff: PointsLedger

**Definition:**  
Punkte-Konto eines EcoUser. Hält aktuelle Gesamtpunkte und stellt konsistente Punkteoperationen sicher (Invariante: Summe aller gültigen Buchungen).

**Bounded Context:**  
Scoring & Activity

**Rolle im System:**  
Aggregate Root

**Synonyme:**  
Punktebuchhaltung, Punkte-Konto

**Beispiel:**

    pointsLedger.addPoints(activityEntry.getPoints());
    long balance = pointsLedger.getBalance();

**Repräsentation im Code:**  
Klasse `PointsLedger`, Tabelle `points_ledger`, Fremdschlüssel `ecoUserId`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Punkteübersicht“, „Punkte & Fortschritt“


#### Begriff: ActionHistory

**Definition:**  
Read Model für die Historie der `ActivityEntry`s eines EcoUser, filterbar nach Zeitraum, Kategorie, Aktion; Basis für UI und Exports.

**Bounded Context:**  
Scoring & Activity

**Rolle im System:**  
Read Model

**Synonyme / Verwechslungsgefahr:**  
Historie, Aktionsverlauf; nicht verwechseln mit `ActivityEntry`.

**Beispiel:**

    ActionHistory history =
      actionHistoryRepository.findByEcoUserAndPeriod(ecoUserId, period);

**Repräsentation im Code:**  
Klasse `ActionHistory` (DTO/View), DB-View oder Query auf `activity_entry`.

**Owner:** Backend-Team / Frontend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** Verlauf/Filter/Export


---

## Core Domain: Progress & Gamification

#### Begriff: Level

**Definition:**  
Gamifizierungsstufe eines EcoUser basierend auf seinem Punktestand; repräsentiert Baum-Wachstumsstufe (z. B. Setzling, Jungbaum, Baum, Altbaum).

**Bounded Context:**  
Progress & Gamification

**Rolle im System:**  
Enum / Value Object

**Synonyme:**  
Baumstufe, Rangstufe

**Beispiel:**

    Level currentLevel = Level.SETZLING;

**Repräsentation im Code:**  
Enum `Level`, Schwellenkonfiguration in `level_thresholds`.

**Owner:** Gamification-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Punkte & Fortschritt“


#### Begriff: LevelThreshold

**Definition:**  
Verknüpft ein Level mit einer Mindestpunktzahl; Grundlage für Level-Berechnung und Restpunkte.

**Bounded Context:**  
Progress & Gamification

**Rolle im System:**  
Entity

**Synonyme:**  
Level-Schwelle

**Beispiel:**

    LevelThreshold t = new LevelThreshold(Level.JUNGBaum, 250);
    boolean reached = t.isReached(totalPoints);

**Repräsentation im Code:**  
Klasse `LevelThreshold`, Tabelle `level_thresholds`.

**Owner:** Gamification-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Punkte & Fortschritt“


#### Begriff: Milestone

**Definition:**  
Benannter Meilenstein mit eigener ID und Punkteziel; beim Erreichen kann ein Event (z. B. Badge) ausgelöst werden.

**Bounded Context:**  
Progress & Gamification

**Rolle im System:**  
Entity

**Synonyme:**  
Meilenstein, Etappenziel

**Beispiel:**

    Milestone m = new Milestone(milestoneId, "100 Punkte", 100);
    if (m.isReached(totalPoints)) { /* MilestoneReachedEvent */ }

**Repräsentation im Code:**  
Klasse `Milestone`, Event `MilestoneReached`.

**Owner:** Gamification-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Punkte & Fortschritt“


#### Begriff: Percentage

**Definition:**  
Prozentwert (0–100), z. B. Fortschritt zum nächsten Level oder Challenge-Fortschritt; validiert Wertebereich.

**Bounded Context:**  
Progress & Gamification, Challenges

**Rolle im System:**  
Value Object

**Beispiel:**

    Percentage p = Percentage.of(75);

**Repräsentation im Code:**  
Klasse `Percentage`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Punkte & Fortschritt“, „Challenge anlegen“


#### Begriff: TreeVisualization

**Definition:**  
Modelliert die grafische Darstellung des Fortschritts als Baum, abgeleitet aus dem Level (z. B. Icon-/Asset-Namen).

**Bounded Context:**  
Progress & Gamification

**Rolle im System:**  
Value Object / UI-Modell

**Synonyme:**  
Baum-Visualisierung

**Beispiel:**

    TreeVisualization tree = TreeVisualization.from(level);

**Repräsentation im Code:**  
Klasse `TreeVisualization` (Frontend/Domain-UI).

**Owner:** Frontend-Team / Gamification-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Punkte & Fortschritt (Baum)“


#### Begriff: ProgressSnapshot

**Definition:**  
Aggregiertes Read Model des Fortschritts eines EcoUser zu einem Zeitpunkt: Gesamtpunkte, aktuelles Level, restliche Punkte/nächste Schwelle, erreichte Milestones.

**Bounded Context:**  
Progress & Gamification

**Rolle im System:**  
Read Model

**Synonyme:**  
Fortschrittsstand

**Beispiel:**

    ProgressSnapshot snapshot = progressService.computeSnapshot(ecoUserId);

**Repräsentation im Code:**  
Klasse `ProgressSnapshotView`.

**Owner:** Frontend-Team / Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Punkte & Fortschritt“


#### Begriff: GamificationService

**Definition:**  
Domänenservice, der aus Punkteständen und Konfiguration (`LevelThreshold`, `Milestone`) `ProgressSnapshot`s berechnet und Level-Ups bzw. neue Milestones erkennt.

**Bounded Context:**  
Progress & Gamification

**Rolle im System:**  
Domain Service

**Beispiel:**

    ProgressSnapshot oldSnap = ...;
    ProgressSnapshot newSnap = gamificationService.computeSnapshot(ecoUserId);
    boolean levelUp = gamificationService.detectLevelUp(oldSnap, newSnap);

**Repräsentation im Code:**  
Klasse `GamificationService`.

**Owner:** Backend-Team / Gamification-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Punkte & Fortschritt“


---

## Core Domain: Leaderboard & Competition

#### Begriff: PeriodType

**Definition:**  
Art des Zeitraums, für den eine Rangliste berechnet wird (Gesamt, Monat, Woche).

**Bounded Context:**  
Leaderboard

**Rolle im System:**  
Enum

**Synonyme:**  
Ranglisten-Zeitraum

**Beispiel:**  
`PeriodType.MONTH`, `PeriodType.TOTAL`

**Repräsentation im Code:**  
Enum `PeriodType`, Felder in `ranking_table`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Rangliste“


#### Begriff: Rank

**Definition:**  
Platzierung eines EcoUser oder einer Klasse in einer Rangliste (1., 2., 3. …).

**Bounded Context:**  
Leaderboard

**Rolle im System:**  
Value Object

**Synonyme:**  
Platzierung

**Beispiel:**

    Rank rank = Rank.of(1);

**Repräsentation im Code:**  
Klasse `Rank` (Value Object), Feld `position` in `RankingRow`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Rangliste“


#### Begriff: RankingTable

**Definition:**  
Rangliste für einen bestimmten Scope (z. B. Klasse oder Schule) und Zeitraum mit geordneten `RankingRow`s; typischerweise periodisch neu berechnet.

**Bounded Context:**  
Leaderboard

**Rolle im System:**  
Aggregate Root / Read Model

**Synonyme:**  
Rangliste

**Beispiel:**

    RankingTable table = rankingService.buildForClass(classId, PeriodType.MONTH);

**Repräsentation im Code:**  
Klasse `RankingTable`, Tabelle `ranking_table`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Rangliste“


#### Begriff: RankingRow

**Definition:**  
Zeile in `RankingTable`, repräsentiert EcoUser oder Klasse mit `Rank`, `subjectId`, Anzeigename und Punkten.

**Bounded Context:**  
Leaderboard

**Rolle im System:**  
Entity (Teil von `RankingTable`)

**Synonyme:**  
Ranglistenzeile

**Beispiel:**

    RankingRow row = new RankingRow(Rank.of(1), ecoUserId, "Anna", 340);

**Repräsentation im Code:**  
Klasse `RankingRow`, Tabelle `ranking_row`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Rangliste“


#### Begriff: PointsProjection

**Definition:**  
Read Model / Query-Service, der aggregierte Punktesummen pro EcoUser oder Klasse für bestimmte Zeiträume zurückliefert; Basis zur Berechnung von `RankingTable`s.

**Bounded Context:**  
Leaderboard

**Rolle im System:**  
Read Model / Query-Service

**Beispiel:**

    List<PointsAggregate> top =
      pointsProjection.findForScope(classId, "CLASS", PeriodType.MONTH);

**Repräsentation im Code:**  
Interface/Service `PointsProjection` (z. B. über SQL-Views).

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Rangliste“


---

## Core Domain: Challenges

#### Begriff: Title

**Definition:**  
Kurzer, aussagekräftiger Titel einer Challenge (z. B. „Plastikfreie Woche“).

**Bounded Context:**  
Challenges

**Rolle im System:**  
Value Object

**Beispiel:**

    Title t = new Title("Plastikfreie Woche");

**Repräsentation im Code:**  
Value Object `Title` oder String-Feld `title` in `Challenge`.

**Owner:** Lehrkräfte-Team / Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Challenge anlegen“


#### Begriff: Description

**Definition:**  
Textliche Beschreibung einer Challenge mit Regeln, Zielen und Beispielen.

**Bounded Context:**  
Challenges

**Rolle im System:**  
Value Object

**Beispiel:**

    Description d = new Description("Vermeidet diese Woche Einwegplastik in der Schule...");

**Repräsentation im Code:**  
Value Object `Description` oder String-Feld `description` in `Challenge`.

**Owner:** Lehrkräfte-Team / Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Challenge anlegen“


#### Begriff: TargetValue

**Definition:**  
Abstrakter Zielwert einer Challenge (z. B. 500 Punkte oder 50 Aktionen), der mit `GoalUnit` interpretiert wird.

**Bounded Context:**  
Challenges

**Rolle im System:**  
Value Object

**Beispiel:**

    TargetValue v = TargetValue.ofPoints(500);

**Repräsentation im Code:**  
Value Object `TargetValue` oder Feld in `ChallengeGoal`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Challenge anlegen“


#### Begriff: ChallengeStatus

**Definition:**  
Status einer Challenge (`DRAFT`, `ACTIVE`, `CLOSED`).

**Bounded Context:**  
Challenges

**Rolle im System:**  
Enum

**Beispiel:**  
`ChallengeStatus.ACTIVE`

**Repräsentation im Code:**  
Enum `ChallengeStatus`, Spalte `status` in `challenge`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Challenge anlegen“, „Challenge beendet“


#### Begriff: GoalUnit

**Definition:**  
Einheit, in der das Challenge-Ziel gemessen wird (z. B. Punkte oder Anzahl Aktionen).

**Bounded Context:**  
Challenges

**Rolle im System:**  
Enum

**Beispiel:**  
`GoalUnit.POINTS`, `GoalUnit.ACTIONS`

**Repräsentation im Code:**  
Enum `GoalUnit`, Bestandteil von `ChallengeGoal`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Challenge anlegen“


#### Begriff: ChallengeGoal

**Definition:**  
Value Object, das `TargetValue` und `GoalUnit` einer Challenge zusammenfasst und prüft, ob das Ziel anhand aktueller Punkte/Aktionen erreicht ist.

**Bounded Context:**  
Challenges

**Rolle im System:**  
Value Object

**Synonyme:**  
Zieldefinition

**Beispiel:**

    boolean reached = challengeGoal.isReached(points, actions);

**Repräsentation im Code:**  
Klasse `ChallengeGoal`.

**Owner:** Lehrkräfte-Team / Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Challenge anlegen“


#### Begriff: ClassAssignment

**Definition:**  
Zuordnung einer Challenge zu Klasse/Schule; zwei Instanzen mit gleicher `classId`/`schoolId` sind fachlich identisch.

**Bounded Context:**  
Challenges

**Rolle im System:**  
Value Object

**Synonyme:**  
Klassen-Zuweisung

**Beispiel:**

    ClassAssignment ca = new ClassAssignment(classId, schoolId);

**Repräsentation im Code:**  
Klasse `ClassAssignment` (ohne eigene ID), Felder in `challenge` oder Join-Tabelle.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Challenge anlegen“


#### Begriff: ChallengeProgress

**Definition:**  
Value Object, das Challenge-Fortschritt (Punkte, Aktionen, `Percentage`) zusammenfasst.

**Bounded Context:**  
Challenges

**Rolle im System:**  
Value Object

**Beispiel:**

    ChallengeProgress p = new ChallengeProgress(points, actions, Percentage.of(75));

**Repräsentation im Code:**  
Klasse `ChallengeProgress`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Challenge anlegen“, „Lehrer-Dashboard“


#### Begriff: Challenge

**Definition:**  
Zeitlich begrenztes Klassen-Ziel (z. B. „Sammelt 500 Punkte in 7 Tagen“). Wird von Lehrkraft (EcoUser mit Rolle `LEHRER`) angelegt, Klasse zugewiesen und durch Status gesteuert.

**Bounded Context:**  
Challenges

**Rolle im System:**  
Aggregate Root

**Synonyme:**  
Wettbewerb, Umwelt-Challenge

**Beispiel:**

    Challenge c = Challenge.create(
      new Title("Plastikfreie Woche"),
      ChallengeGoal.points(500),
      startDate, endDate,
      classAssignment,
      teacherEcoUserId
    );

**Repräsentation im Code:**  
Klasse `Challenge`, Tabelle `challenge`.

**Owner:** Lehrkräfte-Team / Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Challenge anlegen“


#### Begriff: ChallengeService

**Definition:**  
Domänenservice zum Anlegen, Aktivieren, Schließen, Duplizieren von Challenges sowie zur Fortschrittsberechnung.

**Bounded Context:**  
Challenges

**Rolle im System:**  
Domain Service

**Beispiel:**

    Challenge ch = challengeService.createChallengeForClass(...);
    challengeService.closeExpiredChallenges(LocalDate.now());

**Repräsentation im Code:**  
Klasse `ChallengeService`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Challenge anlegen“


---

## Generic Domain: Identity & Access

#### Begriff: EmailAddress

**Definition:**  
Value Object für E-Mail-Adresse mit Formatvalidierung und optionalen Regeln (z. B. Schuldomain); dient als Login-Name.

**Bounded Context:**  
Identity & Access

**Rolle im System:**  
Value Object

**Synonyme:**  
E-Mail

**Beispiel:**

    EmailAddress mail = EmailAddress.of("anna@schule.at");

**Repräsentation im Code:**  
Klasse `EmailAddress`, Feld `email` in `User`.

**Owner:** Backend-Team / Admin-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Register“


#### Begriff: Password

**Definition:**  
Value Object für Passwort im Domain-Layer, inkl. Richtlinien (Länge, Komplexität); persistiert als Hash.

**Bounded Context:**  
Identity & Access

**Rolle im System:**  
Value Object

**Synonyme:**  
Kennwort

**Beispiel:**

    Password pw = Password.fromPlaintext("SicheresPasswort123!");

**Repräsentation im Code:**  
Klasse `Password`, Feld `passwordHash` in `User`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Register“, „Passwort zurücksetzen“


#### Begriff: UserStatus

**Definition:**  
Status eines Users (z. B. `ACTIVE`, `DISABLED`); steuert Anmeldemöglichkeit.

**Bounded Context:**  
Identity & Access, Administration

**Rolle im System:**  
Enum

**Beispiel:**  
`UserStatus.ACTIVE`

**Repräsentation im Code:**  
Enum `UserStatus`, Spalte `status` in `user`.

**Owner:** Admin-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Nutzerverwaltung“


#### Begriff: Role

**Definition:**  
Rolle eines Users (`SCHUELER`, `LEHRER`, `ADMIN`), bestimmt Berechtigungen.

**Bounded Context:**  
Identity & Access

**Rolle im System:**  
Value Object / Enum

**Synonyme:**  
Benutzerrolle, Berechtigungsprofil

**Beispiel:**

    Role teacher = Role.LEHRER;

**Repräsentation im Code:**  
Enum `Role`, Feld bzw. Tabelle `roles`.

**Owner:** Admin-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Register“, „Nutzerverwaltung“


#### Begriff: User

**Definition:**  
Technischer Systembenutzer mit Login-Daten (`EmailAddress`, `PasswordHash`) und Rollen (`Role`). Dient der Authentifizierung/Autorisierung; fachliche Informationen liegen im `EcoUser`.

**Bounded Context:**  
Identity & Access

**Rolle im System:**  
Aggregate Root

**Synonyme:**  
Benutzer, Account

**Beispiel:**

    User u = new User(emailAddress, passwordHash);
    u.assignRole(Role.SCHUELER);

**Repräsentation im Code:**  
Klasse `User`, Tabelle `user`.

**Owner:** Admin-Team / Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Register“, „Nutzerverwaltung“


#### Begriff: Session

**Definition:**  
Temporäre Login-Sitzung nach erfolgreicher Authentifizierung mit Token und Ablaufzeit.

**Bounded Context:**  
Identity & Access

**Rolle im System:**  
Entity

**Synonyme:**  
Login-Sitzung

**Beispiel:**

    Session session = authService.authenticate(email, password);
    session.invalidate();

**Repräsentation im Code:**  
Klasse `Session`, Tabelle `sessions`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Login“, „Passwort zurücksetzen“


#### Begriff: Invitation

**Definition:**  
Einladung (z. B. für Schüler/Lehrer), mit der ein Account angelegt werden kann; enthält Ziel-E-Mail, Rolle, optional Klasse und Ablaufdatum.

**Bounded Context:**  
Identity & Access

**Rolle im System:**  
Entity

**Synonyme:**  
Einladungscode, Einladungslink

**Beispiel:**

    Invitation inv = new Invitation(email, Role.SCHUELER, classId);

**Repräsentation im Code:**  
Klasse `Invitation`, Tabelle `invitation`.

**Owner:** Admin-Team / Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Register“ (Einladungs-Flow)


#### Begriff: Registration

**Definition:**  
Prozess/Command, bei dem aus Invitation/Anmeldung ein `User` (technisch) und ein `EcoUser` (fachlich) erzeugt werden.

**Bounded Context:**  
Identity & Access, User Profile

**Rolle im System:**  
Domain Service / Command

**Beispiel:**

    EcoUser ecoUser = registrationService.registerStudent(invitationToken, password);

**Repräsentation im Code:**  
Service `RegistrationService` oder Command Handler.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Register“


#### Begriff: AuthService

**Definition:**  
Service für Authentifizierung und Session-Verwaltung (Login, Logout, Passwort-Reset).

**Bounded Context:**  
Identity & Access

**Rolle im System:**  
Domain Service

**Beispiel:**

    Session s = authService.authenticate(email, plainPassword);
    authService.logout(sessionId);

**Repräsentation im Code:**  
Klasse `AuthService`.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Login“, „Passwort zurücksetzen“


---

## Generic Domain: Administration

#### Begriff: Class

**Definition:**  
Schulklasse als organisatorische Einheit, die Schüler (EcoUser mit Rolle `SCHUELER`) und Lehrkräfte (EcoUser mit Rolle `LEHRER`) zusammenfasst.

**Bounded Context:**  
Administration

**Rolle im System:**  
Aggregate Root

**Synonyme:**  
Lerngruppe

**Beispiel:**

    Class c = new Class("3A", schoolId);
    c.addStudent(userId);
    c.assignTeacher(teacherUserId);

**Repräsentation im Code:**  
Klasse `Class`, Tabelle `class`.

**Owner:** Admin-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Nutzerverwaltung“


#### Begriff: School

**Definition:**  
Schule als Organisation mit mehreren Klassen und Lehrkräften.

**Bounded Context:**  
Administration

**Rolle im System:**  
Aggregate Root

**Synonyme:**  
Bildungseinrichtung

**Beispiel:**

    School s = new School("BG Beispielstadt");
    s.addClass(classId);

**Repräsentation im Code:**  
Klasse `School`, Tabelle `school`.

**Owner:** Admin-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Nutzerverwaltung“


#### Begriff: AdminService

**Definition:**  
Service für Verwaltung von Schulen, Klassen und Zuordnung von Usern zu Klassen (inkl. Bulk-Import).

**Bounded Context:**  
Administration

**Rolle im System:**  
Domain Service

**Beispiel:**

    adminService.createSchool("BG Beispielstadt");
    adminService.createClass(schoolId, "3A", "3");
    adminService.assignUserToClass(userId, classId);

**Repräsentation im Code:**  
Klasse `AdminService`.

**Owner:** Admin-Team / Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Nutzerverwaltung“, „Bulk-Import Nutzer“


---

## Supporting Domain: Reporting / Dashboard

#### Begriff: ClassSummary

**Definition:**  
Read Model für eine Klasse in einem Zeitraum (z. B. Woche/Monat/Gesamt) mit Gesamtpunkten, Anzahl Aktionen, aktiven Challenges, Top-Aktionen und Top-Schülern.

**Bounded Context:**  
Reporting / Dashboard

**Rolle im System:**  
Read Model / DTO

**Synonyme:**  
Klassenübersicht

**Beispiel:**

    ClassSummary summary = dashboardService.getClassSummary(classId, period);

**Repräsentation im Code:**  
ViewModel `ClassSummary`.

**Owner:** Dashboard-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Lehrer-Dashboard“


#### Begriff: ChallengeSummary

**Definition:**  
Read Model für eine Challenge mit Titel, Status, erreichten Punkten und Fortschritt in Prozent.

**Bounded Context:**  
Reporting / Dashboard

**Rolle im System:**  
Read Model / DTO

**Synonyme:**  
Challenge-Übersicht

**Beispiel:**

    ChallengeSummary cs = dashboardService.getChallengeSummary(challengeId);

**Repräsentation im Code:**  
ViewModel `ChallengeSummary`.

**Owner:** Dashboard-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Lehrer-Dashboard“


#### Begriff: ActionStats

**Definition:**  
Aggregierte Statistiken für Aktionen in einer Klasse/Schule und Zeitraum (Anzahl, Punkte pro Aktion).

**Bounded Context:**  
Reporting / Dashboard

**Rolle im System:**  
Read Model

**Beispiel:**

    ActionStats stats = new ActionStats(actionDefId, "Fahrrad", 42, 210);

**Repräsentation im Code:**  
Klasse `ActionStats`.

**Owner:** Dashboard-Team / Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Lehrer-Dashboard“


#### Begriff: StudentSummary

**Definition:**  
Read Model, das einen EcoUser im Klassenkontext abbildet: `EcoUserId`, Name, Punkte, Rang in der Klasse.

**Bounded Context:**  
Reporting / Dashboard

**Rolle im System:**  
Read Model

**Beispiel:**

    StudentSummary s = new StudentSummary(ecoUserId, "Anna", 340, 1);

**Repräsentation im Code:**  
Klasse `StudentSummary`.

**Owner:** Dashboard-Team / Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Lehrer-Dashboard“, Klassenrangliste


#### Begriff: DashboardService

**Definition:**  
Service, der aus Core-Domain-Daten (Scoring, Challenges, Leaderboard) `ClassSummary` und `ChallengeSummary` erzeugt.

**Bounded Context:**  
Reporting / Dashboard

**Rolle im System:**  
Domain Service

**Beispiel:**

    ClassSummary summary = dashboardService.getClassSummary(classId, Period.TOTAL);

**Repräsentation im Code:**  
Klasse `DashboardService`.

**Owner:** Backend-Team / Dashboard-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Lehrer-Dashboard“


---

## Cross-Cutting Concepts

#### Begriff: Date

**Definition:**  
Datum (ohne Zeitanteil); wird für Start-/Enddaten von Challenges, Aktionszeiträumen und Ranglistenperioden verwendet.

**Bounded Context:**  
Generic (mehrere Kontexte)

**Rolle im System:**  
Value Object

**Beispiel:**  
`LocalDate.now();`

**Repräsentation im Code:**  
z. B. `java.time.LocalDate`.

**Owner:** –  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Aktion erfassen“, „Challenge anlegen“


#### Begriff: Period

**Definition:**  
Abstrakter Zeitraum für Auswertungen (z. B. „letzte Woche“, „dieser Monat“, „gesamt“).

**Bounded Context:**  
Generic, Leaderboard, Reporting

**Rolle im System:**  
Value Object / Enum

**Beispiel:**

    Period p = Period.MONTH;

**Repräsentation im Code:**  
Enum `Period` oder Value Object mit Start-/Enddatum.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Rangliste“, „Lehrer-Dashboard“


#### Begriff: AuditTrail

**Definition:**  
Nachverfolgung wichtiger fachlicher oder sicherheitsrelevanter Aktionen (z. B. Punkte- oder Rollenänderungen, Bulk-Importe). Speichert wer, was, wann und ggf. warum.

**Bounded Context:**  
Cross-Cutting

**Rolle im System:**  
Entity / technisches Konzept

**Beispiel:**

    auditTrail.log("USER_ROLE_CHANGED", adminUserId, targetUserId, details);

**Repräsentation im Code:**  
Klasse `AuditTrailEntry`, Tabelle `audit_trail`.

**Owner:** Backend-Team / Admin-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** „Aktion erfassen“ (Nachvollziehbarkeit), „Nutzerverwaltung“, „Bulk-Import Nutzer“


#### Begriff: Domain Events (Übersicht)

**Definition:**  
Domain Events signalisieren fachlich relevante Änderungen, die von anderen Bounded Contexts oder externen Systemen konsumiert werden können.

**Typische Events:**

- `ActivityRecorded` – Neue `ActivityEntry` für EcoUser erfasst.
- `PointsCredited` – Punkte einem `PointsLedger` gutgeschrieben.
- `MilestoneReached` – `Milestone` von EcoUser erreicht.
- `LevelAdvanced` – EcoUser erreicht neues Level.
- `ChallengeCreated` – Neue `Challenge` angelegt.
- `ChallengeClosed` – `Challenge` beendet.
- `ChallengeGoalReached` – Challenge-Ziel erreicht.
- `LeaderboardUpdated` – `RankingTable` neu berechnet.

**Bounded Context:**  
Auslösende Aggregate in Scoring, Progress, Challenges, Leaderboard; konsumiert z. B. im Reporting.

**Rolle im System:**  
Domain Events (Event-Driven Architecture)

**Repräsentation im Code:**  
Event-Klassen wie `ActivityRecordedEvent`, `PointsCreditedEvent`, `MilestoneReachedEvent`, usw.

**Owner:** Backend-Team  
**Status:** draft  
**Letzte Änderung:** 2025-11-19  
**Referenzen:** Event-getriebene Architektur, Reporting-Anforderungen
