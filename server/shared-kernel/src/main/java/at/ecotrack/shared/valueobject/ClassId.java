package at.ecotrack.shared.valueobject;

import java.util.UUID;

/**
 * Typisierte Class-ID für typsichere Referenzen zwischen Modulen.
 */
public record ClassId(UUID value) {

    public static ClassId of(UUID value) {
        return new ClassId(value);
    }

    public static ClassId generate() {
        return new ClassId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
