# User Story: Keycloak Authentication Setup

## Beschreibung

**Als** System-Administrator  
**möchte ich** Keycloak als zentralen Identity Provider konfigurieren und die Client-Credentials für Backend, Admin-Web und Mobile App verwalten  
**damit** eine sichere, standardisierte Authentifizierung über OAuth2/OIDC für alle EcoTrack-Plattformen bereitgestellt wird

## Akzeptanzkriterien

### AC1: Keycloak Realm-Konfiguration
- [ ] **GIVEN** Keycloak läuft lokal via Docker  
      **WHEN** Realm "ecotrack" wird erstellt  
      **THEN** Realm enthält vordefinierte Rollen (STUDENT, TEACHER, ADMIN)  
      **AND** Realm-Settings sind exportierbar als JSON

### AC2: Client-Konfiguration für Backend
- [ ] **GIVEN** Realm "ecotrack" existiert  
      **WHEN** Client "ecotrack-backend" wird erstellt  
      **THEN** Client verwendet "confidential" Access Type  
      **AND** Service Account ist aktiviert  
      **AND** Client-Credentials (Client ID + Secret) sind verfügbar  
      **AND** Valid Redirect URIs sind konfiguriert: `http://localhost:8080/*`

### AC3: Client-Konfiguration für Admin-Web
- [ ] **GIVEN** Realm "ecotrack" existiert  
      **WHEN** Client "ecotrack-admin-web" wird erstellt  
      **THEN** Client verwendet "public" Access Type (SPA)  
      **AND** PKCE (Proof Key for Code Exchange) ist aktiviert  
      **AND** Valid Redirect URIs: `http://localhost:5173/*`, `http://localhost:5173/callback`  
      **AND** Web Origins: `http://localhost:5173`

### AC4: Client-Konfiguration für Mobile App
- [ ] **GIVEN** Realm "ecotrack" existiert  
      **WHEN** Client "ecotrack-mobile" wird erstellt  
      **THEN** Client verwendet "public" Access Type  
      **AND** PKCE ist aktiviert  
      **AND** Valid Redirect URIs: `ecotrack://callback`  
      **AND** Standard Scopes: openid, profile, email

### AC5: Realm Export & Import
- [ ] **GIVEN** Realm ist vollständig konfiguriert  
      **WHEN** Export über Keycloak Admin Console  
      **THEN** JSON-Datei enthält alle Clients, Rollen, Settings  
      **AND** JSON ist versioniert im Repository (`shared-resources/keycloak/realm-export.json`)  
      **AND** Import funktioniert ohne Fehler

### AC6: Client-Credentials Management
- [ ] **GIVEN** Clients sind erstellt  
      **WHEN** Credentials werden exportiert  
      **THEN** `.env.example` Templates existieren für Backend, Admin-Web, Mobile  
      **AND** Secrets werden NICHT im Repository committed  
      **AND** Dokumentation erklärt, wie Credentials lokal gesetzt werden

### AC7: Docker-Compose Integration
- [ ] **GIVEN** Keycloak-Service in `docker-compose.yml`  
      **WHEN** `docker-compose up` wird ausgeführt  
      **THEN** Keycloak startet auf Port 8180  
      **AND** Admin-Credentials sind über Environment-Variablen gesetzt  
      **AND** Realm-Import passiert automatisch beim ersten Start

### AC8: Fehlerbehandlung
- [ ] **GIVEN** Falscher Client Secret  
      **WHEN** Backend versucht Token zu erhalten  
      **THEN** 401 Unauthorized Response  
      **AND** Fehlermeldung loggt "Invalid Client Credentials"

- [ ] **GIVEN** Keycloak ist offline  
      **WHEN** Login-Versuch  
      **THEN** User erhält "Service temporarily unavailable"  
      **AND** Retry-Mechanismus nach 5 Sekunden

## Bounded Context

- [x] Administration (Keycloak Integration, User Management)
- [x] Backend (OAuth2 Resource Server)
- [x] Admin-Web (OAuth2 Client)
- [x] Mobile (OAuth2 Client)

## Plattform

- [x] Mobile (React Native)
- [x] Admin-Web (React + Vite)
- [x] Backend (Spring Boot)

## Story Points

**Schätzung**: 5

**Begründung**: 
- Keycloak-Konfiguration ist bekannt, aber detailliert (Clients, Rollen, Scopes)
- Realm Export/Import ist straightforward
- Docker-Integration erfordert Testing
- Environment-Setup für 3 Plattformen
- Dokumentation ist umfangreich
- Kein komplexer Code, primär Konfiguration

## Abhängigkeiten

- [ ] Abhängig von: Keine (Foundation)
- [ ] Blockiert: #002 - User Registration Backend Implementation
- [ ] Blockiert: #003 - Login Flow Mobile App
- [ ] Blockiert: #004 - Admin-Web Authentication

## Technische Notizen

### Keycloak-Setup

**Realm-Konfiguration:**
- Realm Name: `ecotrack`
- Display Name: `EcoTrack - Nachhaltigkeits-App für Schulen`
- Email als Username aktivieren
- User Registration deaktiviert (nur Admin/Lehrer erstellen Accounts)
- Email-Verification aktiviert
- Password Policy: min. 8 Zeichen, 1 Großbuchstabe, 1 Zahl, 1 Sonderzeichen

**Rollen:**
- `STUDENT` - Standard-Rolle für Schüler:innen
- `TEACHER` - Lehrkräfte (Admin-Web Zugriff)
- `ADMIN` - System-Administratoren (volle Rechte)

**Token-Settings:**
- Access Token Lifespan: 15 Minuten
- SSO Session Idle: 30 Minuten
- SSO Session Max: 7 Tage (bei "Remember Me")
- Refresh Token: 7 Tage

### Backend (Spring Boot)

**Dependencies:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

**application.yml Struktur:**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI}
      client:
        registration:
          keycloak:
            client-id: ${KEYCLOAK_CLIENT_ID}
            client-secret: ${KEYCLOAK_CLIENT_SECRET}
            scope: openid, profile, email
        provider:
          keycloak:
            issuer-uri: ${KEYCLOAK_ISSUER_URI}
```

### Admin-Web (React)

**Environment Variables:**
```
VITE_KEYCLOAK_URL=http://localhost:8180
VITE_KEYCLOAK_REALM=ecotrack
VITE_KEYCLOAK_CLIENT_ID=ecotrack-admin-web
```

**OAuth2 Library:** `@react-keycloak/web` oder `oidc-client-ts`

### Mobile (React Native)

**Environment Variables:**
```
KEYCLOAK_URL=http://localhost:8180
KEYCLOAK_REALM=ecotrack
KEYCLOAK_CLIENT_ID=ecotrack-mobile
```

**OAuth2 Library:** `react-native-app-auth`

### Docker-Compose

**Keycloak Service:**
```yaml
keycloak:
  image: quay.io/keycloak/keycloak:23.0
  environment:
    KEYCLOAK_ADMIN: admin
    KEYCLOAK_ADMIN_PASSWORD: ${KEYCLOAK_ADMIN_PASSWORD}
    KC_DB: postgres
    KC_DB_URL: jdbc:postgresql://postgres:5432/keycloak
  command: start-dev --import-realm
  volumes:
    - ./shared-resources/keycloak/realm-export.json:/opt/keycloak/data/import/realm-export.json
  ports:
    - "8180:8080"
```

## Design/UX

**Keine UI-Änderungen:** Reine Backend/Infrastruktur-Story

**Keycloak Admin Console:**
- URL: http://localhost:8180/admin
- Realm: ecotrack
- Admin-Login mit Credentials aus `.env`

## Definition of Done

- [x] Keycloak läuft via Docker-Compose
- [x] Realm "ecotrack" exportiert als JSON
- [x] 3 Clients konfiguriert (Backend, Admin-Web, Mobile)
- [x] Rollen (STUDENT, TEACHER, ADMIN) erstellt
- [x] `.env.example` Templates für alle Plattformen
- [x] Dokumentation:
  - Setup-Guide (Ersteinrichtung)
  - Keycloak-Admin-Anleitung
  - Client-Credentials-Management
  - Troubleshooting
- [x] Docker-Compose tested (Start, Stop, Realm-Import)
- [x] Realm-Import funktioniert fehlerfrei
- [x] Code Review (Config-Files, Docker-Setup)
- [x] Keine Secrets im Repository

## Notizen

**Security Best Practices:**
- Secrets niemals committen (`.gitignore` prüfen)
- Production: Eigene Keycloak-Instanz (managed service)
- HTTPS für Production (TLS-Zertifikate)
- Regelmäßige Keycloak-Updates (Security Patches)

**Nächste Schritte nach dieser Story:**
1. Backend Security Configuration (JWT-Validierung, Role-Mapping)
2. Admin-Web Login-Flow (OAuth2 Authorization Code Flow mit PKCE)
3. Mobile App Login-Flow (OAuth2 mit Deep Links)
4. User Registration Backend (Keycloak Admin API Integration)

**Keycloak-Versionen:**
- Development: Keycloak 23.0 (latest stable)
- Production: Keycloak 23.0+ (managed oder self-hosted)
