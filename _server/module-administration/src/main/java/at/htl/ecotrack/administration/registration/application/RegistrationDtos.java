package at.htl.ecotrack.administration.registration.application;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTOs für die Self-Registration API.
 */
public class RegistrationDtos {

    /**
     * Eingehende Registrierungsanfrage vom Client.
     *
     * @param email     Schul-E-Mail-Adresse
     * @param password  Passwort (mindestens 8 Zeichen)
     * @param firstName Vorname
     * @param lastName  Nachname
     * @param classId   Klassen-UUID (optional, für Schüler empfohlen)
     */
    public record RegistrationRequestDto(
            @Email(message = "Keine gültige E-Mail-Adresse") @NotBlank(message = "E-Mail ist erforderlich") String email,
            @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen haben") @NotBlank(message = "Passwort ist erforderlich") String password,
            @NotBlank(message = "Vorname ist erforderlich") String firstName,
            @NotBlank(message = "Nachname ist erforderlich") String lastName,
            UUID classId) {
    }

    /**
     * Antwort nach erfolgreicher Registrierung.
     *
     * @param userId  die zugewiesene Benutzer-UUID (= Keycloak-ID)
     * @param email   die registrierte E-Mail-Adresse
     * @param message Bestätigungstext für den Benutzer
     */
    public record RegistrationResponseDto(UUID userId, String email, String message) {
    }

    /**
     * Antwort für die E-Mail-Verfügbarkeitsprüfung.
     *
     * @param email     die geprüfte E-Mail-Adresse
     * @param available true, wenn die E-Mail noch nicht registriert ist
     */
    public record EmailAvailabilityDto(String email, boolean available) {
    }

    private RegistrationDtos() {
    }
}
