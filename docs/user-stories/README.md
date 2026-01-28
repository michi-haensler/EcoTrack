# 📋 User Stories - EcoTrack

Dieses Verzeichnis enthält alle User Stories, aufgeteilt nach Arbeitsbereichen.

## Struktur

```
user-stories/
├── README.md           # Diese Datei
├── mobile/             # User Stories für Mobile App
│   └── README.md
├── admin-web/          # User Stories für Admin-Web
│   └── README.md
├── server/             # User Stories für Backend
│   └── README.md
└── all/                # Alle originalen User Stories
    ├── must-haves/     # Pflicht-Features (P1)
    └── should-haves/   # Wichtige Features (P2)
```

## Workspace-Zuordnung

| Workspace | User Stories Ordner |
|-----------|---------------------|
| `ecotrack-mobile.code-workspace` | [mobile/](mobile/) |
| `ecotrack-web.code-workspace` | [admin-web/](admin-web/) |
| `ecotrack-server.code-workspace` | [server/](server/) |
| `ecotrack-full.code-workspace` | Alle (dieser Ordner) |

## Übersicht

### Must-Haves (P1)

| ID | Titel | SP | Mobile | Web | Server |
|----|-------|----|:------:|:---:|:------:|
| M-US-1 | Registrierung & Login | 8 | ✅ | ✅ | ✅ |
| M-US-2 | Aktion erfassen | 13 | ✅ | | ✅ |
| M-US-3 | Punkte & Fortschritt | 8 | ✅ | | ✅ |
| M-US-4 | Ranglisten (Klasse) | 8 | ✅ | | ✅ |
| M-US-5 | Challenge anlegen | 14 | | ✅ | ✅ |
| M-US-6 | Dashboard für Lehrer | 10 | | ✅ | ✅ |
| M-US-7 | Nutzerverwaltung | 8 | | ✅ | ✅ |

### Should-Haves (P2)

| ID | Titel | SP | Mobile | Web | Server |
|----|-------|----|:------:|:---:|:------:|
| S-US-1 | Profil & Zuordnung | 5 | ✅ | | ✅ |
| S-US-2 | Aktionskatalog & Verlauf | 13 | ✅ | ✅ | ✅ |
| S-US-3 | Meilenstein-Feedback | 3 | ✅ | | ✅ |
| S-US-4 | Schul-Rangliste & Filter | 8 | ✅ | ✅ | ✅ |
| S-US-5 | Challenge-Vorlagen | 5 | | ✅ | ✅ |
| S-US-6 | Belohnungssystem | 8 | ✅ | | ✅ |
| S-US-7 | Schul-/Gemeinde-Dashboard | 8 | | ✅ | ✅ |
| S-US-8 | Benutzerverwaltung (Bulk) | 13 | | ✅ | ✅ |

## Story Points nach Bereich

| Bereich | Must-Have | Should-Have | Gesamt |
|---------|-----------|-------------|--------|
| Mobile | 37 | 37 | **74** |
| Admin-Web | 40 | 47 | **87** |
| Server | 69 | 63 | **132** |
