# 🌐 Admin-Web - User Stories

Diese User Stories betreffen das Admin/Lehrer-Webinterface (React + TypeScript + Vite).

---

## Must-Haves (P1)

### M-US-1: Registrierung & Login
**Story Points:** 8 | **Epic:** Onboarding & Auth

> Als Nutzer möchte ich mich registrieren und sicher anmelden können, um EcoTrack personalisiert zu nutzen.

**Web-Fokus:**
- Lehrer/Admin Login
- Session-Handling, Logout
- Fehlerbehandlung bei gesperrten Nutzern
- Rate-Limit mit Captcha

📄 [Vollständige User Story](../all/must-haves/US-1%20Registrierung%20%26%20Login.md)

---

### M-US-5: Challenge anlegen
**Story Points:** 14 | **Epic:** Challenges & Klassensteuerung

> Als Lehrer möchte ich eine neue Umwelt-Challenge für meine Klasse anlegen.

**Web-Fokus:**
- Formular: Titel, Beschreibung, Zielwert, Zeitraum
- Validierung der Pflichtfelder
- Challenge-Übersicht nach Speichern
- Fortschrittsberechnung (Prozent)
- Archiv für abgeschlossene Challenges
- Duplizieren von Challenges

📄 [Vollständige User Story](../all/must-haves/US-5%20Challenge%20Anlegen.md)

---

### M-US-6: Dashboard für Lehrer
**Story Points:** 10 | **Epic:** Lehrer-Verwaltung & Monitoring

> Als Lehrer möchte ich ein Dashboard sehen, das mir die Fortschritte meiner Schüler und Klassen anzeigt.

**Web-Fokus:**
- Klassenübersicht mit Auswahl
- Summenübersicht (Aktionen, Gesamtpunkte, Durchschnitt)
- Schüler-Tabelle (Name, Aktionen, Punkte)
- Challenge-Fortschrittsbalken
- Filter (Zeitraum, Klasse) und Sortierung
- Schüler-Detailansicht

📄 [Vollständige User Story](../all/must-haves/US-6%20Dashboard%20für%20Lehrer.md)

---

### M-US-7: Nutzerverwaltung
**Story Points:** 8 | **Epic:** Administration

> Als Administrator möchte ich Nutzer und Rollen verwalten, um den sicheren Betrieb zu gewährleisten.

**Web-Fokus:**
- Nutzer anlegen (Name, E-Mail, Rolle, Klasse)
- E-Mail-Duplikat-Prüfung
- Bearbeiten von Rolle/Klasse
- Sperren/Aktivieren von Nutzern
- Listenansicht mit Suche/Filter

📄 [Vollständige User Story](../all/must-haves/US-7%20Nutzerverwaltung.md)

---

## Should-Haves (P2)

### S-US-2: Aktionskatalog & Verlauf (Katalogpflege)
**Story Points:** 13 | **Epic:** Aktionen & Scoring

> Als Admin/Lehrer möchte ich den Aktionskatalog pflegen können.

**Web-Fokus:**
- Listenansicht mit Kategorien, Suchfeld, Filter
- CRUD für Aktionen (Name, Kategorie, Einheit, Punktwert)
- Modifikatoren (Start/Ende + Faktor)
- Deaktivieren statt Löschen

📄 [Vollständige User Story](../all/should-haves/US-2%20Aktionskatalog%20mit%20Kategorien%20und%20Verlauf.md)

---

### S-US-4: Schul-Rangliste & Filter (Lehrer-Ansicht)
**Story Points:** 8 | **Epic:** Ranglisten & Wettbewerb

> Als Lehrer möchte ich schulweite Ranglisten mit Filtern sehen.

**Web-Fokus:**
- Klassenranking-Übersicht
- Top-Schüler-Ansicht
- Zeitraum-Filter
- Aggregation und Caching

📄 [Vollständige User Story](../all/should-haves/US-4%20Schul-Rangliste%20%26%20Filter.md)

---

### S-US-5: Challenge-Vorlagen
**Story Points:** 5 | **Epic:** Challenges & Klassensteuerung

> Als Lehrer möchte ich Challenges aus Vorlagen anlegen, um wiederkehrende Aktionen schneller zu planen.

**Web-Fokus:**
- Vorlagen-Bereich mit Liste + Suchfeld
- Erstellen/Bearbeiten/Duplizieren von Vorlagen
- "Aus Vorlage anlegen" mit vorausgefülltem Formular
- Schulweite Nutzbarkeit

📄 [Vollständige User Story](../all/should-haves/US-5%20Challenge%20Vorlagen.md)

---

### S-US-7: Schul-/Gemeinde-Dashboard
**Story Points:** 8 | **Epic:** Dashboard & Auswertung

> Als Verwaltung/Lehrer möchte ich Trends und Top-Kategorien sehen, um Maßnahmen abzuleiten.

**Web-Fokus:**
- Diagramm "Punkte pro Monat" (12 Monate)
- Top-5 Kategorien nach Punkten
- Zeitraum-Vergleich (Delta/Prozent)
- Drilldown Schule → Klasse
- Export (PNG/SVG)

📄 [Vollständige User Story](../all/should-haves/US-7%20Schul-Gemeinde%20Dashboard.md)

---

### S-US-8: Benutzerverwaltung (Bulk-Import & Passwort-Reset)
**Story Points:** 13 | **Epic:** Administration & Onboarding/Auth

> Als Admin möchte ich Benutzer massenhaft per CSV anlegen und Passwörter sicher zurücksetzen können.

**Web-Fokus:**
- CSV-Upload mit Schema-Validierung
- Vorschau-Tabelle mit Validierungsstatus
- Teil-Import (nur gültige Zeilen)
- Ergebnisübersicht + Fehler-CSV Download
- Passwort-Reset (Admin-seitig)

📄 [Vollständige User Story](../all/should-haves/US-8%20Benutzerverwaltung.md)

---

## Zusammenfassung

| Priorität | Anzahl | Story Points |
|-----------|--------|--------------|
| Must-Have | 4 | 40 |
| Should-Have | 5 | 47 |
| **Gesamt** | **9** | **87** |
