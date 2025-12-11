package at.ecotrack.shared.valueobject;

import java.util.UUID;

/**
 * Typisierte EcoUser-ID für typsichere Referenzen zwischen Modulen.
 */
public record EcoUserId(UUID value) {

    public static EcoUserId of(UUID value) {
        return new EcoUserId(value);
    }

    public static EcoUserId generate() {
        return new EcoUserId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
