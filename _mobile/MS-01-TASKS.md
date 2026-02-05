# MS-01 Tasks — Mobile (_mobile)

## Ziel
Registrierung/Login/Reset/Logout inkl. Deep-Link, unabhängig vom echten Backend.

## Tasks
- Mock-API-Service erstellen (registration, auth/mobile/login, auth/password/reset-request, auth/logout)
- Registrierung + Login UI anbinden
- Passwort-Reset-Request UI + Status-Feedback
- Deep-Link Handler für Reset-Rückleitung
- Logout-Flow (SecureStorage löschen)

## Tests
- Component-Tests für alle Auth-Screens
- API-Mock-Tests für Service-Layer

## Akzeptanzkriterien
- Registrierung/Login/Reset funktionieren mit Mock-API
- Deep-Link Handling vorhanden
- Logout bereinigt Tokens
- Tests decken Happy/Fail-Pfade ab
