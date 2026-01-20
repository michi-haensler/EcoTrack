# Keycloak Client Credentials - Anleitung

## 🔐 Client-Credentials aus Keycloak exportieren

### Voraussetzungen
- Keycloak läuft: `docker-compose up keycloak`
- Admin-Zugriff auf Keycloak Admin Console

---

## Schritt 1: Keycloak Admin Console öffnen

1. Öffne Browser: **http://localhost:8180/admin**
2. Login mit Admin-Credentials (aus `.env`):
   - Username: `admin`
   - Password: `<KEYCLOAK_ADMIN_PASSWORD>`

---

## Schritt 2: Realm "ecotrack" auswählen

1. Oben links: Dropdown-Menü "Master" → **"ecotrack"** auswählen
2. Falls Realm nicht existiert:
   - Import über: **Realm Settings** → **Partial Import**
   - Datei: `shared-resources/keycloak/realm-export.json`

---

## Schritt 3: Backend Client Secret exportieren

### ecotrack-backend (Confidential Client)

1. Linkes Menü: **Clients** → **ecotrack-backend** klicken
2. Tab: **Credentials** öffnen
3. **Client Secret** kopieren (z.B. `9f3a7b2c-8d1e-4f5a-9b6c-7d8e9f0a1b2c`)
4. In `.env` einfügen:
   ```bash
   KEYCLOAK_CLIENT_SECRET=9f3a7b2c-8d1e-4f5a-9b6c-7d8e9f0a1b2c
   ```

**Optional: Secret regenerieren**
- Button: **Regenerate Secret**
- ⚠️ Achtung: Alte Secrets werden ungültig!

---

## Schritt 4: Client-Konfiguration verifizieren

### ecotrack-backend
- **Access Type**: `confidential` ✅
- **Service Accounts Enabled**: `ON` ✅
- **Valid Redirect URIs**: `http://localhost:8080/*` ✅
- **Web Origins**: `http://localhost:8080` ✅

### ecotrack-admin-web
- **Access Type**: `public` ✅
- **Valid Redirect URIs**: 
  - `http://localhost:5173/*`
  - `http://localhost:5173/callback`
- **Web Origins**: `http://localhost:5173` ✅
- **PKCE Code Challenge Method**: `S256` ✅

### ecotrack-mobile
- **Access Type**: `public` ✅
- **Valid Redirect URIs**: `ecotrack://callback` ✅
- **PKCE Code Challenge Method**: `S256` ✅

---

## Schritt 5: Test-User erstellen (optional)

### Manuell über Admin Console

1. Linkes Menü: **Users** → **Add user**
2. Felder ausfüllen:
   - **Username**: `test.student@ecotrack.local`
   - **Email**: `test.student@ecotrack.local`
   - **First Name**: `Test`
   - **Last Name**: `Student`
   - **Email Verified**: `ON` ✅
3. Button: **Create**
4. Tab: **Credentials** → **Set Password**:
   - **Password**: `Test1234!`
   - **Temporary**: `OFF`
5. Tab: **Role Mappings** → **Assign role** → `STUDENT` ✅

### Test-User-Liste

| Username | Password | Rolle | Zweck |
|----------|----------|-------|-------|
| `test.student@ecotrack.local` | `Test1234!` | STUDENT | Mobile App Testing |
| `test.teacher@ecotrack.local` | `Test1234!` | TEACHER | Admin-Web Testing |
| `test.admin@ecotrack.local` | `Test1234!` | ADMIN | Admin-Funktionen Testing |

---

## Schritt 6: Client-Credentials in Applikationen einfügen

### Backend (Spring Boot)

**Datei**: `server/ecotrack-app/src/main/resources/application.yml`

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI:http://localhost:8180/realms/ecotrack}
      client:
        registration:
          keycloak:
            client-id: ${KEYCLOAK_CLIENT_ID:ecotrack-backend}
            client-secret: ${KEYCLOAK_CLIENT_SECRET}
            scope: openid, profile, email
            authorization-grant-type: authorization_code
        provider:
          keycloak:
            issuer-uri: ${KEYCLOAK_ISSUER_URI:http://localhost:8180/realms/ecotrack}
```

**Environment Variablen** (aus `.env`):
```bash
export KEYCLOAK_CLIENT_SECRET=9f3a7b2c-8d1e-4f5a-9b6c-7d8e9f0a1b2c
```

---

### Admin-Web (React)

**Datei**: `admin-web/.env.local`

```bash
VITE_KEYCLOAK_URL=http://localhost:8180
VITE_KEYCLOAK_REALM=ecotrack
VITE_KEYCLOAK_CLIENT_ID=ecotrack-admin-web
```

**Verwendung** (z.B. mit `@react-keycloak/web`):

```typescript
import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL,
  realm: import.meta.env.VITE_KEYCLOAK_REALM,
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
});
```

---

### Mobile (React Native)

**Datei**: `mobile/.env`

```bash
KEYCLOAK_URL=http://localhost:8180
KEYCLOAK_REALM=ecotrack
KEYCLOAK_CLIENT_ID=ecotrack-mobile
```

**Verwendung** (mit `react-native-app-auth`):

```typescript
import { authorize } from 'react-native-app-auth';

const config = {
  issuer: `${process.env.KEYCLOAK_URL}/realms/${process.env.KEYCLOAK_REALM}`,
  clientId: process.env.KEYCLOAK_CLIENT_ID,
  redirectUrl: 'ecotrack://callback',
  scopes: ['openid', 'profile', 'email'],
};

const result = await authorize(config);
```

---

## Schritt 7: Realm exportieren (Versionierung)

### Export über Admin Console

1. Linkes Menü: **Realm Settings**
2. Tab: **Action** → **Partial export**
3. Optionen:
   - ✅ **Export groups and roles**
   - ✅ **Export clients**
   - ✅ **Include users** (optional, nicht für Production!)
4. Button: **Export**
5. JSON-Datei speichern als: `shared-resources/keycloak/realm-export.json`

### Export via CLI (Keycloak Container)

```bash
# In Container einloggen
docker exec -it ecotrack-keycloak bash

# Export ausführen
/opt/keycloak/bin/kc.sh export \
  --dir /tmp/export \
  --realm ecotrack \
  --users skip

# Datei aus Container kopieren
docker cp ecotrack-keycloak:/tmp/export/ecotrack-realm.json \
  ./shared-resources/keycloak/realm-export.json
```

---

## Troubleshooting

### ❌ "Client not found" Fehler

**Problem**: Client-ID falsch oder Realm nicht geladen

**Lösung**:
1. Prüfe Realm-Auswahl in Admin Console (oben links)
2. Prüfe Client-ID (Case-Sensitive!)
3. Re-Import Realm: `docker-compose down && docker-compose up keycloak`

---

### ❌ "Invalid Client Credentials" bei Backend

**Problem**: Client Secret falsch

**Lösung**:
1. Admin Console: Clients → ecotrack-backend → Credentials
2. Secret kopieren und in `.env` aktualisieren
3. Backend neu starten

---

### ❌ "Redirect URI mismatch" bei Login

**Problem**: Frontend-URL nicht in Valid Redirect URIs

**Lösung**:
1. Admin Console: Clients → ecotrack-admin-web → Settings
2. **Valid Redirect URIs** hinzufügen: `http://localhost:5173/*`
3. **Web Origins** hinzufügen: `http://localhost:5173`
4. Button: **Save**

---

### ❌ Keycloak startet nicht

**Problem**: PostgreSQL nicht bereit oder Port belegt

**Lösung**:
```bash
# Logs prüfen
docker-compose logs keycloak

# Port-Konflikte prüfen
lsof -i :8180

# Clean Restart
docker-compose down -v
docker-compose up keycloak
```

---

## Security Best Practices

### ✅ DO's
- ✅ Secrets in `.env` (nicht in `.env.example`)
- ✅ `.env` in `.gitignore`
- ✅ Starke Passwörter (min. 16 Zeichen)
- ✅ Regelmäßige Secret-Rotation (Production)
- ✅ HTTPS für Production
- ✅ Separate Realms für Dev/Stage/Prod

### ❌ DON'Ts
- ❌ Secrets committen (Repository-Scan!)
- ❌ Produktions-Secrets in Logs
- ❌ Default-Passwörter (`admin/admin`)
- ❌ Wildcards in Redirect URIs (`*`)
- ❌ Public Clients mit Client Secret

---

## Nächste Schritte

Nach erfolgreicher Keycloak-Konfiguration:

1. **Backend**: Spring Security OAuth2 Resource Server implementieren
2. **Admin-Web**: Login/Logout-Flow mit Keycloak JS
3. **Mobile**: OAuth2 Flow mit Deep Links
4. **Testing**: Integration Tests mit Keycloak Testcontainers

---

## Referenzen

- [Keycloak Admin REST API](https://www.keycloak.org/docs-api/23.0/rest-api/index.html)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [Keycloak JS Adapter](https://www.keycloak.org/docs/latest/securing_apps/#_javascript_adapter)
- [React Native App Auth](https://github.com/FormidableLabs/react-native-app-auth)
