package at.htl.ecotrack.administration.registration.api;

import at.htl.ecotrack.administration.registration.application.RegisterUserCommand;
import at.htl.ecotrack.administration.registration.application.RegistrationDtos;
import at.htl.ecotrack.administration.registration.application.UserRegistrationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * «Controller» (API-Layer): REST-Endpunkt für die Selbstregistrierung.
 *
 * Öffentlich zugängliche Endpunkte (kein JWT erforderlich):
 * <ul>
 * <li>POST /api/v2/registration — Neuen Benutzer registrieren</li>
 * <li>GET /api/v2/registration/email-check — E-Mail-Verfügbarkeit prüfen</li>
 * </ul>
 *
 * Delegiert die gesamte Geschäftslogik an {@link UserRegistrationService}.
 */
@RestController
@RequestMapping("/api/v2")
public class RegistrationController {

    private final UserRegistrationService userRegistrationService;

    public RegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    /**
     * Registriert einen neuen Benutzer mit Schul-E-Mail.
     *
     * @param request Registrierungsdaten (E-Mail, Passwort, Name, optional
     *                Klassen-UUID)
     * @return 201 Created mit userId, E-Mail und Bestätigungstext
     */
    @PostMapping("/registration")
    public ResponseEntity<RegistrationDtos.RegistrationResponseDto> register(
            @Valid @RequestBody RegistrationDtos.RegistrationRequestDto request) {

        RegisterUserCommand command = new RegisterUserCommand(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                request.classId());

        RegistrationDtos.RegistrationResponseDto response = userRegistrationService.registerUser(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Prüft, ob eine E-Mail-Adresse noch für die Registrierung verfügbar ist.
     * Nützlich für Echtzeit-Validierung im Frontend.
     *
     * @param email die zu prüfende E-Mail-Adresse
     * @return 200 OK mit Verfügbarkeitsstatus
     */
    @GetMapping("/registration/email-check")
    public RegistrationDtos.EmailAvailabilityDto checkEmailAvailability(
            @RequestParam @Email @NotBlank String email) {
        return userRegistrationService.checkEmailAvailability(email);
    }
}
