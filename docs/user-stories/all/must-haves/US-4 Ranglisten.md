Fellegger, Dalipovic

# Card: Rangliste (Klasse)

**User Story:** Als Schüler möchte ich meine Platzierung innerhalb meiner Klasse sehen, um
mich zu vergleichen und motiviert zu bleiben.
**Storypoints:** 8
**Priorität:** Must-Have (P1)
**Epic:** Ranglisten & Wettbewerb

## Conversation

- Klassenweite Rangliste, eigene Position hervorgehoben; Gleichstände korrekt;
    Aktualisierung zeitnah.

## Confirmation

**Funktional**

1. Rangliste zeigt **alle Schüler der Klasse** , sortiert nach Punkten, mit Rang-Nummer,
    Name (oder Initialen, je nach Datenschutz), Punkten.
2. **Eigene Zeile** stets sichtbar (sticky/hervorgehoben), auch bei Pagination.
3. **Gleichstand** : gleiche Rangnummer + Hinweis „geteilte Platzierung“.
4. Aktualität: Änderungen durch neue Aktionen sind **<30 s** nach Speichern sichtbar.
5. Filter Zeitraum: „Gesamt“, „Dieser Monat“, „Diese Woche“.

**Nicht-funktional**

- Laden < **2 s** bei 100 Schülern.
- Datenschutzoption: Anzeige mit Initialen bei aktivierter Anonymisierung.
- Responsiv; Tastatur-Navigation möglich.