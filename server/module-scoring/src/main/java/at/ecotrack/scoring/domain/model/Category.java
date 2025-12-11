package at.ecotrack.scoring.domain.model;

/**
 * Kategorie einer nachhaltigen Aktion.
 */
public enum Category {
    MOBILITAET("Mobilität"),
    KONSUM("Konsum"),
    RECYCLING("Recycling"),
    ENERGIE("Energie"),
    ERNAEHRUNG("Ernährung"),
    SONSTIGES("Sonstiges");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
