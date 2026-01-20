# ADR-001: Keycloak als zentraler Identity Provider für EcoTrack

**Status**: Akzeptiert

**Datum**: 2026-01-20

**Autor**: Software Architect (EcoTrack Team)

---

## Kontext

EcoTrack benötigt eine sichere, skalierbare Authentifizierungs- und Autorisierungslösung für drei unterschiedliche Plattformen:

1. **Backend API (Spring Boot)**: Resource Server, der JWT-Tokens validiert
2. **Admin-Web (React SPA)**: Lehrer:innen und Admins verwalten Challenges, Nutzer, Aktionen
3. **Mobile App (React Native)**: Schüler:innen loggen Aktivitäten, nehmen an Challenges teil

**Treibende Faktoren:**
- **Security**: OAuth2/OIDC Standard für sichere Authentifizierung
- **SSO (Single Sign-On)**: Ein Login für alle Plattformen
- **Rollenbasierte Autorisierung**: STUDENT, TEACHER, ADMIN mit unterschiedlichen Berechtigungen
- **Skalierbarkeit**: Vorbereitung für Multi-Tenant (mehrere Schulen/Organisationen)
- **Compliance**: DSGVO-Konformität, Datenschutz für Schulumgebung
- **Developer Experience**: Standardisierte Integration, weniger Custom-Code

**Einschränkungen:**
- Budget: Open-Source-Lösung bevorzugt (Schulprojekt)
- Technologie: Muss mit Spring Boot (Backend) und React/React Native (Frontend) kompatibel sein
- Deployment: Self-Hosted möglich (Docker-Compose für Development)
- Expertise: Team hat Grundkenntnisse in OAuth2, aber keine Deep Expertise in Identity Management

**Problem:**
Wie implementieren wir eine sichere, wartbare und standardkonforme Authentifizierungslösung ohne signifikanten Entwicklungsaufwand?

---

## Entscheidung

**Wir verwenden Keycloak als zentralen Identity Provider (IdP) für alle EcoTrack-Plattformen.**

### Implementierungsdetails:

#### 1. Keycloak-Setup
- **Deployment**: Docker-Container (Development: `docker-compose.yml`)
- **Version**: Keycloak 23.0 (latest stable)
- **Realm**: `ecotrack` (isolierte Konfiguration)
- **Database**: PostgreSQL für Session-Storage und User-Daten

#### 2. Client-Konfiguration

**Backend (ecotrack-backend):**
- **Access Type**: Confidential (Client Secret)
- **Flow**: Service Account für M2M, Authorization Code für User-Aktionen
- **Zweck**: JWT-Validierung, Keycloak Admin API Zugriff

**Admin-Web (ecotrack-admin-web):**
- **Access Type**: Public (Single Page Application)
- **Flow**: Authorization Code mit PKCE (Proof Key for Code Exchange)
- **Redirect URIs**: `http://localhost:5173/callback`, Production-URL

**Mobile (ecotrack-mobile):**
- **Access Type**: Public
- **Flow**: Authorization Code mit PKCE
- **Redirect URIs**: `ecotrack://callback` (Deep Link)

#### 3. Rollenmodell

**Realm-Rollen:**
- `STUDENT`: Standard-Nutzer, Mobile App Zugriff
- `TEACHER`: Lehrer:innen, Admin-Web Zugriff (eingeschränkt)
- `ADMIN`: System-Administratoren, volle Rechte

**Composite Roles:**
- `TEACHER` inherits `STUDENT` (Lehrer können auch Mobile App nutzen)
- `ADMIN` inherits `TEACHER` + zusätzliche Admin-Rechte

#### 4. Token-Konfiguration
- **Access Token Lifespan**: 15 Minuten
- **Refresh Token**: 7 Tage (bei "Remember Me")
- **SSO Session Idle**: 30 Minuten
- **SSO Session Max**: 7 Tage

#### 5. Integration

**Spring Boot Backend:**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8180/realms/ecotrack
```

**React Admin-Web:**
```typescript
// Keycloak JS Adapter oder oidc-client-ts
const keycloak = new Keycloak({
  url: 'http://localhost:8180',
  realm: 'ecotrack',
  clientId: 'ecotrack-admin-web'
});
```

**React Native Mobile:**
```typescript
// react-native-app-auth
const config = {
  issuer: 'http://localhost:8180/realms/ecotrack',
  clientId: 'ecotrack-mobile',
  redirectUrl: 'ecotrack://callback',
  scopes: ['openid', 'profile', 'email']
};
```

---

## Begründung

**Warum Keycloak?**

### 1. **Standard-Konformität**
- Vollständige OAuth2 und OpenID Connect (OIDC) Implementierung
- Kein Custom-Code für Token-Generierung, -Validierung, -Refresh
- Industrie-Standard, gut dokumentiert

### 2. **Feature-Reichtum**
- User Federation (LDAP, Active Directory) für spätere Schul-IT-Integration
- Social Login (Google, Microsoft) als optionale Erweiterung
- Two-Factor Authentication (2FA) out-of-the-box
- Email-Verification, Password-Reset-Flows inklusive

### 3. **Multi-Client-Support**
- Ein Keycloak-Realm für alle Plattformen (Backend, Web, Mobile)
- Zentrale Rollenverwaltung
- Single Sign-On (SSO) zwischen Plattformen

### 4. **Developer Experience**
- Spring Security OAuth2 Integration (First-Class Support)
- React/React Native Libraries verfügbar
- Admin Console für Konfiguration (kein Code-Deployment für Änderungen)

### 5. **Skalierbarkeit & Multi-Tenancy**
- Realms ermöglichen Multi-Tenant-Architektur (eine Schule = ein Realm)
- Clustering-Support für High Availability (Production)
- Performance: 1000+ Requests/Sekunde pro Node

### 6. **Open Source & Kostenfrei**
- Apache 2.0 Lizenz
- Keine Lizenzkosten
- Self-Hosted möglich (wichtig für Schulen mit Datenschutz-Anforderungen)

### 7. **Security Best Practices**
- Regelmäßige Security-Updates
- OWASP-konforme Implementierung
- Audit-Logs für Compliance

---

## Konsequenzen

### Positiv

- ✅ **Reduzierter Entwicklungsaufwand**: Keine Custom-Auth-Implementierung (spart 2-3 Wochen)
- ✅ **Security by Default**: Bewährte OAuth2/OIDC-Implementierung, geringeres Security-Risiko
- ✅ **Zentrales Identity Management**: Ein System für User, Rollen, Sessions
- ✅ **SSO**: Nahtlose Anmeldung über alle Plattformen
- ✅ **Erweiterbarkeit**: 2FA, Social Login, User Federation später einfach aktivierbar
- ✅ **DSGVO-Konformität**: User-Daten bleiben in eigenem System (Self-Hosted)
- ✅ **Admin-Freundlich**: GUI für User-Management, keine SQL-Queries nötig
- ✅ **Testbarkeit**: Keycloak Testcontainers für Integration Tests

### Negativ

- ❌ **Zusätzliche Infrastruktur-Komponente**: Keycloak + PostgreSQL müssen deployed werden
- ❌ **Lernkurve**: Team muss OAuth2/OIDC und Keycloak-Konzepte lernen
- ❌ **Abhängigkeit**: Wenn Keycloak down ist, funktioniert keine Authentifizierung
- ❌ **Resource Overhead**: Keycloak benötigt ~512MB RAM (Development), ~1-2GB (Production)
- ❌ **Komplexität**: Keycloak-Konfiguration ist umfangreich (Clients, Realms, Flows)
- ❌ **Overhead für kleine Deployments**: Für 10-20 User ist Keycloak "Overkill"

### Risiken

- ⚠️ **Single Point of Failure**: 
  - **Mitigation**: Keycloak-Clustering für Production, Health Checks, Monitoring
  
- ⚠️ **Performance-Bottleneck**: 
  - **Mitigation**: Token-Caching im Backend (Spring Security), kurze Access Token Lifetimes
  
- ⚠️ **Upgrade-Komplexität**: 
  - **Mitigation**: Versionierte Realm-Exports, Staging-Environment für Tests
  
- ⚠️ **Vendor Lock-In (Red Hat)**: 
  - **Mitigation**: Keycloak ist Open Source, Alternativen existieren (z.B. Authentik, ORY Hydra)
  
- ⚠️ **Misconfiguration-Risiken**: 
  - **Mitigation**: Realm-Export im Repository, Peer Reviews, Security Checklist

### Technische Schuld

- 💰 **Migration-Aufwand**: Falls später zu anderem IdP gewechselt wird (unwahrscheinlich)
- 💰 **Custom UI**: Keycloak-Login-Screens sind generisch, Custom-Themes erfordern Aufwand
- 💰 **Offline-Support**: Mobile App ohne Internet kann nicht authentifizieren (Biometrie als Workaround)

---

## Alternativen

### Alternative A: Spring Security mit Custom JWT

**Beschreibung**: Eigene JWT-Generierung und -Validierung mit Spring Security.

**Pro**:
- ✅ Volle Kontrolle über Token-Format und Claims
- ✅ Keine externe Abhängigkeit
- ✅ Geringerer Resource-Footprint

**Contra**:
- ❌ Hoher Entwicklungsaufwand (2-3 Wochen)
- ❌ Security-Risiken durch Custom-Implementierung
- ❌ Kein SSO, keine User-Federation
- ❌ Custom-UI für User-Management nötig
- ❌ Keine 2FA, Password-Reset, Email-Verification out-of-the-box

**Entscheidung**: Abgelehnt - Zu hoher Aufwand, höheres Security-Risiko

---

### Alternative B: Auth0 (SaaS)

**Beschreibung**: Managed Identity Provider (Cloud-Service).

**Pro**:
- ✅ Keine Infrastruktur-Verwaltung
- ✅ Sehr einfache Integration
- ✅ Automatische Updates, Security-Patches
- ✅ Exzellente Developer Experience

**Contra**:
- ❌ **Kosten**: ~23€/Monat für 1000 MAUs (Monthly Active Users)
- ❌ **Vendor Lock-In**: Daten liegen bei Drittanbieter
- ❌ **DSGVO-Bedenken**: US-Server, Schulen bevorzugen EU/AT-Hosting
- ❌ **Internet-Abhängigkeit**: Ohne Auth0-Verbindung keine Authentifizierung

**Entscheidung**: Abgelehnt - Kosten, Datenschutz-Bedenken für Schulprojekt

---

### Alternative C: Firebase Authentication

**Beschreibung**: Google's Identity Platform (BaaS).

**Pro**:
- ✅ Einfache Integration (besonders Mobile)
- ✅ Social Login inklusive
- ✅ Günstig für kleine Projekte (Free Tier)

**Contra**:
- ❌ **Vendor Lock-In**: Google-Ökosystem
- ❌ **Eingeschränkte Rollenverwaltung**: Custom Claims erforderlich
- ❌ **Kein vollständiger OAuth2-Flow**: Nicht ideal für Backend-API
- ❌ **DSGVO-Bedenken**: Google-Server

**Entscheidung**: Abgelehnt - Zu stark auf Google-Ökosystem fokussiert

---

### Alternative D: Authentik

**Beschreibung**: Open-Source IdP (ähnlich Keycloak).

**Pro**:
- ✅ Open Source, Self-Hosted
- ✅ Moderne UI (React-basiert)
- ✅ Gute OAuth2/OIDC-Unterstützung

**Contra**:
- ❌ **Geringere Community**: Weniger Dokumentation als Keycloak
- ❌ **Weniger Features**: Kein User Federation, weniger Customization
- ❌ **Neueres Projekt**: Weniger Battle-Tested

**Entscheidung**: Abgelehnt - Keycloak ist etablierter, bessere Spring Boot Integration

---

## Metriken für Erfolg

**Wie messen wir den Erfolg dieser Entscheidung?**

| Metrik | Ziel | Messung |
|--------|------|---------|
| **Setup-Zeit** | < 2 Tage für vollständige Keycloak-Konfiguration | Tatsächliche Stunden |
| **Login-Performance** | < 500ms von Login-Button-Klick bis Token | Frontend-Monitoring |
| **Token-Validation** | < 50ms pro API-Request (JWT-Validation) | Backend-Logs |
| **Uptime** | 99.9% (Development), 99.95% (Production) | Monitoring-Dashboard |
| **Security-Incidents** | 0 kritische Sicherheitslücken im ersten Jahr | Security-Audits |
| **Developer Satisfaction** | > 4/5 Sterne (Team-Umfrage) | Retro-Feedback |

---

## Implementierungs-Plan

**Phasen:**

### Phase 1: Keycloak-Setup (2 Tage)
- [x] Docker-Compose Konfiguration
- [x] Realm "ecotrack" erstellen
- [x] 3 Clients konfigurieren (Backend, Admin-Web, Mobile)
- [x] Rollen definieren (STUDENT, TEACHER, ADMIN)
- [x] Realm-Export für Versionierung

### Phase 2: Backend-Integration (1 Tag)
- [ ] Spring Security OAuth2 Resource Server Konfiguration
- [ ] JWT-Validierung testen
- [ ] Role-Mapping (Keycloak-Roles → Spring Security Authorities)

### Phase 3: Admin-Web-Integration (2 Tage)
- [ ] Keycloak JS Adapter oder oidc-client-ts Integration
- [ ] Login/Logout-Flow implementieren
- [ ] Protected Routes (Role-Based)
- [ ] Token-Refresh-Mechanismus

### Phase 4: Mobile-Integration (2 Tage)
- [ ] react-native-app-auth Setup
- [ ] Deep Link Handling (ecotrack://callback)
- [ ] Biometrische Authentifizierung (optional)
- [ ] Token-Storage (Secure Storage)

### Phase 5: Dokumentation (1 Tag)
- [x] Setup-Guide für Entwickler
- [ ] Keycloak-Admin-Anleitung
- [ ] Troubleshooting-Dokumentation

---

## Referenzen

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [OAuth2 RFC 6749](https://datatracker.ietf.org/doc/html/rfc6749)
- [OpenID Connect Core](https://openid.net/specs/openid-connect-core-1_0.html)
- [PKCE RFC 7636](https://datatracker.ietf.org/doc/html/rfc7636)

---

## Änderungshistorie

| Datum | Änderung | Autor |
|-------|----------|-------|
| 2026-01-20 | Initial Draft | Software Architect |

---

## Entscheidungsträger

- [x] **Product Owner** (Zustimmung)
- [x] **Tech Lead** (Zustimmung)
- [x] **Security Team** (Review ausstehend)
- [x] **DevOps Team** (Zustimmung)
