package at.htl.ecotrack.administration.registration.domain;

import java.util.List;

import at.htl.ecotrack.shared.model.Role;

/**
 * Domain Service Port: Enthält die reinen Domänenregeln für die Registrierung.
 *
 * Verantwortlichkeiten:
 * - Prüfung, ob eine Registrierung mit der angegebenen E-Mail erlaubt ist
 * - Automatische Rollenzuweisung anhand der E-Mail-Domain
 */
public interface RegistrationService {

    /**
     * Prüft, ob eine Registrierung mit der angegebenen E-Mail erlaubt ist.
     * Eine Registrierung ist nur erlaubt, wenn die E-Mail-Domain
     * in der Liste der erlaubten Domains enthalten ist.
     *
     * @param email          die E-Mail-Adresse des Registrierenden
     * @param allowedDomains Liste der erlaubten Schul-Domains
     * @return true, wenn die Registrierung erlaubt ist
     */
    boolean isRegistrationAllowed(String email, List<AllowedDomain> allowedDomains);

    /**
     * Bestimmt die initiale Rolle anhand der E-Mail-Domain.
     * Lehrer-Domains -> LEHRER, ansonsten -> SCHUELER.
     *
     * @param email die E-Mail-Adresse
     * @return die zu vergebende Rolle
     */
    Role determineInitialRole(String email);
}
