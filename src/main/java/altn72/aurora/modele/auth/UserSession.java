package altn72.aurora.modele.auth;

public record UserSession(Long id, String firstName, User.Profile profile) {}