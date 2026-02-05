# MS-01 Tasks — Admin Web (_admin-web)

## Ziel
Admin-Login/Logout inkl. Redirects und Fehlerbehandlung, unabhängig vom echten Backend.

## Tasks
- Mock-API-Service erstellen (auth/admin/login, auth/logout)
- Login-UI anbinden (Form, Loading, Error-States)
- update_password-Handling → Redirect zur Keycloak-UI (konfigurierbar)
- Logout-Flow (Token löschen, Redirect Login)
- Fehlerzustände (401/403/temporäres Passwort) sauber abbilden

## Tests
- Component-Tests (Login: success, invalid, update_password)
- API-Mock-Tests für Service-Layer

## Akzeptanzkriterien
- Login funktioniert mit Mock-API
- update_password führt zum Redirect
- Logout bereinigt Tokens und navigiert korrekt
- Tests decken Happy/Fail-Pfade ab
