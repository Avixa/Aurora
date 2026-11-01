package altn72.aurora.api;

import altn72.aurora.modele.incident.Incident;

import java.time.LocalDateTime;

// F12: what the API exposes of an incident, instead of the JPA entity itself
public record IncidentResponse(
        Long id,
        String description,
        Incident.StatusEnum status,
        LocalDateTime openingDate,
        LocalDateTime resolutionDate,
        String equipmentReference,
        String stationName,
        String providerName) {

    public static IncidentResponse from(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getDescription(),
                incident.getStatus(),
                incident.getOpeningDate(),
                incident.getResolutionDate(),
                incident.getEquipment().getReference(),
                incident.getEquipment().getStation().getName(),
                incident.getProvider() == null ? null : incident.getProvider().getName());
    }
}