package altn72.aurora.modele.incident;

import java.time.Duration;

/**
 * Attention level of an active incident on the dashboard.
 * Computed from the current time on each display, never stored in the database.
 */
public enum AttentionLevel {
    GREEN("Vert"),
    ORANGE("Orange"),
    RED("Rouge"),
    ASSIGNED("Pris en charge");

    public static final Duration ORANGE_THRESHOLD = Duration.ofHours(48);
    public static final Duration RED_THRESHOLD = Duration.ofHours(72);

    private final String label;

    AttentionLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // An assigned incident never gets green, orange or red, whatever its age
    public static AttentionLevel of(Duration openFor, boolean assigned) {
        if (assigned) {
            return ASSIGNED;
        }
        if (openFor.compareTo(RED_THRESHOLD) >= 0) {
            return RED;
        }
        if (openFor.compareTo(ORANGE_THRESHOLD) >= 0) {
            return ORANGE;
        }
        return GREEN;
    }
}