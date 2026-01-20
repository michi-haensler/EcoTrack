# EcoTrack - Keycloak Setup Guide

## 🚀 Quick Start (5 Minuten)

### 1. Keycloak starten

```bash
# Repository klonen (falls noch nicht geschehen)
cd EcoTrack

# Environment-Variablen setzen
cp .env.example .env
# Bearbeite .env und setze sichere Passwörter

# Keycloak + PostgreSQL starten
docker-compose up keycloak postgres mailhog -d

# Logs verfolgen (optional)
docker-compose logs -f keycloak
```

**Keycloak ist bereit, wenn du siehst:**
```
Keycloak 23.0 (WildFly Core 21.0.5.Final) started in 15432ms
```

---

### 2. Admin Console öffnen

- **URL**: http://localhost:8180/admin
- **Username**: `admin` (aus `.env`)
- **Password**: `<KEYCLOAK_ADMIN_PASSWORD>` (aus `.env`)

---

### 3. Realm verifizieren

1. Oben links: Dropdown "Master" → **"ecotrack"** sollte verfügbar sein
2. Falls nicht: Realm wurde nicht importiert (siehe Troubleshooting)

---

### 4. Client-Secret exportieren

Siehe [README-CLIENT-CREDENTIALS.md](./README-CLIENT-CREDENTIALS.md) für detaillierte Anleitung.

**Kurzversion:**
1. Clients → **ecotrack-backend** → Credentials Tab
2. **Client Secret** kopieren
3. In `.env` einfügen: `KEYCLOAK_CLIENT_SECRET=...`

---

## 📋 Vollständiger Setup-Workflow

### Phase 1: Docker-Setup

#### 1.1 Voraussetzungen prüfen

```bash
# Docker installiert?
docker --version  # Min. 20.10+

# Docker-Compose installiert?
docker-compose --version  # Min. 2.0+

# Ports frei?
lsof -i :8180  # Keycloak
lsof -i :5432  # PostgreSQL
lsof -i :8025  # MailHog
```

#### 1.2 Environment-Variablen konfigurieren

```bash
# .env erstellen
cp .env.example .env

# Passwörter setzen (Beispiel)
nano .env
```

**Empfohlene Werte (Development):**
```bash
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=DevAdmin123!  # Mindestens 12 Zeichen
POSTGRES_PASSWORD=DevPostgres123!      # Mindestens 12 Zeichen
```

#### 1.3 Services starten

```bash
# Alle Services starten
docker-compose up -d

# Nur Keycloak + Dependencies
docker-compose up keycloak -d

# Mit Logs (Foreground)
docker-compose up keycloak
```

#### 1.4 Health Check

```bash
# Keycloak Health Endpoint
curl http://localhost:8180/health/ready

# Erwartete Antwort: {"status":"UP"}
```

---

### Phase 2: Realm-Konfiguration

#### 2.1 Realm-Import verifizieren

**Automatischer Import:**
- Beim ersten Start: `realm-export.json` wird automatisch importiert
- Volume-Mount in `docker-compose.yml`:
  ```yaml
  volumes:
    - ./shared-resources/keycloak/realm-export.json:/opt/keycloak/data/import/realm-export.json:ro
  ```

**Manueller Import (falls nötig):**
1. Admin Console: http://localhost:8180/admin
2. Oben links: Dropdown → **Create Realm**
3. **Resource file**: `Browse` → `realm-export.json` hochladen
4. **Create**

#### 2.2 Realm-Struktur verifizieren

**Checklist:**
- [ ] Realm Name: `ecotrack`
- [ ] Rollen: `STUDENT`, `TEACHER`, `ADMIN`
- [ ] Clients: `ecotrack-backend`, `ecotrack-admin-web`, `ecotrack-mobile`
- [ ] Client Scopes: `roles`, `profile`, `email`

**Verifizierung via CLI:**
```bash
# Realm-Liste abrufen
docker exec ecotrack-keycloak \
  /opt/keycloak/bin/kcadm.sh config credentials \
  --server http://localhost:8080 \
  --realm master \
  --user admin \
  --password "${KEYCLOAK_ADMIN_PASSWORD}"

docker exec ecotrack-keycloak \
  /opt/keycloak/bin/kcadm.sh get realms
```

---

### Phase 3: Client-Konfiguration

#### 3.1 Backend Client (ecotrack-backend)

**Zugriff:**
- Admin Console → Clients → **ecotrack-backend**

**Settings:**
```
Client ID:              ecotrack-backend
Name:                   EcoTrack Backend API
Access Type:            confidential ✅
Service Accounts:       ON ✅
Authorization:          OFF
Standard Flow:          ON ✅
Implicit Flow:          OFF
Direct Access Grants:   OFF
```

**Valid Redirect URIs:**
```
http://localhost:8080/*
http://localhost:8080/login/oauth2/code/keycloak
```

**Web Origins:**
```
http://localhost:8080
```

**Credentials:**
- Tab: **Credentials**
- **Client Secret** kopieren → `.env`: `KEYCLOAK_CLIENT_SECRET=...`

---

#### 3.2 Admin-Web Client (ecotrack-admin-web)

**Settings:**
```
Client ID:              ecotrack-admin-web
Name:                   EcoTrack Admin Web
Access Type:            public ✅
Standard Flow:          ON ✅
Implicit Flow:          OFF
Direct Access Grants:   OFF
```

**Valid Redirect URIs:**
```
http://localhost:5173/*
http://localhost:5173/callback
http://localhost:5173/silent-check-sso.html
```

**Web Origins:**
```
http://localhost:5173
```

**Advanced Settings:**
- **Proof Key for Code Exchange (PKCE)**: `S256` ✅

---

#### 3.3 Mobile Client (ecotrack-mobile)

**Settings:**
```
Client ID:              ecotrack-mobile
Name:                   EcoTrack Mobile App
Access Type:            public ✅
Standard Flow:          ON ✅
```

**Valid Redirect URIs:**
```
ecotrack://callback
ecotrack://logout
```

**Advanced Settings:**
- **Proof Key for Code Exchange (PKCE)**: `S256` ✅

---

### Phase 4: Rollenmodell

#### 4.1 Realm-Rollen

| Rolle | Beschreibung | Composite Roles |
|-------|--------------|-----------------|
| `STUDENT` | Standard-Nutzer (Schüler:innen) | - |
| `TEACHER` | Lehrkräfte | `STUDENT` |
| `ADMIN` | System-Administratoren | `TEACHER`, `STUDENT` |

**Verifizierung:**
- Admin Console → Realm Roles
- Alle 3 Rollen sollten existieren

**Composite Roles prüfen:**
1. Role: **TEACHER** → Composite Roles Tab
2. Sollte enthalten: `STUDENT`

---

### Phase 5: Test-User erstellen

#### 5.1 User anlegen (Admin Console)

1. **Users** → **Add user**
2. **Username**: `test.student@ecotrack.local`
3. **Email**: `test.student@ecotrack.local`
4. **First Name**: `Test`
5. **Last Name**: `Student`
6. **Email Verified**: `ON` ✅
7. **Create**

#### 5.2 Passwort setzen

1. User öffnen → **Credentials** Tab
2. **Set Password**:
   - Password: `Test1234!`
   - Temporary: `OFF`
3. **Set Password**

#### 5.3 Rolle zuweisen

1. **Role Mappings** Tab
2. **Assign role** → `STUDENT` auswählen
3. **Assign**

#### 5.4 Test-User-Set (Empfohlen)

```bash
# Script zum Erstellen aller Test-User (TODO: Erstellen)
./scripts/create-test-users.sh
```

**Test-User:**

| Email | Password | Rolle | Zweck |
|-------|----------|-------|-------|
| `test.student@ecotrack.local` | `Test1234!` | STUDENT | Mobile App |
| `test.teacher@ecotrack.local` | `Test1234!` | TEACHER | Admin-Web |
| `test.admin@ecotrack.local` | `Test1234!` | ADMIN | Admin-Funktionen |

---

### Phase 6: E-Mail-Konfiguration (Development)

#### 6.1 MailHog verwenden

**MailHog** = Fake SMTP Server für Development

- **SMTP**: localhost:1025
- **Web UI**: http://localhost:8025

**Keycloak SMTP-Config (bereits in realm-export.json):**
```json
"smtpServer": {
  "from": "noreply@ecotrack.local",
  "host": "mailhog",
  "port": "1025"
}
```

#### 6.2 E-Mail-Versand testen

1. Admin Console → Users → Test-User
2. **Send Update Password Email**
3. MailHog UI öffnen: http://localhost:8025
4. E-Mail sollte sichtbar sein

---

## 🧪 Testing

### Test 1: Token abrufen (Backend Client)

```bash
# Client Credentials Grant (Service Account)
curl -X POST http://localhost:8180/realms/ecotrack/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=ecotrack-backend" \
  -d "client_secret=YOUR_CLIENT_SECRET"

# Erwartete Antwort:
# {
#   "access_token": "eyJhbGc...",
#   "token_type": "Bearer",
#   "expires_in": 900
# }
```

---

### Test 2: User-Login (Password Grant - nur für Testing!)

```bash
# Resource Owner Password Credentials (NICHT für Production!)
curl -X POST http://localhost:8180/realms/ecotrack/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=ecotrack-admin-web" \
  -d "username=test.student@ecotrack.local" \
  -d "password=Test1234!"

# Erwartete Antwort:
# {
#   "access_token": "eyJhbGc...",
#   "refresh_token": "eyJhbGc...",
#   "token_type": "Bearer"
# }
```

---

### Test 3: Token dekodieren

```bash
# JWT dekodieren (https://jwt.io oder jq)
echo "YOUR_ACCESS_TOKEN" | cut -d. -f2 | base64 -d | jq

# Erwartete Claims:
# {
#   "sub": "user-id",
#   "realm_access": {
#     "roles": ["STUDENT"]
#   },
#   "email": "test.student@ecotrack.local"
# }
```

---

## 🔧 Troubleshooting

### Problem: Keycloak startet nicht

**Symptom**: Container crasht oder startet nicht

**Lösungen:**

```bash
# Logs prüfen
docker-compose logs keycloak

# Häufige Fehler:
# 1. PostgreSQL nicht bereit
docker-compose up postgres  # Erst DB starten
docker-compose up keycloak   # Dann Keycloak

# 2. Port 8180 belegt
lsof -i :8180
# Prozess beenden oder Port in docker-compose.yml ändern

# 3. Volume-Probleme
docker-compose down -v  # Volumes löschen
docker-compose up keycloak
```

---

### Problem: Realm nicht importiert

**Symptom**: Realm "ecotrack" existiert nicht

**Lösungen:**

1. **Manueller Import:**
   - Admin Console → Create Realm → Upload `realm-export.json`

2. **Container neu starten:**
   ```bash
   docker-compose down
   docker-compose up keycloak
   ```

3. **Import-Logs prüfen:**
   ```bash
   docker-compose logs keycloak | grep import
   ```

---

### Problem: Client Secret funktioniert nicht

**Symptom**: `401 Unauthorized` bei Token-Request

**Lösungen:**

1. **Secret neu generieren:**
   - Admin Console → Clients → ecotrack-backend → Credentials
   - **Regenerate Secret**
   - Neuen Secret in `.env` kopieren

2. **Client-ID prüfen:**
   ```bash
   # Exakte Client-ID (case-sensitive!)
   curl http://localhost:8180/realms/ecotrack/protocol/openid-connect/token \
     -d "client_id=ecotrack-backend"  # NICHT "ecotrack_backend"
   ```

---

### Problem: CORS-Fehler bei Admin-Web

**Symptom**: Browser-Konsole zeigt CORS-Error

**Lösungen:**

1. **Web Origins prüfen:**
   - Admin Console → Clients → ecotrack-admin-web → Settings
   - **Web Origins**: `http://localhost:5173` (exakte URL!)

2. **Wildcard für Development:**
   ```
   Web Origins: *  (NUR für Development!)
   ```

---

### Problem: Redirect URI mismatch

**Symptom**: `Invalid redirect_uri` bei Login

**Lösungen:**

1. **Redirect URIs prüfen:**
   - Admin Console → Clients → ecotrack-admin-web
   - **Valid Redirect URIs**: `http://localhost:5173/*`

2. **Trailing Slash beachten:**
   ```
   ✅ http://localhost:5173/*
   ❌ http://localhost:5173*
   ```

---

## 📚 Weiterführende Dokumentation

- [Client-Credentials exportieren](./README-CLIENT-CREDENTIALS.md)
- [Spring Boot Integration](../../docs/backend/keycloak-integration.md) (TODO)
- [React Admin-Web Integration](../../docs/frontend/keycloak-integration.md) (TODO)
- [React Native Integration](../../docs/mobile/keycloak-integration.md) (TODO)

---

## 🔒 Security Checklist

### Development
- [ ] `.env` in `.gitignore`
- [ ] Keine Secrets in `realm-export.json` (Client Secrets werden nicht exportiert)
- [ ] MailHog nur für Development (keine echten E-Mails)
- [ ] HTTP erlaubt (localhost)

### Production
- [ ] HTTPS erzwungen (`sslRequired: external`)
- [ ] Starke Admin-Passwörter (min. 16 Zeichen)
- [ ] Client-Secret-Rotation (alle 90 Tage)
- [ ] Separate Realms (prod, staging, dev)
- [ ] Real SMTP Server (z.B. SendGrid, AWS SES)
- [ ] Audit-Logs aktiviert
- [ ] Backup-Strategie (PostgreSQL + Realm-Exports)

---

## 🚀 Nächste Schritte

Nach erfolgreichem Keycloak-Setup:

1. **Backend-Integration**: Spring Security OAuth2 Resource Server
2. **Admin-Web-Integration**: Keycloak JS Adapter
3. **Mobile-Integration**: react-native-app-auth
4. **CI/CD**: Keycloak Testcontainers für Integration Tests

---

**Happy Coding! 🌱**
