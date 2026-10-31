package altn72.aurora.modele.auth;

import java.util.Set;

// each protected action and the profiles allowed to perform it
public enum Permission {
    DECLARE_INCIDENT("déclarer un incident", User.Profile.AGENT),
    RESOLVE_INCIDENT("déclarer un incident résolu", User.Profile.AGENT),
    EVALUATE_PROVIDER("évaluer un prestataire", User.Profile.AGENT);

    private final String action;
    private final Set<User.Profile> grantedProfiles;

    Permission(String action, User.Profile... grantedProfiles) {
        this.action = action;
        this.grantedProfiles = Set.of(grantedProfiles);
    }

    public boolean isGrantedTo(User.Profile profile) {
        return grantedProfiles.contains(profile);
    }

    public String getAction() {
        return action;
    }
}