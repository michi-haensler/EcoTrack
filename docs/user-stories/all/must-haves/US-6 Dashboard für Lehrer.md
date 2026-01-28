Karner, Radlinger, Hänsler

# US-06 – Dashboard für Lehrer

```
Story Points: 10
Priorität: Hoch
Epic: Lehrer-Verwaltung & Monitoring
```
## Card (Karte)

```
Als Lehrer
möchte ich ein Dashboard sehen, das mir die Fortschritte meiner Schüler und Klassen
anzeigt,
um die Leistungen, Aktivitäten und den Fortschritt meiner Klasse besser überwachen und
gezielt fördern zu können.
```
## Conversation (Konversation / Hintergrund)

```
Lehrkräfte sollen über ein Dashboard einen schnellen Überblick über die Aktivitäten und den
Fortschritt ihrer Schüler erhalten.
Das Dashboard ist die zentrale Anlaufstelle zur Auswertung von Aktionen, Punkten und
Challenges in einer Klasse.
```
- Beim Öffnen des Dashboards wird eine **Klassenübersicht** angezeigt, aus der die
    Lehrkraft eine ihrer Klassen auswählen kann.
- Wird eine Klasse ausgewählt, zeigt das System eine **Summenübersicht** mit:
    o Anzahl aller Aktionen der Klasse
    o Gesamtpunkte
    o Durchschnittspunkte pro Schüler
- Im Reiter **„Schüler“** wird eine **Tabelle** angezeigt, in der die einzelnen Schüler mit
    ihren Namen, Anzahl der Aktionen und erreichten Punkten gelistet sind.
- Wenn eine **Challenge läuft** , wird im Dashboard zusätzlich
    ein **Fortschrittsbalken** angezeigt, der den Stand der Klasse in Prozent zum Ziel
    darstellt.
- Das Dashboard soll **Filteroptionen** (z. B. Zeitraum, Klasse) und eine einfache
    Sortierung (nach Name, Punkten, Aktionen) bieten.
- Die Oberfläche soll übersichtlich, klar strukturiert und responsiv gestaltet sein
    (optimiert für Desktop und Tablet).
- Ziel ist, dass Lehrkräfte auf einen Blick erkennen, wie aktiv ihre Schüler sind und wo
    eventuell Handlungsbedarf besteht.

## Confirmation (Akzeptanzkriterien)


Karner, Radlinger, Hänsler

```
Funktionale Kriterien
```
```
Nr. Kriterium
1 Der Lehrer kann das Dashboard nach dem Login über das Hauptmenü öffnen.
```
```
2
Wird eine Klasse ausgewählt, zeigt das System die Summe aller Aktionen und Punkte
dieser Klasse an.
```
```
3
Sind mehrere Schüler vorhanden, kann der Lehrer im Tab „Schüler“ eine Tabelle mit
Schülernamen, Anzahl der Aktionen und Punkten sehen.
```
```
4
Läuft aktuell eine Challenge, wird auf dem Dashboard ein Fortschrittsbalken angezeigt,
der den aktuellen Fortschritt in Prozent wiedergibt.
```
```
5 Der Lehrer kann nach Zeitraum oder Klasse filtern, und die Daten werden automatisch
aktualisiert.
```
```
6
Klickt der Lehrer auf einen Schülernamen, öffnet sich eine Detailansicht mit den
individuellen Aktionen und Punkten des Schülers.
7 Bei Fehlern (z. B. keine Daten gefunden) wird eine verständliche Meldung angezeigt.
```
```
8 Änderungen an Aktionen oder Punkten werden nach Neuladen automatisch im
Dashboard übernommen.
```
```
Nicht-funktionale Kriterien
```
```
Nr. Kriterium
1 Das Dashboard lädt in unter 3 Sekunden bei normaler Netzverbindung.
2 Das Design entspricht dem bestehenden UI-Styleguide (Farben, Schriften, Layout).
3 Alle Texte und Fehlermeldungen sind in klarer, verständlicher Sprache formuliert.
4 Die Oberfläche ist responsiv und funktioniert auf Desktop- und Tablet-Geräten.
```
```
5
Die Daten werden live oder periodisch aktualisiert , ohne dass der Lehrer manuell neu
laden muss.
```
```
6
Das Dashboard bleibt nach einem Systemneustart persistent verfügbar (gespeicherte
Einstellungen, Filter).
7 Datenschutzkonformität: Es werden nur die Schüler der eigenen Klasse angezeigt.
```