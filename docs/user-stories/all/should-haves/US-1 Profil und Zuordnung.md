```
Kovacs, Grigic
```
# Card: Profil & Zuordnung (Klasse/Schule)

**User Story:** Als Nutzer möchte ich mein Profil mit Klasse/Schule verwalten, damit meine
Beiträge korrekt zugeordnet und in Ranglisten/Challenges berücksichtigt werden.
**Storypoints:** 5
**Priorität:** Should-Have (P2)
**Epic:** Onboarding & Auth

## Conversation

- Nutzer sehen Profilfelder: Name, Klasse, Schule (schreibgeschützt oder editierbar je
    Rolle).
- Schüler können Klassenwechsel als **Anfrage** stellen; Lehrer/Admin bestätigen.
- Datenschutz: Anzeige von Namen/Initialen steuerbar.

## Confirmation

**Funktional**

1. Profilseite zeigt Name, E-Mail, Rolle, Klasse, Schule; Schüler können **„Klassenwechsel**
    **anfragen“**.
2. Klassenwechsel erzeugt **Workflow** : Eintrag „offen“ → Lehrer/Admin kann
    **annehmen/ablehnen** (Kommentar optional).
3. Nach Annahme werden **Ranglisten/Challenges** automatisch auf neue Klasse
    umgestellt (historische Punkte bleiben dem Nutzer erhalten; Aggregation wechselt
    ab Änderungsdatum).
4. Nutzer kann Einstellung **„In Ranglisten anonym (Initialen)“** toggeln.

**Nicht-funktional**

- Laden < 1,5 s; Update < 2 s.
- Rollenlogik strikt: Nur autorisierte Rollen dürfen Klasse/Schule festlegen.
- Audit (alter/neuer Klassenwert, Entscheider, Zeitstempel).