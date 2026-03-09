package at.htl.ecotrack.administration.registration.application;

import java.util.UUID;

/**
 * Command-Objekt für die Selbstregistrierung eines Benutzers.
 *
 * Kapselt alle Eingabedaten für den Registrierungsvorgang.
 * Die Rolle wird NICHT vom Client bestimmt, sondern automatisch
 * durch den DefaultRegistrationService anhand der E-Mail-Domain ermittelt.
 *
 * @param email     Schul-E-Mail-Adresse (muss einer erlaubten Domain angehören)
 * @param password  Wunsch-Passwort (mindestens 8 Zeichen)
 * @param firstName Vorname
 * @param lastName  Nachname
 * @param classId   UUID der Klasse (optional, wird für SCHUELER automatisch
 *                  gesetzt)
 */
public record RegisterUserCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        UUID classId) {
}
