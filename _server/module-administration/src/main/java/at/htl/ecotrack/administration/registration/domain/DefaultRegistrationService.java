package at.htl.ecotrack.administration.registration.domain;

import java.util.List;

import org.springframework.stereotype.Service;

import at.htl.ecotrack.shared.model.Role;

/**
 * «DomainService» — Konkrete Implementierung der Domänen-Registrierungsregeln.
 *
 * Rollenlogik für EcoTrack:
 * - Domains die "lehrer" enthalten oder mit Lehrer-Domains enden → LEHRER
 * - Alle anderen erlaubten Domains → SCHUELER
 */
@Service
public class DefaultRegistrationService implements RegistrationService {

    @Override
    public boolean isRegistrationAllowed(String email, List<AllowedDomain> allowedDomains) {
        if (email == null || email.isBlank()) {
            return false;
        }
        if (allowedDomains == null || allowedDomains.isEmpty()) {
            return false;
        }
        return allowedDomains.stream().anyMatch(d -> d.matches(email));
    }

    @Override
    public Role determineInitialRole(String email) {
        if (email == null) {
            return Role.SCHUELER;
        }
        String lower = email.toLowerCase().trim();
        // Lehrer-Domains: Adressen die "lehrer" im Subdomain-Teil enthalten
        if (lower.contains("lehrer.") || lower.endsWith(".lehrer")) {
            return Role.LEHRER;
        }
        return Role.SCHUELER;
    }
}
