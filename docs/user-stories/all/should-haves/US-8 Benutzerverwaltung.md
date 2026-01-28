# Card: Benutzerverwaltung – Bulk-Import & Passwort-Reset

# (kombiniert)

**User Story:** Als Admin/Nutzer möchte ich Benutzer massenhaft per CSV anlegen und
Passwörter sicher zurücksetzen können, damit Klassen/Schulen effizient eingerichtet
werden und der Zugang bei Bedarf schnell und sicher wiederhergestellt wird.
**Storypoints:** 8 (Bulk-Import) + 5 (Passwort-Reset) ⇒ **13
Priorität:** Should-Have (P2)
**Epic:** Administration & Onboarding/Auth

## Conversation

- **Bulk-Import (Admin):** CSV-Schema name,email,rolle,klasse,schule; Upload mit
    **Vorschau/Validierung** und **Teil-Import** ; eindeutige E-Mails; Ergebnisübersicht
    (erstellt/übersprungen/fehlerhaft); Fehler-CSV für Korrekturen.
- **Passwort-Reset (Nutzer):** „Passwort vergessen?“ versendet **zeitlich begrenzten**
    Einmal-Link; Policy für neue Passwörter; nach Reset werden **alte Sessions invalidiert**.

## Confirmation (Akzeptanzkriterien)

**A) Funktional – Bulk-Import**

1. **Upload & Schema**
    1.1 Upload akzeptiert nur **CSV** nach Schema name,email,rolle,klasse,schule.
    1.2 Bei fehlenden/spaltig falschen Feldern erscheint eine **klare Schema-**
    **Fehlermeldung** (welche Spalte/Zeile betroffen ist).
2. **Vorschau & Validierung**
    2.1 Nach Upload zeigt eine **Vorschau-Tabelle** jede Zeile mit Validierungsstatus
    (ok/fehlerhaft).
    2.2 Validierungen: E-Mail-Format, **Eindeutigkeit der E-Mail** (gegen DB & innerhalb
    der Datei), erlaubte Rollenwerte, Pflichtfelder nicht leer.
    2.3 Fehlerhafte Zeilen sind **rot markiert** und mit verständlicher Fehlermeldung
    versehen.
3. **Teil-Import & Idempotenz**
    3.1 Der Admin kann **trotz Fehlern** den **Teil-Import** starten: nur gültige Zeilen werden
    erstellt; fehlerhafte bleiben unberührt.


```
3.2 Bereits existierende E-Mails werden übersprungen ; in der Ergebnisübersicht
steht „übersprungen (bereits vorhanden)“.
```
4. **Ergebnis & Fehler-CSV**
    4.1 Nach Import erscheint eine **Ergebnisübersicht** mit Zählern: erstellt /
    übersprungen / fehlerhaft.
    4.2 Ein **Download „Fehler-CSV“** enthält nur die fehlerhaften Zeilen inkl. Fehlerspalte
    zur Korrektur.

**B) Funktional – Passwort-Reset**

5. **Reset anfordern**
    5.1 Formular „Passwort vergessen?“ prüft **E-Mail-Format** und zeigt **immer** eine
    neutrale Bestätigung („Wenn ein Konto existiert, wurde eine E-Mail versendet.“).
    5.2 System erstellt einen **Einmal-Token** (gültig **30 Minuten** ) und sendet einen Link
    per E-Mail.
6. **Zurücksetzen & Policy**
    6.1 Der Reset-Link öffnet ein Formular, das den Token prüft (gültig, nicht verbraucht,
    nicht abgelaufen).
    6.2 Neues Passwort muss Policy erfüllen (mind. 8 Zeichen, Groß/Klein, Zahl **oder**
    Sonderzeichen).
    6.3 Nach Erfolg werden **alle aktiven Sessions** des Nutzers invalidiert; Redirect zum
    Login mit Erfolgsmeldung.

## Nicht-funktional (beide Funktionen)

- **Performance & Skalierung**
    o Bulk-Import: bis **5.000 Zeilen** pro Upload; **< 60 s** Gesamtverarbeitung (Server-
       Side Streaming/Batch).
    o Vorschau rendert ohne merkliche Verzögerung; Passwort-Reset-Flow reagiert
       in **< 2 s** pro Schritt.
- **Sicherheit & Missbrauchsschutz**
    o Reset-Token **kryptografisch stark** , zufällig, **einmal verwendbar** ; sicher
       gespeichert (Hash/Expirations).
    o Rate-Limit: max. **3 Reset-Anfragen/15 min** pro E-Mail/Client-IP.
    o CSV-Import nur für **Admin-Rollen** ; serverseitige Validierung &
       **transaktionssichere** Batch-Writes (Fehler fallen rollensicher zurück).
- **Audit & Nachvollziehbarkeit**
    o Bulk-Import: **Uploader, Datei-Hash, Zeitpunkt** , Anzahl
       erstellt/übersprungen/fehlerhaft werden geloggt.


```
o Passwort-Reset: Ereignisse geloggt ( ohne Token im Log): Anfrage, Mail
versendet, Reset erfolgreich/fehlgeschlagen.
```
- **UX & Barrierefreiheit**
    o Klare, verständliche Meldungen; Tabellen in Vorschau **tastaturbedienbar** ,
       ausreichende Kontraste; responsives Layout.
    o Fehler-CSV nutzt UTF-8 und enthält Originalspalten + error_message.
- **Datenqualität**
    o Rollenwerte werden gegen erlaubte Liste validiert; Klassen/Schulen müssen
       existieren oder werden (konfigurierbar) erstellt/zugeordnet, sonst
       Fehlereintrag.
    o Idempotente Verarbeitung verhindert **Doppelanlage** bei erneutem Import
       derselben Datei.


