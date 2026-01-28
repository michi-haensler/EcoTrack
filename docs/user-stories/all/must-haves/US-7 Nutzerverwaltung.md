Karner, Radlinger, Hänsler

# Card: Nutzerverwaltung (Admin)

**User Story:** Als Administrator möchte ich Nutzer und Rollen verwalten, um den sicheren
Betrieb zu gewährleisten.
**Storypoints:** 8
**Priorität:** Must-Have (P1)
**Epic:** Administration

## Conversation

- Admin legt Nutzer an, weist Rollen & Klassen zu; sperren/aktivieren; E-Mail
    eindeutig.

## Confirmation

**Funktional**

1. **Nutzer anlegen** : Felder Name (Pflicht), E-Mail (Pflicht, eindeutig), Rolle
    (Schüler/Lehrer/Admin, Pflicht), Klasse (Pflicht bei Schüler/Lehrer).
2. **Validierung** : E-Mail-Format; **Duplikat** derselben E-Mail blockiert mit Fehlermeldung.
3. **Bearbeiten** : Rolle/Klasse ändern; Speicherung mit Bestätigung.
4. **Sperren/Aktivieren** : Gesperrte Nutzer können sich nicht anmelden; Status sichtbar
    in Liste.
5. **Listenansicht** mit Suche/Filter (Rolle, Klasse, Status).

**Nicht-funktional**

- Anlage/Bearbeitung < **2 s**.
- Audit-Trail (Ersteller, Zeit, Änderungen).
- Rollenrechte strikt durchgesetzt (RBAC); nur Admin sieht Nutzerverwaltung.


