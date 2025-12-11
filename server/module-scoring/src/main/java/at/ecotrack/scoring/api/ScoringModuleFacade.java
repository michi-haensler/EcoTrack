package at.ecotrack.scoring.api;

import at.ecotrack.scoring.application.dto.ActivityEntryDto;
import at.ecotrack.shared.valueobject.EcoUserId;

import java.time.LocalDate;
import java.util.List;

/**
 * Öffentliche API des Scoring-Moduls.
 * Nur diese Schnittstelle darf von anderen Modulen verwendet werden.
 */
public interface ScoringModuleFacade {

    /**
     * Gibt die letzten Aktivitäten eines Users zurück.
     */
    List<ActivityEntryDto> getRecentActivities(EcoUserId ecoUserId, int limit);

    /**
     * Gibt die Summe der Punkte eines Users in einem Zeitraum zurück.
     */
    int getPointsInPeriod(EcoUserId ecoUserId, LocalDate from, LocalDate to);
}
