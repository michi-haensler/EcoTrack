# CI/CD Setup für EcoTrack

## 📋 Übersicht der erstellten Dateien

Dieses Dokument fasst alle erstellten CI/CD-Komponenten zusammen.

---

## 🔄 GitHub Actions Workflows

### 1. CI Workflow ([ci.yml](../.github/workflows/ci.yml))

**Trigger:** Push auf `main`/`develop`, Pull Requests

**Jobs:**
| Job | Beschreibung |
|-----|--------------|
| `code-quality` | Grundlegende Code-Qualitätsprüfungen |
| `backend-test` | Java/Spring Boot Tests mit PostgreSQL |
| `admin-web-test` | React/TypeScript Tests (Vitest) |
| `mobile-test` | React Native Tests (Jest) |
| `api-validation` | OpenAPI Spezifikation validieren |
| `security-scan` | Trivy Security Scanner |
| `ci-status` | Finaler Status-Check |

### 2. CD Workflow ([cd.yml](../.github/workflows/cd.yml))

**Trigger:** Push auf `main`, manuell

**Jobs:**
| Job | Beschreibung |
|-----|--------------|
| `ci` | CI Pipeline ausführen |
| `build-images` | Docker Images bauen |
| `deploy-staging` | Deploy auf Staging |
| `deploy-production` | Deploy auf Production (Manual Approval) |

### 3. PR Checks ([pr-checks.yml](../.github/workflows/pr-checks.yml))

**Trigger:** Pull Requests

**Jobs:**
| Job | Beschreibung |
|-----|--------------|
| `pr-validation` | PR Titel & Beschreibung prüfen |
| `changed-files` | Geänderte Dateien analysieren |
| `backend-tests` | Conditional Backend Tests |
| `admin-web-tests` | Conditional Frontend Tests |
| `mobile-tests` | Conditional Mobile Tests |

### 4. Coverage Report ([coverage.yml](../.github/workflows/coverage.yml))

**Trigger:** Push auf `main`/`develop`, Pull Requests

**Features:**
- Generiert Coverage-Reports für alle Komponenten
- Erstellt PR-Kommentar mit Coverage-Summary
- Speichert Reports als Artifacts

---

## 🔒 Branch Protection

Dokumentation: [BRANCH_PROTECTION_SETUP.md](../docs/BRANCH_PROTECTION_SETUP.md)

### Erforderliche Einstellungen:

| Einstellung | Wert |
|-------------|------|
| Require pull request | ✅ |
| Required approvals | 1 |
| Dismiss stale reviews | ✅ |
| Require status checks | ✅ |
| Require up-to-date branch | ✅ |
| Include administrators | ✅ |

### Required Status Checks:
- `✅ CI Status`
- `📋 PR Validation`

---

## 🤖 Dependabot ([dependabot.yml](../.github/dependabot.yml))

**Update Schedule:** Wöchentlich (Montag 06:00 Wien)

| Ecosystem | Verzeichnis |
|-----------|-------------|
| Maven | `/server` |
| npm | `/admin-web` |
| npm | `/mobile` |
| GitHub Actions | `/` |
| Docker | `/server`, `/admin-web` |

---

## 📝 Templates

### Test Implementation Checklist
[test-implementation-checklist.md](../.github/templates/test-implementation-checklist.md)

Enthält:
- Backend Test-Checkliste (Unit, Integration, E2E)
- Admin-Web Test-Checkliste
- Mobile Test-Checkliste
- Test-Dateien Struktur
- Coverage-Ziele

---

## 🚀 Workflow-Diagramm

```
┌─────────────────────────────────────────────────────────────────┐
│                         Developer                                │
│                            │                                     │
│                            ▼                                     │
│                    Feature Branch                                │
│                            │                                     │
│                            ▼                                     │
│               ┌────────────────────────┐                        │
│               │    Pull Request        │                        │
│               └───────────┬────────────┘                        │
│                           │                                      │
│           ┌───────────────┼───────────────┐                     │
│           ▼               ▼               ▼                     │
│    ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│    │PR Checks │    │    CI    │    │ Coverage │                │
│    └────┬─────┘    └────┬─────┘    └────┬─────┘                │
│         │               │               │                       │
│         └───────────────┼───────────────┘                       │
│                         │                                        │
│                         ▼                                        │
│              ┌──────────────────────┐                           │
│              │   All Checks Pass?   │                           │
│              └──────────┬───────────┘                           │
│                         │                                        │
│              ┌──────────┴───────────┐                           │
│              │                      │                           │
│              ▼ Yes                  ▼ No                        │
│    ┌─────────────────┐    ┌─────────────────┐                  │
│    │  Code Review    │    │   Fix Issues    │                  │
│    └────────┬────────┘    └─────────────────┘                  │
│             │                                                    │
│             ▼                                                    │
│    ┌─────────────────┐                                          │
│    │  Merge to main  │                                          │
│    └────────┬────────┘                                          │
│             │                                                    │
│             ▼                                                    │
│    ┌─────────────────┐                                          │
│    │       CD        │                                          │
│    │  ├─ Build       │                                          │
│    │  ├─ Staging     │                                          │
│    │  └─ Production  │                                          │
│    └─────────────────┘                                          │
└─────────────────────────────────────────────────────────────────┘
```

---

## ✅ Nächste Schritte

1. **Repository-Einstellungen konfigurieren:**
   - Branch Protection Rules aktivieren
   - Required Status Checks hinzufügen
   - CODEOWNERS überprüfen

2. **Secrets einrichten (falls benötigt):**
   - `GITHUB_TOKEN` (automatisch vorhanden)
   - Deployment-Secrets für Staging/Production

3. **Workflows testen:**
   - Einen Test-PR erstellen
   - Alle Workflows beobachten
   - Status Checks verifizieren

4. **Badge-URLs aktualisieren:**
   - In README.md `[OWNER]/[REPO]` ersetzen

---

## 📚 Referenzen

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Branch Protection Rules](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches)
- [EcoTrack Testing Standards](.github/instructions/testing.instructions.md)
- [Test Engineer Agent](.github/agents/test-engineer.agent.md)
