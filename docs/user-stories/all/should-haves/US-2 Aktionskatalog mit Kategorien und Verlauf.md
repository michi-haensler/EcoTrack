Kovacs & Grigic

# Card: Aktionskatalog & Verlauf mit Filtern (Pflege,

# Transparenz, Korrektur)

**User Story:** Als Admin/Lehrer/Schüler möchte ich Aktionen konsistent erfassen
(kategorisiert, mit Punktwerten) und meinen persönlichen Verlauf filtern, exportieren und
bei Bedarf korrigieren lassen, damit Punkte transparent, nachvollziehbar und fair sind.
**Storypoints: 13
Priorität:** Should-Have (P2)
**Epic:** Aktionen & Scoring

## Conversation

- **Katalogpflege (Admin/Lehrer):** Kategorien (Mobilität, Konsum, Recycling, Energie
    ...); Aktionen mit Name, Beschreibung, Einheit (Stück, km ...), Standard-Punktwert;
    temporäre Modifikatoren (z. B. „Aktionswoche +20 %“); Aktionen deaktivierbar statt
    löschen.
- **Verlauf & Transparenz (Schüler):** Persönliche Aktions-Historie mit Filtern
    (Zeitraum/Kategorie/Aktion),
- **Fairness:** Neue Punktwerte gelten **prospektiv** ; historische Buchungen bleiben
    unverändert.

## Confirmation (Akzeptanzkriterien)

**Funktional**

**A. Katalogpflege (Admin/Lehrer)**

1. **Listenansicht** zeigt Kategorien, Suchfeld, Filter; **CRUD** für Aktionen mit Pflichtfeldern:
    Name*, Kategorie*, Einheit*, Punktwert* (> 0); Beschreibung optional.
2. **Validierung:** Pflichtfelder & eindeutiger Aktionsname **innerhalb** einer Kategorie;
    klare Inline-Fehlermeldungen.
3. **Modifikator** je Aktion: Start/Ende + Faktor (z. B. 1.2) – wird beim **Buchen**
    automatisch berücksichtigt; in der Buchung gespeichert (Nachvollziehbarkeit).
4. **Deaktivieren** statt Löschen: deaktivierte Aktionen sind nicht buchbar und in
    Katalog/Autocomplete ausgeblendet; Historie bleibt sichtbar.

**B. Verlauf & Transparenz (Schüler)**

6. **Historie-Tabelle** zeigt je Eintrag: Datum, Kategorie, Aktion, Menge, berechnete Punkte,
ggf. Challenge-Bezug; Sortierung absteigend nach Buchungszeitpunkt.


Kovacs & Grigic

7. **Filter** : Zeitraum (Woche/Monat/Benutzerdefiniert), Kategorie, Aktion; Filter sind
kombinierbar und wirken auf Zähler (Summe Punkte/Anzahl).

**Nicht-funktional**

- **Performance:**
    o Katalog laden < **1,5 s** (100+ Aktionen); Speichern einer Aktion < **2 s**.
    o Historie laden < **2 s** bei **500** Einträgen (Server-Paging)
- **Konsistenz & Nachvollziehbarkeit:**
    o Einheiten werden in UI und Export **konsistent** angezeigt.
    o Jede Buchung speichert: verwendeten Punktwert, ggf. Modifikator, Ersteller,
       Zeitstempel (Audit).
- **Rechte & Sichtbarkeit:**
    o **Admin** : globaler Katalog; **Lehrer** : mandanten/klassengebunden; **Schüler** : nur
       eigene Historie & Korrekturanfrage.
- **UX/Barrierefreiheit:**
    o Verständliche Labels/Fehlertexte; responsive Tabellen; Tastaturbedienung;
       Kontraste gemäß Styleguide.
- **Datenintegrität:**
    o Deaktivierte Aktionen bleiben in Historie/Export sichtbar; neue Buchungen
       darauf sind verhindert.
    o Korrekturen sind **idempotent** (kein doppeltes Anwenden bei Re-Submit).