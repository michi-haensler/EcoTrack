package at.ecotrack.userprofile.entity;

/**
 * Gamification-Level (Baum-Wachstumsstufe).
 */
public enum Level {
    SETZLING("Setzling", 0),
    JUNGBAUM("Jungbaum", 200),
    BAUM("Baum", 500),
    ALTBAUM("Altbaum", 1000);

    private final String displayName;
    private final int minPoints;

    Level(String displayName, int minPoints) {
        this.displayName = displayName;
        this.minPoints = minPoints;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinPoints() {
        return minPoints;
    }

    /**
     * Bestimmt das Level basierend auf Punkten.
     */
    public static Level fromPoints(long points) {
        Level result = SETZLING;
        for (Level level : values()) {
            if (points >= level.minPoints) {
                result = level;
            }
        }
        return result;
    }
}
