Kovacs & Grigic

# Card: Schul-/Gemeinde-Dashboards (Trends)

**User Story:** Als Verwaltung/Lehrer möchte ich Trends und Top-Kategorien sehen, um
Maßnahmen abzuleiten und Erfolge zu kommunizieren.
**Storypoints:** 8
**Priorität:** Should-Have (P2)
**Epic:** Dashboard & Auswertung

## Conversation

```
Diagramme: Punkte/Monat, Top-Kategorien, aktive Klassen.
Vergleich Zeiträume (z. B. letzter Monat vs. aktuell).
```
## Confirmation

**Funktional**

```
Diagramm „Punkte pro Monat“ (12 Monate, fehlende Monate = 0).
Top-Kategorien (Top-5) nach Punkten im gewählten Zeitraum.
Vergleich zweier Zeiträume (Delta/Prozent).
Detail-Drilldown von Schule → Klasse (berechtigte Rollen).
```
**Nicht-funktional**

```
Render < 2 s (Server-Aggregation + Caching).
Farblegende barrierearm, Tooltips Screenreader-kompatibel.
Exportfähig (PNG/SVG Screenshot-freundlich).
```

