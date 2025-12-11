package at.ecotrack.shared.valueobject;

import java.util.UUID;

/**
 * Typisierte User-ID für typsichere Referenzen zwischen Modulen.
 */
public record UserId(UUID value) {

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
