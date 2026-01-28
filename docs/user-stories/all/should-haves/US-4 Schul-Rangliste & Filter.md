Fellegger, Dalipovic

# Card: Schul-Rangliste & Filter

**User Story:** Als Schüler/Lehrer möchte ich schulweite Ranglisten mit Filtern sehen, um
Vergleichbarkeit über Klassen/Zeitfenster herzustellen.
**Storypoints:** 8
**Priorität:** Should-Have (P2)
**Epic:** Ranglisten & Wettbewerb

## Conversation

- Ansichten: „Klassenvergleich“, „Top-Schüler (opt-in/Initialen)“, Zeitfenster
    (Woche/Monat/Gesamt).

## Confirmation

**Funktional**

1. **Klassenranking** : Liste aller Klassen der Schule, sortiert nach Gesamtpunkten
    (Zeitraum-Filter).
2. **Top-Schüler** (nur, wenn Datenschutz-Setting erlaubt): Name oder Initialen; eigene
    Position stets sichtbar.
3. **Zeitraum-Filter** wirkt auf beide Ansichten; Aggregation korrekt.
4. Gleichstand → gemeinsame Platzierung + Hinweis.

**Nicht-funktional**

- Laden < 2,5 s bei 50 Klassen/1.000 Schülern (Server-Aggregation, Caching).
- Datenschutzschalter („nur Initialen“) strikt durchgesetzt.


