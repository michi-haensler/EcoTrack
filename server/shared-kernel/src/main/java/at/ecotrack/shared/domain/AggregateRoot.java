package at.ecotrack.shared.domain;

import java.util.UUID;

/**
 * Marker-Interface für Aggregate Roots.
 */
public interface AggregateRoot {
    UUID getId();
}
