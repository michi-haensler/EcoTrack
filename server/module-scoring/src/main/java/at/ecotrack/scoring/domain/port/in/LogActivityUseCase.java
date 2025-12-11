package at.ecotrack.scoring.domain.port.in;

import at.ecotrack.scoring.application.command.LogActivityCommand;
import at.ecotrack.scoring.application.dto.ActivityEntryDto;

/**
 * Use Case Port: Aktivität erfassen.
 */
public interface LogActivityUseCase {
    ActivityEntryDto handle(LogActivityCommand command);
}
