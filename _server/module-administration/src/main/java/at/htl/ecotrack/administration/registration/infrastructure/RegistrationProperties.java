package at.htl.ecotrack.administration.registration.infrastructure;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Konfigurationseigenschaften für die Selbstregistrierung.
 *
 * Konfiguration in application.yml:
 * 
 * <pre>
 * ecotrack:
 *   registration:
 *     allowed-domains:
 *       - htl-leoben.at
 *       - schueler.htl-leoben.at
 *       - lehrer.htl-leoben.at
 * </pre>
 *
 * @param allowedDomains Liste der erlaubten Schul-E-Mail-Domains
 */
@ConfigurationProperties(prefix = "ecotrack.registration")
public record RegistrationProperties(List<String> allowedDomains) {

    public RegistrationProperties {
        if (allowedDomains == null) {
            allowedDomains = List.of();
        }
    }
}
