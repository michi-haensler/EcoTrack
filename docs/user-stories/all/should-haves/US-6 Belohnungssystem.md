Kovacs & Grigic

# Card: Belohnungsshop (Einlösen ab Schwelle)

**User Story:** Als Schüler möchte ich Belohnungen ab einer Punkteschwelle einlösen, um
zusätzliche Motivation zu haben.
**Storypoints:** 8
**Priorität:** Should-Have (P2)
**Epic:** Belohnungen

## Conversation

- Katalog mit Rewards (Titel, Beschreibung, Schwelle, begrenzter Bestand).
- Einlösen zieht Punkte ab; Beleg (Voucher-Code/Bestätigung).

## Confirmation

**Funktional**

1. Reward-Katalog zeigt **verfügbare** Belohnungen (Schwelle, Restbestand).
2. **Einlösen** nur möglich, wenn Punktestand ≥ Schwelle **und** Bestand > 0.
3. Nach Einlösen: Punkte-Abzug, Bestand –1, **Beleg** (Code oder Bestätigung) angezeigt
    & im Profil gespeichert.
4. Bei nicht genügend Punkten → klare Meldung; bei 0 Bestand → ausgegraut.

**Nicht-funktional**

- Transaktion ist **atomar** (kein doppeltes Einlösen bei Parallelklick).
- Laden/Einlösen < 2 s.
- Audit (wer/was/wann), manipulationssichere Codes (mind. 10 zufällige Zeichen).


