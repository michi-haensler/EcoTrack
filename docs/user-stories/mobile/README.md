# 📱 Mobile App - User Stories

Diese User Stories betreffen die mobile Schüler-App (React Native).

---

## Must-Haves (P1)

### M-US-1: Registrierung & Login
**Story Points:** 8 | **Epic:** Onboarding & Auth

> Als Nutzer möchte ich mich registrieren und sicher anmelden können, um EcoTrack personalisiert zu nutzen.

**Mobile-Fokus:**
- Schüler-Registrierung mit Klassen-Zuordnung
- Login mit E-Mail/Passwort
- Session-Handling, Logout
- Fehlerbehandlung bei gesperrten Nutzern

📄 [Vollständige User Story](../all/must-haves/US-1%20Registrierung%20%26%20Login.md)

---

### M-US-2: Aktion erfassen
**Story Points:** 13 | **Epic:** Aktionen & Scoring

> Als Schüler möchte ich nachhaltige Aktionen manuell erfassen, um Punkte zu sammeln.

**Mobile-Fokus:**
- Aktionskatalog nach Kategorien + Suchfeld
- Formular: Menge, Datum, Notiz
- Punkte-Gutschrift & Historie
- "Rückgängig machen"-Button (60s)
- Duplikat-Hinweis bei identischer Aktion

📄 [Vollständige User Story](../all/must-haves/US-2%20Aktion%20erfassen.md)

---

### M-US-3: Punkte & Fortschritt
**Story Points:** 8 | **Epic:** Punkte & Fortschritt

> Als Schüler möchte ich meinen aktuellen Punktestand samt Veränderungen und eine visuelle Baum-Fortschrittsanzeige sehen.

**Mobile-Fokus:**
- Gesamtpunktestand mit Änderungs-Badge
- Letzte Aktionen (Historie, min. 10 Einträge)
- Baum-Visualisierung mit Level-System
- Meilenstein-Hinweise (100/250/500 Punkte)
- Prozent zum nächsten Level

📄 [Vollständige User Story](../all/must-haves/US-3%20Punkte%20und%20Fortschritt.md)

---

### M-US-4: Ranglisten (Klasse)
**Story Points:** 8 | **Epic:** Ranglisten & Wettbewerb

> Als Schüler möchte ich meine Platzierung innerhalb meiner Klasse sehen, um mich zu vergleichen und motiviert zu bleiben.

**Mobile-Fokus:**
- Klassenrangliste mit allen Schülern
- Eigene Position hervorgehoben (sticky)
- Gleichstand-Behandlung
- Zeitraum-Filter (Gesamt/Monat/Woche)
- Datenschutz: Initialen-Option

📄 [Vollständige User Story](../all/must-haves/US-4%20Ranglisten.md)

---

## Should-Haves (P2)

### S-US-1: Profil & Zuordnung
**Story Points:** 5 | **Epic:** Onboarding & Auth

> Als Nutzer möchte ich mein Profil mit Klasse/Schule verwalten.

**Mobile-Fokus:**
- Profilseite (Name, E-Mail, Rolle, Klasse, Schule)
- Klassenwechsel-Anfrage
- Anonymitäts-Einstellung für Ranglisten

📄 [Vollständige User Story](../all/should-haves/US-1%20Profil%20und%20Zuordnung.md)

---

### S-US-2: Aktionskatalog & Verlauf (Schüler-Ansicht)
**Story Points:** 13 | **Epic:** Aktionen & Scoring

> Als Schüler möchte ich meinen persönlichen Verlauf filtern und exportieren können.

**Mobile-Fokus:**
- Historie-Tabelle mit Filtern (Zeitraum, Kategorie, Aktion)
- Kombinierbare Filter
- Punkte-Summe/Anzahl nach Filter

📄 [Vollständige User Story](../all/should-haves/US-2%20Aktionskatalog%20mit%20Kategorien%20und%20Verlauf.md)

---

### S-US-3: Meilenstein-Feedback & Level-Details
**Story Points:** 3 | **Epic:** Punkte & Fortschritt

> Als Schüler möchte ich detailliertes Feedback zu Meilensteinen und Level-Fortschritt.

**Mobile-Fokus:**
- Panel "Meilensteine" mit Datum
- Panel "Nächstes Level" mit Restpunkten
- Top-3 Aktionen-Vorschläge (Punkte-Effizienz)
- Info-Tooltip für Level-Schwellen

📄 [Vollständige User Story](../all/should-haves/US-3%20Meilenstein-Feedback%20%26%20Level-Details.md)

---

### S-US-4: Schul-Rangliste & Filter
**Story Points:** 8 | **Epic:** Ranglisten & Wettbewerb

> Als Schüler möchte ich schulweite Ranglisten mit Filtern sehen.

**Mobile-Fokus:**
- Klassenranking (alle Klassen der Schule)
- Top-Schüler (wenn Datenschutz erlaubt)
- Eigene Position sichtbar
- Zeitraum-Filter

📄 [Vollständige User Story](../all/should-haves/US-4%20Schul-Rangliste%20%26%20Filter.md)

---

### S-US-6: Belohnungssystem
**Story Points:** 8 | **Epic:** Belohnungen

> Als Schüler möchte ich Belohnungen ab einer Punkteschwelle einlösen.

**Mobile-Fokus:**
- Reward-Katalog (Schwelle, Restbestand)
- Einlösen-Funktion mit Validierung
- Beleg/Voucher-Code Anzeige
- Meldungen bei nicht genügend Punkten

📄 [Vollständige User Story](../all/should-haves/US-6%20Belohnungssystem.md)

---

## Zusammenfassung

| Priorität | Anzahl | Story Points |
|-----------|--------|--------------|
| Must-Have | 4 | 37 |
| Should-Have | 5 | 37 |
| **Gesamt** | **9** | **74** |
