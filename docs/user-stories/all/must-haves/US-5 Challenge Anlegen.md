Karner, Radlinger, Hänsler

```
Card Challenge anlegen
```
```
Als Lehrer möchte ich eine neue Umwelt-Challenge für meine Klasse anlegen
um das Umweltbewusstsein der Schülerinnen und Schüler durch spielerische
Wettbewerbe zu fördern.
```
```
Storypoints: 14
Priorität: Must Have
Epic: Challenges und Klassensteuerung
```
```
Conversation (Konversation / Hintergrund)
```
- Die Lehrkraft wollen Challenges (z. B. „Plastikfreie Woche“) mit Titel,
    Beschreibung, Ziel und Zeitraum erstellen können.
- Jede Challenge soll einer Klasse zugeordnet werden.
- Schüler können dann Aktionen zu dieser Challenge beitragen (z. B.
    „Pfandflaschen gesammelt“).
- Der Fortschritt der Klasse soll automatisch berechnet werden (z. B. „75 %
    des Ziels erreicht“).
- Nach Ablauf soll die Challenge als _abgeschlossen_ markiert und im
    Dashboard archiviert werden.
- Das System soll Lehrkräfte informieren, wenn eine Challenge endet oder
    das Ziel erreicht ist.
- Lehrer sollen abgeschlossene Challenges duplizieren können, um sie
    wiederzuverwenden.


Karner, Radlinger, Hänsler

```
Confirmation (Akzeptanzkriterien)
```
```
Funktionale Kriterien:
```
1. 1 Der Lehrer kann über das Menü „Neue Challenge“ ein Formular öffnen.
2. 1 Das Formular enthält Felder für Titel (Pflichtfeld), Beschreibung
    (optional), Zielwert (Pflichtfeld, numerisch) und Zeitraum (Start- und
    Enddatum, Pflichtfelder).
3. 1 Wird ein Pflichtfeld leer gelassen oder ein ungültiger Wert eingegeben,
    erscheint eine Fehlermeldung mit klarer Erklärung.
4. 2 Nach dem Speichern wird die Challenge in der Challenge-Übersicht
    angezeigt.
5. 2 Jede Challenge ist mit der Klasse des Lehrers verknüpft.
6. 2 Schüler dieser Klasse sehen die Challenge automatisch in ihrer
    Übersicht.
7. 2 Während des Challenge-Zeitraums wird der Fortschritt dynamisch
    anhand der Schüleraktionen berechnet.
8. 1 Der Fortschritt wird in Prozent angezeigt (Formel: erreichte Punkte /
    Zielpunkte × 100).
9. 1 Nach Ablauf des Enddatums wird die Challenge automatisch in den
    Status _abgeschlossen_ gesetzt.
10. 1 Abgeschlossene Challenges sind weiterhin im Lehrer-Dashboard unter
    „Archiv“ sichtbar.

```
Nicht-funktionale Kriterien:
```
1. Die Eingabemaske lädt in unter 3 Sekunden.
2. Alle Texte (Labels, Buttons, Fehlermeldungen) sind in einfacher,
    verständlicher Sprache gehalten.
3. Das Design folgt dem bestehenden UI-Stil (Farben, Schriften, Button-
    Größen).
4. Der Prozess ist sowohl am Desktop als auch auf mobilen Endgeräten
    vollständig nutzbar (responsives Layout).
5. Das System prüft auf doppelte Challenge-Titel innerhalb derselben Klasse
    und warnt bei Duplikaten.
6. Bei erfolgreichem Anlegen erscheint eine grüne Bestätigungsmeldung
    „Challenge erfolgreich erstellt“.
7. Daten werden persistent gespeichert und sind nach einem Neustart des
    Systems weiterhin verfügbar.


