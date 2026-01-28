# ⚙️ Server/Backend - User Stories

Diese User Stories betreffen das Backend (Spring Boot, Java 21, Modularer Monolith).

---

## Architektur-Übersicht

| Modul | Domain-Typ | User Stories |
|-------|------------|--------------|
| `module-scoring` | Core Domain | M-US-2, M-US-3, M-US-4, S-US-2, S-US-3, S-US-4, S-US-6 |
| `module-challenge` | Core Domain | M-US-5, S-US-5 |
| `module-userprofile` | Supporting Domain | M-US-1, S-US-1 |
| `module-administration` | Generic Domain (ACL) | M-US-1, M-US-7, S-US-8 |

---

## Must-Haves (P1)

### M-US-1: Registrierung & Login
**Story Points:** 8 | **Epic:** Onboarding & Auth

> Als Nutzer möchte ich mich registrieren und sicher anmelden können.

**Backend-Module:** `module-administration`, `module-userprofile`

**Server-Fokus:**
- Keycloak-Integration (ACL)
- Session-Management
- Rate-Limiting (5 Fehlversuche/5 min)
- Rollen-Validierung (Schüler, Lehrer, Admin)
- Klassen-Zuordnung bei Schülern
- TLS-verschlüsselte Übertragung

📄 [Vollständige User Story](../all/must-haves/US-1%20Registrierung%20%26%20Login.md)

---

### M-US-2: Aktion erfassen
**Story Points:** 13 | **Epic:** Aktionen & Scoring

> Als Schüler möchte ich nachhaltige Aktionen manuell erfassen, um Punkte zu sammeln.

**Backend-Modul:** `module-scoring` (Hexagonal Architecture)

**Server-Fokus:**
- Aktionskatalog-API (Kategorien, Punktwerte)
- Buchungs-Erstellung (Aktion, Menge, Datum, User)
- Punkteberechnung: `Punkte = Menge × Aktions-Punktwert`
- Duplikat-Erkennung (identische Aktion+Menge+Datum innerhalb 5 min)
- Challenge-Fortschritt automatisch aktualisieren
- Audit-Trail (Wer/Wann/Was)

📄 [Vollständige User Story](../all/must-haves/US-2%20Aktion%20erfassen.md)

---

### M-US-3: Punkte & Fortschritt
**Story Points:** 8 | **Epic:** Punkte & Fortschritt

> Als Schüler möchte ich meinen aktuellen Punktestand und Fortschritt sehen.

**Backend-Modul:** `module-scoring` (Hexagonal Architecture)

**Server-Fokus:**
- Punktestand-Aggregation (idempotent)
- Historie-API (Paginierung, max 10 pro Seite)
- Level-Berechnung (konfigurierbare Schwellen: 0/100/250/500/1000)
- Meilenstein-Tracking
- Konsistenz bei parallelen Buchungen

📄 [Vollständige User Story](../all/must-haves/US-3%20Punkte%20und%20Fortschritt.md)

---

### M-US-4: Ranglisten (Klasse)
**Story Points:** 8 | **Epic:** Ranglisten & Wettbewerb

> Als Schüler möchte ich meine Platzierung innerhalb meiner Klasse sehen.

**Backend-Modul:** `module-scoring` (Hexagonal Architecture)

**Server-Fokus:**
- Ranglisten-Aggregation pro Klasse
- Gleichstand-Behandlung (gleiche Rangnummer)
- Zeitraum-Filter (Gesamt/Monat/Woche)
- Aktualisierung <30s nach neuer Aktion
- Datenschutz-Option (Initialen)
- Performance: <2s bei 100 Schülern

📄 [Vollständige User Story](../all/must-haves/US-4%20Ranglisten.md)

---

### M-US-5: Challenge anlegen
**Story Points:** 14 | **Epic:** Challenges & Klassensteuerung

> Als Lehrer möchte ich eine neue Umwelt-Challenge für meine Klasse anlegen.

**Backend-Modul:** `module-challenge` (Hexagonal Architecture)

**Server-Fokus:**
- Challenge-CRUD (Titel, Beschreibung, Zielwert, Zeitraum)
- Klassen-Zuordnung
- Fortschrittsberechnung: `(erreichte Punkte / Zielpunkte) × 100`
- Automatischer Status "abgeschlossen" nach Enddatum
- Duplikat-Warnung bei gleichem Titel in Klasse
- Event: Challenge-Ende / Ziel erreicht

📄 [Vollständige User Story](../all/must-haves/US-5%20Challenge%20Anlegen.md)

---

### M-US-6: Dashboard für Lehrer
**Story Points:** 10 | **Epic:** Lehrer-Verwaltung & Monitoring

> Als Lehrer möchte ich ein Dashboard mit Fortschritten meiner Schüler sehen.

**Backend-Module:** `module-scoring`, `module-challenge`, `module-userprofile`

**Server-Fokus:**
- Klassen-Übersicht API
- Aggregationen: Summe Aktionen, Gesamtpunkte, Durchschnitt
- Schüler-Liste mit Statistiken
- Challenge-Fortschritt-API
- Filter-Logik (Zeitraum, Klasse)
- Live/periodische Aktualisierung

📄 [Vollständige User Story](../all/must-haves/US-6%20Dashboard%20für%20Lehrer.md)

---

### M-US-7: Nutzerverwaltung
**Story Points:** 8 | **Epic:** Administration

> Als Administrator möchte ich Nutzer und Rollen verwalten.

**Backend-Module:** `module-administration`, `module-userprofile`

**Server-Fokus:**
- CRUD für Nutzer (Name, E-Mail, Rolle, Klasse)
- E-Mail-Eindeutigkeit prüfen
- Sperren/Aktivieren von Nutzern
- RBAC (nur Admin sieht Nutzerverwaltung)
- Audit-Trail (Ersteller, Zeit, Änderungen)

📄 [Vollständige User Story](../all/must-haves/US-7%20Nutzerverwaltung.md)

---

## Should-Haves (P2)

### S-US-1: Profil & Zuordnung
**Story Points:** 5 | **Epic:** Onboarding & Auth

**Backend-Modul:** `module-userprofile`

**Server-Fokus:**
- Profil-API
- Klassenwechsel-Workflow (Anfrage → Genehmigung)
- Historische Punkte bei Klassenwechsel erhalten
- Audit-Trail

📄 [Vollständige User Story](../all/should-haves/US-1%20Profil%20und%20Zuordnung.md)

---

### S-US-2: Aktionskatalog & Verlauf
**Story Points:** 13 | **Epic:** Aktionen & Scoring

**Backend-Modul:** `module-scoring`

**Server-Fokus:**
- Katalog-CRUD (Admin/Lehrer)
- Modifikator-Logik (Faktor bei Buchung speichern)
- Deaktivieren statt Löschen
- Prospektive Punktwert-Änderungen
- Server-Side Paging für Historie (500+ Einträge)

📄 [Vollständige User Story](../all/should-haves/US-2%20Aktionskatalog%20mit%20Kategorien%20und%20Verlauf.md)

---

### S-US-3: Meilenstein-Feedback & Level-Details
**Story Points:** 3 | **Epic:** Punkte & Fortschritt

**Backend-Modul:** `module-scoring`

**Server-Fokus:**
- Meilenstein-API mit Datum
- Top-3 Aktionen nach Punkte-Effizienz
- Level-Schwellen-Konfiguration

📄 [Vollständige User Story](../all/should-haves/US-3%20Meilenstein-Feedback%20%26%20Level-Details.md)

---

### S-US-4: Schul-Rangliste & Filter
**Story Points:** 8 | **Epic:** Ranglisten & Wettbewerb

**Backend-Modul:** `module-scoring`

**Server-Fokus:**
- Schulweite Aggregation (50 Klassen/1000 Schüler)
- Server-Side Caching
- Datenschutz-Filterung

📄 [Vollständige User Story](../all/should-haves/US-4%20Schul-Rangliste%20%26%20Filter.md)

---

### S-US-5: Challenge-Vorlagen
**Story Points:** 5 | **Epic:** Challenges & Klassensteuerung

**Backend-Modul:** `module-challenge`

**Server-Fokus:**
- Vorlagen-CRUD
- Schulweite Sichtbarkeit
- Berechtigungs-Logik

📄 [Vollständige User Story](../all/should-haves/US-5%20Challenge%20Vorlagen.md)

---

### S-US-6: Belohnungssystem
**Story Points:** 8 | **Epic:** Belohnungen

**Backend-Modul:** `module-scoring` (oder neues Modul)

**Server-Fokus:**
- Reward-Katalog API
- Atomare Einlöse-Transaktion
- Voucher-Code-Generierung (10+ Zeichen)
- Audit-Trail

📄 [Vollständige User Story](../all/should-haves/US-6%20Belohnungssystem.md)

---

### S-US-7: Schul-/Gemeinde-Dashboard
**Story Points:** 8 | **Epic:** Dashboard & Auswertung

**Backend-Module:** `module-scoring`, `module-userprofile`

**Server-Fokus:**
- Aggregations-APIs (Punkte/Monat, Top-Kategorien)
- Zeitraum-Vergleich
- Caching für Performance

📄 [Vollständige User Story](../all/should-haves/US-7%20Schul-Gemeinde%20Dashboard.md)

---

### S-US-8: Benutzerverwaltung (Bulk-Import & Passwort-Reset)
**Story Points:** 13 | **Epic:** Administration & Onboarding/Auth

**Backend-Module:** `module-administration`, `module-userprofile`

**Server-Fokus:**
- CSV-Parsing & Validierung (bis 5000 Zeilen, <60s)
- Transaktionssichere Batch-Writes
- Reset-Token (kryptografisch, 30 min gültig)
- Rate-Limit (3 Anfragen/15 min)
- Session-Invalidierung nach Reset

📄 [Vollständige User Story](../all/should-haves/US-8%20Benutzerverwaltung.md)

---

## Zusammenfassung

| Priorität | Anzahl | Story Points |
|-----------|--------|--------------|
| Must-Have | 7 | 69 |
| Should-Have | 8 | 63 |
| **Gesamt** | **15** | **132** |

### Nach Modul

| Modul | User Stories | Story Points |
|-------|--------------|--------------|
| `module-scoring` | 9 | ~70 |
| `module-challenge` | 2 | ~19 |
| `module-userprofile` | 4 | ~26 |
| `module-administration` | 3 | ~29 |
