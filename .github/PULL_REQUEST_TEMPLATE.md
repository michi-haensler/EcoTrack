## Beschreibung

<!-- Was wurde geändert und warum? Beschreibe den Kontext und die Motivation. -->

## Betroffene Komponenten

- [ ] 📱 Mobile App (React Native)
- [ ] 🌐 Admin Web (React)
- [ ] ⚙️ Backend Server (Spring Boot)
- [ ] 📦 Shared Resources (API-Contracts, Design Tokens)
- [ ] 🏗️ Infrastruktur (Docker, CI/CD)
- [ ] 📚 Dokumentation

## Art der Änderung

- [ ] 🐛 Bugfix (non-breaking change, behebt ein Problem)
- [ ] ✨ Neues Feature (non-breaking change, fügt Funktionalität hinzu)
- [ ] 💥 Breaking Change (fix oder feature, das bestehende Funktionalität ändert)
- [ ] 📝 Dokumentation
- [ ] 🔧 Refactoring (keine funktionale Änderung)
- [ ] 🧪 Tests

## Checkliste

### Allgemein
- [ ] Mein Code folgt den Code-Style Guidelines des Projekts
- [ ] Ich habe ein Self-Review meines Codes durchgeführt
- [ ] Ich habe meinen Code kommentiert, besonders an schwer verständlichen Stellen
- [ ] Ich habe entsprechende Änderungen an der Dokumentation vorgenommen
- [ ] Meine Änderungen erzeugen keine neuen Warnings

### Tests
- [ ] Ich habe Tests hinzugefügt, die beweisen, dass mein Fix effektiv ist oder mein Feature funktioniert
- [ ] Neue und bestehende Unit-Tests laufen lokal durch
- [ ] Integration Tests sind erfolgreich (falls zutreffend)

### API-Änderungen (falls zutreffend)
- [ ] Ich habe die OpenAPI-Spezifikation aktualisiert
- [ ] API-Clients wurden neu generiert (`npm run generate-api`)
- [ ] Die Änderungen sind abwärtskompatibel ODER Breaking Changes sind dokumentiert

## Screenshots / Videos

<!-- Füge Screenshots oder Videos hinzu, falls relevant (z.B. UI-Änderungen) -->

## Breaking Changes

<!-- Liste alle Breaking Changes und notwendige Migrationsschritte auf -->

## Zusätzliche Informationen

<!-- Gibt es noch etwas, das Reviewer wissen sollten? -->

---

⚠️ **Achtung bei Änderungen an `shared-resources/api-contracts/`:**  
Diese betreffen alle Plattformen und erfordern besondere Sorgfalt!
