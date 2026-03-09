package at.htl.ecotrack.administration.registration.domain;

/**
 * Value Object: Repräsentiert eine erlaubte E-Mail-Domain für die
 * Selbstregistrierung.
 * Nur Nutzer mit einer Schul-E-Mail-Adresse dürfen sich registrieren.
 * Beispiel: "htl-leoben.at" → erlaubt @htl-leoben.at
 */
public record AllowedDomain(String domain) {

    public AllowedDomain {
        if (domain == null || domain.isBlank()) {
            throw new IllegalArgumentException("Domain darf nicht leer sein");
        }
        domain = domain.toLowerCase().trim();
    }

    /**
     * Prüft, ob die angegebene E-Mail-Adresse zu dieser Domain gehört.
     *
     * @param email die zu prüfende E-Mail-Adresse
     * @return true, wenn die E-Mail zur Domain passt
     */
    public boolean matches(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return email.toLowerCase().trim().endsWith("@" + domain);
    }
}
