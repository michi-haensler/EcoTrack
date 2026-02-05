# MS-01 Tasks — Backend (_server)

## Ziel
Implementierung der Auth-/Registrierungs-/Reset-Flows inkl. Keycloak-Integration und Tests, unabhängig von Frontends.

## Tasks
- API-Spezifikation finalisieren (Request/Response, Fehlercodes)
- Keycloak-Adapter/Client implementieren (Login, User anlegen, Reset, Logout)
- REST-Endpunkte implementieren:
  - POST /api/v1/auth/mobile/login
  - POST /api/v1/auth/admin/login
  - POST /api/v1/registration
  - GET /api/v1/users/me
  - POST /api/v1/auth/password/reset-request
  - POST /api/v1/auth/logout
- User-Synchronisation (Keycloak externalId ↔ App-DB)
- Admin-Login: update_password erkennen und 401 mit Flag zurückgeben
- Logout: Refresh-Token serverseitig invalidieren

## Tests
- Unit-Tests für Services/Use-Cases
- Integration-Tests für alle Endpunkte
- Mock/Container für Keycloak (z. B. Testcontainers/WireMock)

## Akzeptanzkriterien
- Alle Endpunkte gemäß Spezifikation erreichbar
- E-Mail-Verifikation wird korrekt angestoßen
- Admin-Login verlangt Passwortwechsel (update_password)
- Logout invalidiert Session in Keycloak
- Testabdeckung für alle Flows vorhanden
