package at.htl.ecotrack.administration.registration.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import at.htl.ecotrack.shared.model.Role;

/**
 * Unit Tests für {@link DefaultRegistrationService}.
 *
 * Testet die reinen Domänenregeln ohne Spring-Kontext.
 */
class DefaultRegistrationServiceTest {

    private DefaultRegistrationService registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new DefaultRegistrationService();
    }

    // --- isRegistrationAllowed Tests ---

    @Test
    void should_allowRegistration_when_emailMatchesAllowedDomain() {
        // Arrange
        List<AllowedDomain> allowedDomains = List.of(new AllowedDomain("htl-leoben.at"));

        // Act
        boolean result = registrationService.isRegistrationAllowed("max@htl-leoben.at", allowedDomains);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void should_denyRegistration_when_emailDoesNotMatchAllowedDomain() {
        // Arrange
        List<AllowedDomain> allowedDomains = List.of(new AllowedDomain("htl-leoben.at"));

        // Act
        boolean result = registrationService.isRegistrationAllowed("max@gmail.com", allowedDomains);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void should_denyRegistration_when_allowedDomainsListIsEmpty() {
        // Arrange & Act
        boolean result = registrationService.isRegistrationAllowed("max@htl-leoben.at", List.of());

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void should_denyRegistration_when_emailIsNull() {
        // Arrange
        List<AllowedDomain> allowedDomains = List.of(new AllowedDomain("htl-leoben.at"));

        // Act
        boolean result = registrationService.isRegistrationAllowed(null, allowedDomains);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void should_allowRegistration_when_emailMatchesOneOfMultipleDomains() {
        // Arrange
        List<AllowedDomain> allowedDomains = List.of(
                new AllowedDomain("htl-leoben.at"),
                new AllowedDomain("schueler.htl-leoben.at"),
                new AllowedDomain("lehrer.htl-leoben.at"));

        // Act & Assert
        assertThat(registrationService.isRegistrationAllowed("anna@schueler.htl-leoben.at", allowedDomains)).isTrue();
        assertThat(registrationService.isRegistrationAllowed("herr@lehrer.htl-leoben.at", allowedDomains)).isTrue();
        assertThat(registrationService.isRegistrationAllowed("info@outlook.com", allowedDomains)).isFalse();
    }

    @Test
    void should_allowRegistration_caseInsensitive() {
        // Arrange
        List<AllowedDomain> allowedDomains = List.of(new AllowedDomain("htl-leoben.at"));

        // Act
        boolean result = registrationService.isRegistrationAllowed("Max.Mustermann@HTL-LEOBEN.AT", allowedDomains);

        // Assert
        assertThat(result).isTrue();
    }

    // --- determineInitialRole Tests ---

    @Test
    void should_returnSchueler_when_emailDoesNotContainLehrerSubdomain() {
        // Arrange & Act
        Role role = registrationService.determineInitialRole("max@schueler.htl-leoben.at");

        // Assert
        assertThat(role).isEqualTo(Role.SCHUELER);
    }

    @Test
    void should_returnLehrer_when_emailContainsLehrerSubdomain() {
        // Arrange & Act
        Role role = registrationService.determineInitialRole("frau.huber@lehrer.htl-leoben.at");

        // Assert
        assertThat(role).isEqualTo(Role.LEHRER);
    }

    @Test
    void should_returnSchueler_when_emailIsNull() {
        // Act
        Role role = registrationService.determineInitialRole(null);

        // Assert
        assertThat(role).isEqualTo(Role.SCHUELER);
    }

    @Test
    void should_returnSchueler_for_genericHtlEmail() {
        // Act
        Role role = registrationService.determineInitialRole("schueler@htl-leoben.at");

        // Assert
        assertThat(role).isEqualTo(Role.SCHUELER);
    }
}
