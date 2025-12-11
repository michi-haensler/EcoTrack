package at.ecotrack.scoring.domain.model;

/**
 * Mengeneinheit für Aktionen.
 */
public enum Unit {
    STUECK("Stück"),
    KM("Kilometer"),
    MINUTEN("Minuten"),
    KG("Kilogramm"),
    LITER("Liter");

    private final String displayName;

    Unit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
