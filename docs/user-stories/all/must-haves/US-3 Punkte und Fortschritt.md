Fellegger, Dalipovic

# Card: Punkte & Fortschritt (Übersicht + Baum)

**User Story:** Als Schüler möchte ich meinen aktuellen Punktestand samt Veränderungen und
eine visuelle Baum-Fortschrittsanzeige sehen, um meinen Lernfortschritt motivierend und
nachvollziehbar darzustellen.
**Storypoints:** 8
**Priorität:** Must-Have (P1)
**Epic:** Punkte & Fortschritt

## Conversation

- Start/Konto bündelt **Gesamtpunkte** , **letzte Buchungen** (Aktionshistorie) und eine
    **Baum-Visualisierung** mit Leveln (z. B. Setzling, Jungbaum, Baum) an
    **Punkteschwellen** gekoppelt.
- Hinweise auf **Meilensteine** (z. B. 100/250/500 Punkte) und **Level-Ups** erhöhen
    Motivation.
- Prozent zum nächsten Level und **Restpunkte** werden transparent angezeigt.

## Confirmation (Akzeptanzkriterien)

**Funktional**

1. **Gesamtpunkte & Änderungen**
    1.1 Beim Öffnen der Start/Konto-Seite wird der **Gesamtpunktestand** als Zahl
    angezeigt.
    1.2 Nach einer neuen Aktion erscheint ein **Änderungs-Badge** (z. B. “+10”) bis zum
    nächsten Reload/Tab-Wechsel.
    1.3 Die Summe entspricht der Aggregation aller verbuchten Aktionen (inkl. Zeitfilter
    „Gesamt“ standardmäßig).
2. **Letzte Aktionen (Historie)**
    2.1 Es wird eine Liste **„Letzte Aktionen“** mit **mind. 10 Einträgen** angezeigt, jeweils:
    **Aktion** , **Datum** , **Menge** , **Punkte**.
    2.2 Sortierung absteigend nach Buchungszeitpunkt; Paginierung/„Mehr anzeigen“
    bei >10 Einträgen.
    2.3 Ungültige oder zurückgenommene Buchungen erscheinen **nicht** bzw. sind als
    „storniert“ klar markiert.
3. **Meilensteine & Hinweise**
    3.1 Beim erstmaligen Erreichen definierter **Meilensteine** (z. B. 100/250/500 Punkte)


Fellegger, Dalipovic

```
erscheint ein Hinweis mit Datum („Meilenstein 250 erreicht am ...“).
3.2 Bereits erreichte Meilensteine bleiben in einer kompakten Liste sichtbar.
```
4. **Baum-Fortschritt & Level**
    4.1 Die **Baum-Anzeige** zeigt ein **Level** basierend auf **konfigurierbaren**
    **Punkteschwellen** (z. B. 0/100/250/500/1000).
    4.2 Unter der Grafik werden **Prozent zum nächsten Level** sowie die **benötigten**
    **Restpunkte** numerisch angezeigt.
    4.3 Beim Überschreiten einer Schwelle erscheint ein **Level-Up-Hinweis** („Neues
    Baum-Level erreicht“).
    4.4 Ein Levelwechsel aktualisiert **sowohl** die Baum-Grafik **als auch** die numerischen
    Anzeigen konsistent.
5. **Konsistenz & Aktualität**
    5.1 Nach Verbuchen einer Aktion werden **Gesamtpunkte, Historie und Baum** in
    derselben View **synchron** aktualisiert (ohne Seitenwechsel).
    5.2 Bei parallelen Buchungen (z. B. aus anderem Gerät) wird die Anzeige spätestens
    beim nächsten Refresh/Auto-Refresh korrekt neu berechnet.

**Nicht-funktional**

- **Performance:**
    o Initiales Laden der Seite bei bis zu **50 Buchungen** < **1,5 s**.
    o Re-Render der Baum-Grafik < **1 s** (ruckelfrei auf Mobile).
- **Barrierefreiheit & Sprache:**
    o Klare, verständliche Labels; **Alt-Text** /ARIA für die Baum-Grafik; Tastatur-
       Navigation möglich.
- **Responsivität & Design:**
    o Voll funktionsfähig auf **Desktop** und **mobilen Endgeräten** (responsives
       Layout); Farben/Schriften gemäß **Styleguide**.
- **Datenkonsistenz:**
    o Keine Doppelzählung bei konkurrierenden Updates; serverseitig idempotente
       Aggregation.
- **Persistenz:**
    o Punkte, Historie, Meilenstein- und Level-Status bleiben nach
       Neustart/Neuanmeldung erhalten.


