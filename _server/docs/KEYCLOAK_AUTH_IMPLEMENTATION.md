# EcoTrack – Keycloak-Authentifizierung: Implementierungsdokumentation

**Stand:** 05. März 2026  
**Branch:** `13-us-mh-1-registrierung-login`

---

## 1. Übersicht

Diese Dokumentation beschreibt die vollständige Implementierung der Keycloak-basierten Authentifizierung für das EcoTrack-Projekt. Die Implementierung umfasst:

| Feature | Beschreibung |
|---------|-------------|
| **Keycloak-Integration** | Registrierung und Login über Keycloak Admin REST API + ROPC Grant |
| **E-Mail-Verifikation** | Automatischer Versand einer Bestätigungs-E-Mail bei Registrierung |
| **Passwort-Änderungszwang** | Auslesen der `requiredActions` aus Keycloak (z. B. `UPDATE_PASSWORD`) |
| **Default-Rollenzuweisung** | Standardrolle `SCHUELER` für Benutzer ohne explizite Rollenzuweisung |
| **Rollenbasierte Login-Einschränkung** | Admin-Dashboard nur für `ADMIN`/`LEHRER`, Mobile-App nur für `SCHUELER` |
| **JIT-Provisioning** | Automatisches Anlegen lokaler DB-Einträge für Keycloak-Admin-Konsole-Benutzer |

---

## 2. Architektur

### 2.1 Technologie-Stack

- **Identity Provider:** Keycloak 23.0.7 (Docker, Port 8180)
- **Backend:** Spring Boot 3.2.12, Java 21+
- **Datenbank:** H2 In-Memory (Dev), PostgreSQL (Prod)
- **Frontend:** React 18, TypeScript, Vite, Zustand
- **E-Mail:** Mailhog (Dev, Port 1025/8025), SMTP (Prod)

### 2.2 Kommunikationsfluss

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Frontend   │────▶│   Backend    │────▶│   Keycloak   │────▶│   Mailhog    │
│  (React/RN)  │     │ (Spring Boot)│     │  (Port 8180) │     │  (Port 8025) │
└──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
    HTTP/JSON          JSON/REST            Admin REST API        SMTP (Port 1025)
```

### 2.3 Keycloak als Single Source of Truth

Keycloak ist die zentrale Autorität für:
- **Benutzerkonten** (Erstellung, Passwort-Management)
- **E-Mail-Verifikation** (Status + Versand)
- **Rollen** (Realm-Rollen: `ADMIN`, `LEHRER`, `SCHUELER`)
- **Required Actions** (`VERIFY_EMAIL`, `UPDATE_PASSWORD`)
- **Token-Ausstellung** (ROPC Grant für access/refresh Tokens)

Die lokale H2/PostgreSQL-Datenbank dient nur zur:
- Statusverwaltung (`ACTIVE`, `DISABLED`)
- Profilverknüpfung mit `EcoUserProfile`
- Anwendungsspezifischen Daten (Klassen, Punkte, etc.)

---

## 3. Backend-Implementierung

### 3.1 Beteiligte Klassen

| Klasse | Paket | Verantwortung |
|--------|-------|---------------|
| `AuthService` | `administration.application` | Geschäftslogik für Registrierung & Login |
| `AuthDtos` | `administration.application` | Request/Response DTOs |
| `AuthController` | `administration.api` | REST-Endpunkte |
| `KeycloakAdminService` | `administration.security` | Keycloak Admin REST API Client |
| `KeycloakTokenService` | `administration.security` | Keycloak Token-Operationen (ROPC) |
| `KeycloakAdminProperties` | `administration.security` | Konfiguration (server-url, realm, client) |

### 3.2 Registrierung

**Endpunkte:**
- `POST /api/auth/register`
- `POST /api/v1/registration` (Legacy)

**Ablauf:**

```
1. Validierung (E-Mail-Duplikat, Klasse für SCHUELER)
2. Keycloak: Benutzer anlegen
   → emailVerified = false
   → requiredActions = ["VERIFY_EMAIL"]
   → Realm-Rolle zuweisen (ADMIN/LEHRER/SCHUELER)
   → Verifikations-E-Mail senden
3. Lokale DB: AppUser anlegen (userId = Keycloak-UUID)
4. Lokale DB: EcoUserProfile anlegen
5. Response: RegisterResponse (userId, email, message)
   → KEIN automatischer Login (E-Mail muss zuerst verifiziert werden)
```

**Response-Format:**
```json
{
  "userId": "ef638739-0be2-4354-a040-06dcbf65e701",
  "email": "user@example.com",
  "message": "Registrierung erfolgreich. Bitte prüfe dein E-Mail-Postfach und bestätige deine E-Mail-Adresse."
}
```

#### 3.2.1 Keycloak-Benutzeranlage (KeycloakAdminService)

```java
// Benutzer-Representation für Keycloak
Map<String, Object> userRep = new HashMap<>();
userRep.put("username", email);
userRep.put("email", email);
userRep.put("firstName", firstName);
userRep.put("lastName", lastName);
userRep.put("enabled", true);
userRep.put("emailVerified", false);  // E-Mail nicht verifiziert
userRep.put("requiredActions", List.of("VERIFY_EMAIL"));
userRep.put("credentials", List.of(Map.of(
    "type", "password",
    "value", password,
    "temporary", false
)));
```

#### 3.2.2 Verifikations-E-Mail-Versand

```java
private void sendVerificationEmail(String adminToken, UUID keycloakUserId) {
    restClient.put()
        .uri(serverUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId
             + "/execute-actions-email")
        .header("Authorization", "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(List.of("VERIFY_EMAIL"))
        .retrieve()
        .toBodilessEntity();
}
```

Diese Methode nutzt Keycloaks `execute-actions-email` API, die eine E-Mail mit Bestätigungslink an den Benutzer sendet. Im Development-Setup wird diese E-Mail von **Mailhog** (http://localhost:8025) abgefangen.

### 3.3 Login

**Endpunkte:**
- `POST /api/auth/login` → allgemeiner Login (`targetRole = null`)
- `POST /api/v1/auth/admin/login` → Admin-Dashboard (`targetRole = ADMIN`)
- `POST /api/v1/auth/mobile/login` → Mobile-App (`targetRole = SCHUELER`)

**Ablauf:**

```
1. Keycloak: Benutzerdaten abrufen (getUserByEmail)
2. Prüfung: emailVerified == true?
   → Nein: 403 EMAIL_NOT_VERIFIED
3. Prüfung: requiredActions enthält "UPDATE_PASSWORD"?
   → Ja: 401 PASSWORD_CHANGE_REQUIRED
4. Keycloak: ROPC-Login (Passwortprüfung)
   → Fehler: 401 INVALID_CREDENTIALS
5. Lokalen Benutzer laden oder per JIT-Provisioning anlegen
6. Rollenprüfung:
   → Admin-Endpoint + Rolle SCHUELER: 403 INSUFFICIENT_ROLE
   → Mobile-Endpoint + Rolle ≠ SCHUELER: 403 ROLE_MISMATCH
7. Statusprüfung: DISABLED → 403 USER_DISABLED
8. AuthResponse mit Tokens zurückgeben
```

#### 3.3.1 E-Mail-Verifikationsprüfung

```java
Map<String, Object> kcUser = keycloakAdminService.getUserByEmail(request.email());

if (kcUser != null) {
    Boolean emailVerified = (Boolean) kcUser.get("emailVerified");
    if (emailVerified == null || !emailVerified) {
        throw new ApiException(HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED",
            "E-Mail-Adresse wurde noch nicht verifiziert. Bitte prüfe dein Postfach.");
    }
}
```

#### 3.3.2 Passwort-Änderungszwang

```java
List<String> requiredActions = (List<String>) kcUser.getOrDefault("requiredActions", List.of());
if (requiredActions.contains("UPDATE_PASSWORD")) {
    throw new ApiException(HttpStatus.UNAUTHORIZED, "PASSWORD_CHANGE_REQUIRED",
        "Das Passwort muss vor dem ersten Login geändert werden");
}
```

Das `UPDATE_PASSWORD`-Flag wird in der Keycloak Admin-Konsole gesetzt (z. B. beim Anlegen eines Benutzers mit temporärem Passwort). Solange dieses Flag aktiv ist, wird der Login blockiert.

#### 3.3.3 Rollenbasierte Admin-Einschränkung

```java
// Admin-Dashboard: Nur ADMIN und LEHRER erlaubt
if (targetRole == Role.ADMIN) {
    if (user.getRole() == Role.SCHUELER) {
        throw new ApiException(HttpStatus.FORBIDDEN, "INSUFFICIENT_ROLE",
            "Schüler haben keinen Zugang zum Admin-Dashboard. Bitte die Mobile-App verwenden.");
    }
}
// Mobile-Login: Nur Schüler erlaubt
if (targetRole == Role.SCHUELER && user.getRole() != Role.SCHUELER) {
    throw new ApiException(HttpStatus.FORBIDDEN, "ROLE_MISMATCH",
        "Falscher Login-Endpunkt für diese Rolle");
}
```

### 3.4 JIT-Provisioning (Just-In-Time)

Wenn ein Benutzer in der **Keycloak Admin-Konsole** angelegt wurde, existiert er nicht in der lokalen Datenbank. Beim ersten Login wird automatisch ein lokaler Eintrag erstellt:

```
1. Login-Versuch → Benutzer nicht in lokaler DB
2. Keycloak: Benutzerdaten abrufen (getUserByEmail)
3. Keycloak: Realm-Rollen abrufen (getUserRealmRoles)
4. Rolle ableiten: ADMIN > LEHRER > SCHUELER (Prioritätsreihenfolge)
5. Lokalen AppUser anlegen
6. EcoUserProfile anlegen
7. Login fortsetzen
```

#### Rollenauflösung

```java
private Role resolveRoleFromKeycloak(UUID keycloakUserId) {
    List<String> roles = keycloakAdminService.getUserRealmRoles(keycloakUserId);
    if (roles.contains("ADMIN"))    return Role.ADMIN;
    if (roles.contains("LEHRER"))   return Role.LEHRER;
    if (roles.contains("SCHUELER")) return Role.SCHUELER;
    return Role.SCHUELER;  // Default-Rolle
}
```

### 3.5 Default-Rollenzuweisung

Die Rolle `SCHUELER` wird in zwei Situationen als Standard verwendet:

1. **Bei Registrierung:** Der Benutzer wählt seine Rolle explizit (`ADMIN`, `LEHRER`, `SCHUELER`)
2. **Bei JIT-Provisioning:** Wenn keine der Rollen `ADMIN`, `LEHRER` oder `SCHUELER` in Keycloak zugewiesen ist, wird automatisch `SCHUELER` vergeben

---

## 4. Error-Codes

| Code | HTTP Status | Bedeutung |
|------|-------------|-----------|
| `EMAIL_NOT_VERIFIED` | 403 Forbidden | E-Mail wurde noch nicht bestätigt |
| `PASSWORD_CHANGE_REQUIRED` | 401 Unauthorized | Passwort muss geändert werden (Keycloak-Flag) |
| `INSUFFICIENT_ROLE` | 403 Forbidden | Schüler versucht Admin-Dashboard-Login |
| `ROLE_MISMATCH` | 403 Forbidden | Falsche Rolle für den Login-Endpunkt |
| `INVALID_CREDENTIALS` | 401 Unauthorized | Falsche E-Mail oder falsches Passwort |
| `EMAIL_EXISTS` | 400 Bad Request | E-Mail ist bereits registriert |
| `USER_DISABLED` | 403 Forbidden | Benutzerkonto deaktiviert |
| `CLASS_REQUIRED` | 400 Bad Request | classId fehlt bei Schüler-Registrierung |

---

## 5. Frontend-Implementierung (Admin-Web)

### 5.1 Beteiligte Dateien

| Datei | Verantwortung |
|-------|---------------|
| `src/types/auth.ts` | Typen + Error-Klassen |
| `src/api/authApi.ts` | API-Client für Auth-Endpunkte |
| `src/stores/authStore.ts` | Zustand-Store für Auth-State |
| `src/pages/LoginPage.tsx` | Login-Seite mit Fehlermeldungen |
| `src/services/apiClient.ts` | Axios-Client mit Interceptor |

### 5.2 Neue Error-Klassen

```typescript
export class EmailNotVerifiedError extends Error {
  constructor(message: string = 'E-Mail nicht verifiziert') {
    super(message);
    this.name = 'EmailNotVerifiedError';
  }
}

export class InsufficientRoleError extends Error {
  constructor(message: string = 'Unzureichende Berechtigung') {
    super(message);
    this.name = 'InsufficientRoleError';
  }
}
```

### 5.3 API Error Mapping

In `authApi.ts` werden Backend-Error-Codes auf typisierte Fehler abgebildet:

```typescript
function handleApiError(error: unknown): never {
  if (axios.isAxiosError(error)) {
    const code = error.response?.data?.code;
    const message = error.response?.data?.message;
    
    switch (code) {
      case 'PASSWORD_CHANGE_REQUIRED':
        throw new PasswordChangeRequiredError(message);
      case 'EMAIL_NOT_VERIFIED':
        throw new EmailNotVerifiedError(message);
      case 'INSUFFICIENT_ROLE':
        throw new InsufficientRoleError(message);
    }
  }
  throw error;
}
```

### 5.4 Login-Seite: E-Mail-Verifikations-Hinweis

Wenn der Login mit `EMAIL_NOT_VERIFIED` fehlschlägt, wird auf der Login-Seite ein blauer Info-Block angezeigt:

```
📧 Deine E-Mail-Adresse wurde noch nicht verifiziert.
   Bitte prüfe dein Postfach und klicke auf den Bestätigungslink.
```

### 5.5 Login-Seite: Rolleneinschränkung

Bei `INSUFFICIENT_ROLE` wird die Standard-Fehlermeldung angezeigt:

```
Schüler haben keinen Zugang zum Admin-Dashboard. Bitte die Mobile-App verwenden.
```

---

## 6. Keycloak-Konfiguration

### 6.1 Realm: `ecotrack`

| Einstellung | Wert |
|-------------|------|
| Realm Name | `ecotrack` |
| SSL Required | `none` (Development) |
| SMTP Server | `mailhog:1025` (Docker) / `localhost:1025` (lokal) |

### 6.2 Client: `ecotrack-backend`

| Einstellung | Wert |
|-------------|------|
| Client ID | `ecotrack-backend` |
| Client Authentication | ON (Confidential) |
| Direct Access Grants | ON (ROPC) |
| Service Account | ON |
| Service Account Role | `realm-admin` (für Admin API) |

### 6.3 Realm-Rollen

| Rolle | Beschreibung |
|-------|-------------|
| `ADMIN` | Vollzugriff auf Admin-Dashboard |
| `LEHRER` | Zugriff auf Admin-Dashboard (eingeschränkt) |
| `SCHUELER` | Nur Mobile-App, kein Admin-Dashboard |

### 6.4 Wichtige Hinweise

> **Service-Account-Rolle:** Die Rolle `realm-admin` muss dem Service-Account des Clients `ecotrack-backend` zugewiesen werden. Ohne diese Rolle schlagen alle Admin-API-Aufrufe mit `403 Forbidden` fehl.

> **Docker Volumes:** Bei `docker compose down -v` gehen alle KC-Daten verloren. Die Service-Account-Rollenzuweisung muss dann erneut vorgenommen werden (über KC Admin-Konsole oder Realm-Export).

---

## 7. Konfiguration (application.yml)

```yaml
keycloak:
  admin:
    server-url: http://localhost:8180
    realm: ecotrack
    client-id: ecotrack-backend
    client-secret: ${KEYCLOAK_CLIENT_SECRET}
```

---

## 8. Tests

### 8.1 Backend-Tests (4/4 PASS)

**`EcoTrackApplicationTests`** — Spring Context Load  
**`ApiIntegrationTests`** — 3 Tests mit MockMvc:

| Test | Beschreibung |
|------|-------------|
| `registerStudentCreateActivityAndReadProgress` | Vollständiger Flow: Admin registriert → Klasse anlegen → Student registriert → Aktivität → Fortschritt |
| `teacherLoginRequiresPasswordChange` | Lehrer mit `UPDATE_PASSWORD` → Login wird blockiert |
| `teacherCanCreateChallengeForClass` | Lehrer erstellt Challenge für Klasse |

Die Tests mocken `KeycloakAdminService` und `KeycloakTokenService`. Für den `PASSWORD_CHANGE_REQUIRED`-Test wird `getUserByEmail()` so konfiguriert, dass `requiredActions: ["UPDATE_PASSWORD"]` zurückgegeben wird.

### 8.2 Frontend-Tests (14/14 PASS)

Alle bestehenden Vitest-Tests bestehen weiterhin:
- Auth-Store Tests
- Login-Page Tests
- Komponenten-Tests

### 8.3 E2E-Tests (manuell, alle bestanden)

| Test | Request | Erwartung | Ergebnis |
|------|---------|-----------|----------|
| Registrierung | `POST /api/v1/registration` | `RegisterResponse` mit Nachricht | ✅ |
| Login ohne E-Mail-Verifikation | `POST /api/v1/auth/admin/login` | `EMAIL_NOT_VERIFIED` (403) | ✅ |
| Login nach Verifikation | `POST /api/v1/auth/admin/login` | Token + User-Daten | ✅ |
| SCHUELER auf Admin-Endpoint | `POST /api/v1/auth/admin/login` | `INSUFFICIENT_ROLE` (403) | ✅ |
| Login mit UPDATE_PASSWORD | `POST /api/v1/auth/admin/login` | `PASSWORD_CHANGE_REQUIRED` (401) | ✅ |

---

## 9. Sequenzdiagramme

### 9.1 Registrierung

```
Benutzer          Frontend          Backend          Keycloak         Mailhog
   │                  │                │                │                │
   │  Formular        │                │                │                │
   │─────────────────▶│                │                │                │
   │                  │  POST /register│                │                │
   │                  │───────────────▶│                │                │
   │                  │                │  createUser()  │                │
   │                  │                │───────────────▶│                │
   │                  │                │  assignRole()  │                │
   │                  │                │───────────────▶│                │
   │                  │                │  sendVerifyMail│                │
   │                  │                │───────────────▶│                │
   │                  │                │                │  SMTP: E-Mail  │
   │                  │                │                │───────────────▶│
   │                  │                │  DB: AppUser   │                │
   │                  │                │  DB: Profile   │                │
   │                  │  RegisterResp  │                │                │
   │                  │◀───────────────│                │                │
   │  "Prüfe E-Mail"  │                │                │                │
   │◀─────────────────│                │                │                │
```

### 9.2 Login (Erfolg)

```
Benutzer          Frontend          Backend          Keycloak
   │                  │                │                │
   │  Login           │                │                │
   │─────────────────▶│                │                │
   │                  │  POST /login   │                │
   │                  │───────────────▶│                │
   │                  │                │ getUserByEmail │
   │                  │                │───────────────▶│
   │                  │                │  ◀── kcUser ───│
   │                  │                │                │
   │                  │                │ ✓ emailVerified│
   │                  │                │ ✓ no requiredActions
   │                  │                │                │
   │                  │                │  ROPC login()  │
   │                  │                │───────────────▶│
   │                  │                │  ◀── tokens ───│
   │                  │                │                │
   │                  │                │ DB: load/JIT user
   │                  │                │ ✓ role check   │
   │                  │                │ ✓ status check │
   │                  │                │                │
   │                  │  AuthResponse  │                │
   │                  │◀───────────────│                │
   │  Dashboard       │                │                │
   │◀─────────────────│                │                │
```

---

## 10. Geänderte Dateien

### 10.1 Backend

| Datei | Änderungen |
|-------|-----------|
| `KeycloakAdminService.java` | `emailVerified=false`, `requiredActions=["VERIFY_EMAIL"]`, `sendVerificationEmail()`, `getUserByEmail()`, `getUserRealmRoles()` |
| `AuthService.java` | Login-Rewrite (KC-Vorabprüfungen), `register()` → `RegisterResponse`, JIT-Provisioning, Rollen-Checks |
| `AuthDtos.java` | Neues DTO `RegisterResponse` |
| `AuthController.java` | Endpunkte auf `RegisterResponse` umgestellt, `adminLogin()` → `Role.ADMIN` |
| `ApiIntegrationTests.java` | Mocks für `getUserByEmail()`, `getUserRealmRoles()`, Test-Anpassungen |

### 10.2 Frontend

| Datei | Änderungen |
|-------|-----------|
| `types/auth.ts` | `EmailNotVerifiedError`, `InsufficientRoleError` |
| `authApi.ts` | Error-Mapping für neue Codes |
| `authStore.ts` | `emailNotVerified`-State, Error-Handling |
| `LoginPage.tsx` | E-Mail-Verifikations-Hinweis (blauer Info-Block) |

---

## 11. Bekannte Einschränkungen

1. **Passwort-Änderung:** Der Benutzer wird über `PASSWORD_CHANGE_REQUIRED` informiert, aber es gibt noch keinen Frontend-Flow zum Ändern des Passworts direkt in der App. Das Passwort muss über die Keycloak Account-Konsole geändert werden.

2. **E-Mail-Verifikation:** Im Development-Setup landen Verifikations-E-Mails in Mailhog (http://localhost:8025). Ein klickbarer Bestätigungslink erfordert korrekte Keycloak-Frontend-URL-Konfiguration.

3. **Service-Account-Rolle:** Die `realm-admin` Rolle für den Service-Account geht bei `docker compose down -v` verloren und muss manuell oder per Realm-Export wiederhergestellt werden.

4. **H2 In-Memory:** Bei Backend-Neustart werden alle lokalen Daten gelöscht. JIT-Provisioning stellt sicher, dass Keycloak-Benutzer beim nächsten Login automatisch wieder angelegt werden.
