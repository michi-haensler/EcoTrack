Karner,Radlingmaier,Hänsler

# Card: Aktion erfassen (manuell)

**User Story:** Als Schüler möchte ich nachhaltige Aktionen manuell erfassen, um Punkte zu
sammeln.
**Storypoints:** 13
**Priorität:** Must-Have (P1)
**Epic:** Aktionen & Scoring

## Conversation

- Vordefinierter Aktionskatalog (z. B. Fahrrad statt Auto, Mehrwegbecher, Papier
    gesammelt) mit Punktwerten.
- Schüler wählen Aktion, Menge, Datum (default heute); speichern → Punkte
    gutschreiben; Historie sichtbar.
- Leichte Plausibilitätsprüfungen gegen Spam.

## Confirmation

**Funktional**

1. Ansicht „Aktion erfassen“ zeigt **Katalog nach Kategorien** + Suchfeld.
2. Auswahl Aktion ⇒ Formular: Menge (Pflicht, ganzzahlig >0), Datum (Pflicht, ≤ heute),
    optional Notiz.
3. Ungültige Eingaben (z. B. 0, negativ, Datum in Zukunft) → Fehlermeldung und kein
    Speichern.
4. Speichern erzeugt **Buchung** : Aktion, Menge, Punkte = Menge × Aktions-Punktwert,
    Datum, User.
5. **Punkte** werden sofort dem Nutzerkonto gutgeschrieben; **Punkteübersicht**
    aktualisiert sich.
6. **Historie** zeigt neuen Eintrag (Zeit, Aktion, Menge, Punkte); sortiert absteigend.
7. Optionaler „Rückgängig machen“-Button bis 60 s nach Erfassung; danach nur via
    Lehrer/Admin.
8. Bei laufender Challenge der Klasse fließt die Buchung automatisch in deren
    Fortschritt ein.

**Nicht-funktional**

- Speichern < **2 s**.
- **Duplikat-Hinweis** bei identischer Aktion+Menge+Datum innerhalb 5 min
    („Doppelteingabe?“).


Karner,Radlingmaier,Hänsler

- Responsiv; klare, einfache Texte; konsistentes UI.
- Persistenz/Audit (Wer/Wann/Was).


