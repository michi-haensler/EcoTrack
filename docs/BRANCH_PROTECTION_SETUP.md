# GitHub Branch Protection Einrichtung für EcoTrack

## 🔒 Branch Protection Rules

Diese Dokumentation beschreibt die erforderlichen Branch Protection Rules, um CI/CD im main Branch zu gewährleisten.

### Voraussetzungen

1. Repository-Admin-Rechte
2. GitHub Actions müssen aktiviert sein
3. Die CI/CD Workflows müssen gepusht sein

---

## 📋 Einrichtung im GitHub Repository

### Schritt 1: Branch Protection Rules öffnen

1. Gehe zu **Settings** → **Branches**
2. Klicke auf **Add branch protection rule**

### Schritt 2: Main Branch schützen

**Branch name pattern:** `main`

#### Aktiviere folgende Optionen:

| Option | Einstellung | Beschreibung |
|--------|-------------|--------------|
| ✅ Require a pull request before merging | Aktiviert | Direktes Pushen auf main verhindern |
| ├─ Require approvals | 1 | Mindestens 1 Reviewer erforderlich |
| ├─ Dismiss stale pull request approvals | Aktiviert | Bei neuen Commits neu reviewen |
| └─ Require review from Code Owners | Optional | CODEOWNERS-Datei verwenden |
| ✅ Require status checks to pass | Aktiviert | **WICHTIG für CI/CD** |
| ├─ Require branches to be up to date | Aktiviert | Branch muss aktuell sein |
| └─ Status checks: | Siehe unten | Erforderliche Checks |
| ✅ Require conversation resolution | Aktiviert | Alle Kommentare müssen gelöst sein |
| ✅ Require signed commits | Optional | GPG-signierte Commits |
| ✅ Include administrators | Empfohlen | Admins müssen auch Rules befolgen |
| ❌ Allow force pushes | Deaktiviert | Keine Force Pushes |
| ❌ Allow deletions | Deaktiviert | Branch kann nicht gelöscht werden |

### Schritt 3: Required Status Checks konfigurieren

Füge folgende Status Checks hinzu (erscheinen nach dem ersten Workflow-Lauf):

```
✅ CI Status (ci.yml)
✅ PR Status (pr-checks.yml)
```

**Detaillierte Checks:**

| Check Name | Workflow | Beschreibung |
|------------|----------|--------------|
| `✅ CI Status` | ci.yml | Haupt-CI-Status-Check |
| `🧪 Backend Tests` | ci.yml | Java/Spring Boot Tests |
| `🧪 Admin-Web Tests` | ci.yml | React/TypeScript Tests |
| `🧪 Mobile Tests` | ci.yml | React Native Tests |
| `📝 API Contract Validation` | ci.yml | OpenAPI Validierung |
| `📋 PR Validation` | pr-checks.yml | PR Titel & Beschreibung |

---

## 📋 Develop Branch schützen

**Branch name pattern:** `develop`

| Option | Einstellung |
|--------|-------------|
| ✅ Require a pull request before merging | Aktiviert |
| ├─ Require approvals | 1 |
| ✅ Require status checks to pass | Aktiviert |
| └─ Status checks: `✅ CI Status` | Erforderlich |

---

## 🔧 Ruleset Alternative (GitHub UI)

> ⚠️ **Wichtig:** Rulesets können **nicht** über Dateien im Repository konfiguriert werden. 
> Sie müssen über die GitHub Web-Oberfläche eingerichtet werden.

### Ruleset über GitHub UI erstellen:

1. Gehe zu **Settings** → **Rules** → **Rulesets**
2. Klicke auf **New ruleset** → **New branch ruleset**
3. Konfiguriere folgende Einstellungen:

| Feld | Wert |
|------|------|
| **Ruleset Name** | `EcoTrack Main Protection` |
| **Enforcement status** | `Active` |
| **Target branches** | Add target → Include by pattern: `main`, `develop` |

### Rules hinzufügen:

#### 1. Restrict deletions
- ✅ Aktivieren

#### 2. Require a pull request before merging
- ✅ Aktivieren
- Required approvals: `1`
- ✅ Dismiss stale pull request approvals when new commits are pushed
- ✅ Require approval of the most recent reviewable push

#### 3. Require status checks to pass
- ✅ Aktivieren
- ✅ Require branches to be up to date before merging
- **Status checks hinzufügen:**
  - Suche nach `CI Status` und füge hinzu
  - Suche nach `PR Status` und füge hinzu

#### 4. Block force pushes
- ✅ Aktivieren

### Speichern
Klicke auf **Create** um das Ruleset zu aktivieren.

---

## 🏷️ CODEOWNERS Datei

Die Datei `.github/CODEOWNERS` ist bereits vorhanden und definiert:

```
# Global Owners
* @ecotrack-team

# Backend
/server/ @backend-team

# Frontend Web
/admin-web/ @frontend-team

# Mobile
/mobile/ @mobile-team
```

---

## 📊 Workflow Übersicht

```
┌─────────────────────────────────────────────────────────────┐
│                    Pull Request erstellt                     │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│  PR Checks Workflow (pr-checks.yml)                         │
│  ├─ PR Title Validation (Semantic)                          │
│  ├─ PR Description Check                                    │
│  ├─ Changed Files Analysis                                  │
│  └─ Conditional Tests (nur geänderte Module)                │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│  CI Workflow (ci.yml)                                       │
│  ├─ Code Quality Checks                                     │
│  ├─ Backend Tests (JUnit 5 + Jacoco)                       │
│  ├─ Admin-Web Tests (Vitest)                               │
│  ├─ Mobile Tests (Jest)                                    │
│  ├─ API Validation                                         │
│  └─ Security Scan (Trivy)                                  │
└─────────────────────┬───────────────────────────────────────┘
                      │
            ┌─────────┴─────────┐
            │ Alle Checks ✅?   │
            └─────────┬─────────┘
                      │
          ┌───────────┴───────────┐
          │                       │
          ▼ JA                    ▼ NEIN
┌─────────────────────┐   ┌─────────────────────┐
│  Code Review        │   │  ❌ Merge blockiert │
│  erforderlich       │   │  → Fixes nötig      │
└─────────┬───────────┘   └─────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────┐
│  Merge in main Branch                                        │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│  CD Workflow (cd.yml)                                       │
│  ├─ CI Pipeline ausführen                                   │
│  ├─ Docker Images bauen                                     │
│  ├─ Deploy to Staging                                       │
│  ├─ Smoke Tests                                             │
│  └─ Deploy to Production (Manual Approval)                  │
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ Checkliste für Einrichtung

- [ ] CI Workflow gepusht (`.github/workflows/ci.yml`)
- [ ] CD Workflow gepusht (`.github/workflows/cd.yml`)
- [ ] PR Checks Workflow gepusht (`.github/workflows/pr-checks.yml`)
- [ ] Branch Protection für `main` aktiviert
- [ ] Branch Protection für `develop` aktiviert
- [ ] Required Status Checks konfiguriert
- [ ] CODEOWNERS Datei vorhanden
- [ ] Team über neue Regeln informiert

---

## 🆘 Troubleshooting

### Status Checks erscheinen nicht

1. Workflow muss mindestens einmal gelaufen sein
2. Job-Namen müssen exakt übereinstimmen
3. Prüfe Workflow-Logs auf Fehler

### "Required status check is expected"

1. Stelle sicher, dass der Check-Name exakt übereinstimmt
2. Workflow muss auf den korrekten Events triggern
3. Prüfe `concurrency` Einstellungen

### Bypass für Notfälle

**Nur für Admins mit aktiviertem "Include administrators":**
- Temporär Rule deaktivieren
- Nach Fix sofort wieder aktivieren
- Im PR dokumentieren warum

---

## 📚 Weiterführende Links

- [GitHub Branch Protection Docs](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Status Checks Docs](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/collaborating-on-repositories-with-code-quality-features/about-status-checks)
