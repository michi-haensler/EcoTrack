// ============================================================
// REST Controller Beispiel - ScoringController
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung eines
// REST Controllers im Adapter Layer.
// ============================================================

// -----------------------------
// Request DTO
// -----------------------------

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record LogActivityRequest(
        @NotNull UUID ecoUserId,
        @NotNull UUID actionDefinitionId,
        @Min(1) @Max(100) int quantity,
        @Size(max = 500) String notes) {
}

// -----------------------------
// REST Controller
// -----------------------------
@RestController
@RequestMapping("/api/scoring")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ScoringController {

    private final LogActivityUseCase logActivityUseCase;
    private final GetActivitiesUseCase getActivitiesUseCase;

    @PostMapping("/activities")
    public ResponseEntity<ActivityEntryDto> logActivity(
            @Valid @RequestBody LogActivityRequest request) {
        log.debug("POST /api/scoring/activities: {}", request);

        // Request → Command Mapping
        LogActivityCommand command = new LogActivityCommand(
                EcoUserId.of(request.ecoUserId()),
                ActionDefinitionId.of(request.actionDefinitionId()),
                request.quantity(),
                request.notes());

        // Use Case ausführen
        ActivityEntryDto result = logActivityUseCase.execute(command);

        // HTTP 201 Created zurückgeben
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @GetMapping("/activities/{id}")
    public ResponseEntity<ActivityEntryDto> getActivity(
            @PathVariable UUID id) {
        log.debug("GET /api/scoring/activities/{}", id);

        return getActivitiesUseCase.findById(ActivityEntryId.of(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/activities")
    public ResponseEntity<List<ActivityEntryDto>> getUserActivities(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("GET /api/scoring/users/{}/activities", userId);

        List<ActivityEntryDto> activities = getActivitiesUseCase
                .findByUser(EcoUserId.of(userId), page, size);

        return ResponseEntity.ok(activities);
    }
}

// -----------------------------
// Global Exception Handler
// -----------------------------
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "Invalid value"));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse("VALIDATION_FAILED", errors));
    }
}

// -----------------------------
// Error Response DTOs
// -----------------------------
public record ErrorResponse(
        String code,
        String message) {
}

public record ValidationErrorResponse(
        String code,
        Map<String, String> errors) {
}
