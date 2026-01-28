Karner, Radlinger, Hänsler

# Card: Registrierung & Login

**User Story:** Als Nutzer möchte ich mich registrieren und sicher anmelden können, um
EcoTrack personalisiert zu nutzen.
**Storypoints:** 8
**Priorität:** Must-Have (P1)
**Epic:** Onboarding & Auth

## Conversation

- Rollen: Schüler, Lehrer, Admin.
- Registrierung durch Einladungs-/Schul-E-Mail; Schüler werden ihrer Klasse
    zugeordnet.
- Sichere Session, Logout, Fehlermeldungen klar.

## Confirmation

**Funktional**

1. **Registrieren** : Felder Name, E-Mail, Passwort (Pflicht, mind. 8 Zeichen), Rolle
    (vorgegeben durch Einladung) / Klasse (bei Schülern Pflicht).
2. Validierung: Format E-Mail, Passwort-Regeln, Klassenpflicht. Fehler inline.
3. **Login** : E-Mail + Passwort → gültig ⇒ Session erstellt; ungültig ⇒ „E-Mail oder
    Passwort falsch“.
4. **Logout** : beendet Session; Rückkehr auf Login.
5. Gesperrte/archivierte Nutzer können sich nicht anmelden (Hinweis).

**Nicht-funktional**

- Login-Antwort < **1,5 s**.
- **Rate-Limit** : max. 5 Fehlversuche/5 min → Captcha/Hinweis.
- **Sitzung** : Timeout nach Inaktivität (z. B. 30 min).
- Barrierefreiheit: Form-Labels, Tastatur-Nutzung, Kontrast AA.
- Daten verschlüsselt in Transit (TLS).