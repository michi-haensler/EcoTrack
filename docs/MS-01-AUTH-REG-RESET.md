# Meilenstein MS‑01: Authentifizierung, Registrierung & Passwort‑Management  
**(Keycloak Integration)**  
**Datum:** 2026‑02‑03

---

## Überblick

Dieser Meilenstein umfasst die vollständige Implementierung des Identitäts‑ und Zugriffsmanagements (IAM) für das Softwareprojekt.  
Er integriert den **Keycloak‑Server** als zentralen Identity Provider (IdP) und ermöglicht sowohl **Benutzern (Mobile)** als auch **Administratoren (Web‑Admin)** einen sicheren Zugang zum System.

**Hinweis:**  
Der Token‑Refresh‑Mechanismus ist **nicht** Teil dieses Meilensteins.

---

## Funktionsumfang & Anforderungen

### 1. Selbstregistrierung (Mobile App)

Schüler und Lehrer können eigenständig ein Konto anlegen.

- **E‑Mail‑Verifikation:**  
  Nach der Registrierung wird eine Bestätigungs‑E‑Mail versendet.  
  Der Account ist erst nach Klick auf den Verifikationslink aktiv.

- **Rollen‑Zuweisung:**  
  Standardrolle: `STUDENT`  
  (Ausnahme: z. B. Lehrer‑Import oder andere Logik)

---

### 2. Login‑Prozesse

- **Schüler/Lehrer (Mobile):**  
  Anmeldung via E‑Mail + Passwort direkt in der App.

- **Administratoren (Web‑Admin):**  
  Anmeldung über eine eigene Maske.

  - **Initial‑Passwort:**  
    Admins werden manuell in Keycloak angelegt.

  - **Passwortänderungszwang:**  
    Beim ersten Login erkennt das System den Status *„temporäres Passwort“*  
    → Weiterleitung zur Keycloak‑UI zum Setzen eines neuen Passworts.

---

### 3. Passwort‑Management

- Benutzer können über „Passwort vergessen“ einen Reset‑Link anfordern.
- Der Prozess führt über eine Keycloak‑Webseite.
- Nach erfolgreicher Änderung erfolgt eine automatische Rückleitung:
  - Mobile: Deep‑Link
  - Web: definierte URL

---

### 4. Logout (Mobile & Web‑Admin)

- **Token‑Invalidierung:**  
  Beim Logout wird das Refresh‑Token an das Backend gesendet, um die Session in Keycloak zu beenden.

- **Client‑Bereinigung:**  
  Tokens werden lokal gelöscht (LocalStorage / SecureStorage).  
  Danach erfolgt ein Redirect zur Login‑Seite.

---

### 5. Datenspeicherung & Synchronisation (User Profil)

Das System verwaltet Benutzerdaten redundant in zwei Systemen:

- **Keycloak (IAM):**  
  Authentifizierungsdaten (E‑Mail, Passwort, Rollen)

- **PostgreSQL (App‑DB):**  
  Erweiterbare Profildaten (z. B. Leihlimit, Klasse, Status, Registrierungsdatum)

- **Synchronisation:**  
  Bei Registrierung oder Login werden Daten zwischen Keycloak und App‑DB synchronisiert.  
  Verknüpfung über eine eindeutige **externalId (Keycloak User ID)**.

---

## Technische Schnittstellen (REST‑API)

| Endpunkt | Methode | Beschreibung |
|---------|---------|--------------|
| `/api/v1/auth/mobile/login` | POST | Authentifizierung für Mobile‑Nutzer (E‑Mail/Passwort) |
| `/api/v1/auth/admin/login` | POST | Authentifizierung für Admins (mit Rollenprüfung `ADMIN`) |
| `/api/v1/registration` | POST | Erstellen eines neuen Benutzerkontos (Keycloak + DB) |
| `/api/v1/users/me` | GET | Abruf des angereicherten Benutzerprofils aus der Datenbank |
| `/api/v1/auth/password/reset-request` | POST | Auslösen der Passwort‑Reset‑E‑Mail |
| `/api/v1/auth/logout` | POST | Beenden der Session (Token‑Invalidierung) |

---

## Ablaufdiagramme (Workflows)

### A) Registrierung & Verifikation

Benutzer (Mobile)
→ POST /api/v1/registration
→ Backend erstellt User (status: disabled)
→ Keycloak erzeugt externalId
→ App‑DB speichert User‑Profil
→ Backend sendet Verifikations‑E‑Mail
Benutzer klickt auf Link
→ Keycloak aktiviert User (status: enabled)


---

### B) Admin‑Login mit Passwortänderung

Admin (Web)
→ POST /api/v1/auth/admin/login
→ Keycloak: temporäres Passwort erkannt
→ 401 Unauthorized (update_password)
Admin wird zur Keycloak‑UI weitergeleitet
→ Passwort ändern
→ Redirect zurück zur Web‑Admin‑App
→ erneuter Login

---

### C) Passwort vergessen (Reset)
Benutzer
→ POST /api/v1/auth/password/reset-request
→ Backend triggert Reset‑Action in Keycloak
→ Keycloak sendet Reset‑E‑Mail
Benutzer klickt Link
→ Passwort in Keycloak‑UI neu setzen
→ Redirect zurück zur App (Deep‑Link / URL)

---

### D) Logout

Benutzer
→ POST /api/v1/auth/logout (Refresh Token)
→ Backend invalidiert Session in Keycloak
→ OK
Client löscht Tokens
Redirect zur Login‑Seite


---

## Akzeptanzkriterien

- [ ] Login funktioniert erst nach E‑Mail‑Verifikation.  
- [ ] Admin‑Login erzwingt beim ersten Mal den Passwortwechsel via Keycloak.  
- [ ] Logout invalidiert das Token serverseitig und bereinigt den Client.  
- [ ] Passwort‑Reset führt den Nutzer nach Änderung zurück zur Anwendung.

---

## Definition of Done (DoD)

- [ ] Alle REST‑Endpunkte gemäß Spezifikation implementiert.  
- [ ] Unit‑ und Integrationstests für alle Workflows vorhanden.  
- [ ] Keycloak‑Realm und Clients sind konfiguriert.  
- [ ] UI‑Komponenten (Mobile & Web) sind mit den Endpunkten verknüpft.


