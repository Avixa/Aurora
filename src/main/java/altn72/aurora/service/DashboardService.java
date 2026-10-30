package altn72.aurora.service;

import altn72.aurora.modele.incident.AttentionLevel;
import altn72.aurora.modele.incident.Incident;
import altn72.aurora.modele.incident.IncidentRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    // the incident with its attention level
    public record ActiveIncident(Incident incident, AttentionLevel level, long hoursOpen) {}

    private final IncidentRepository incidentRepository;
    private final Clock clock;

    public DashboardService(IncidentRepository incidentRepository, Clock clock) {
        this.incidentRepository = incidentRepository;
        this.clock = clock;
    }

    // unresolved incidents only, oldest first
    public List<ActiveIncident> getActiveIncidents() {
        LocalDateTime now = LocalDateTime.now(clock);
        return incidentRepository.findOngoingWithDetails().stream()
                .map(incident -> toActiveIncident(incident, now))
                .toList();
    }

    // F11: a null criterion means "no filter"
    public List<ActiveIncident> filter(List<ActiveIncident> incidents, Long stationId, AttentionLevel level) {
        return incidents.stream()
                .filter(row -> stationId == null
                        || stationId.equals(row.incident().getEquipment().getStation().getId()))
                .filter(row -> level == null || row.level() == level)
                .toList();
    }

    // Every level is present, even at 0, so the view always shows the same four counters
    public Map<AttentionLevel, Long> countByLevel(List<ActiveIncident> incidents) {
        Map<AttentionLevel, Long> counts = new EnumMap<>(AttentionLevel.class);

        for (AttentionLevel level : AttentionLevel.values()) {
            counts.put(level, 0L);
        }

        incidents.forEach(row -> counts.merge(row.level(), 1L, Long::sum));
        return counts;
    }

    private ActiveIncident toActiveIncident(Incident incident, LocalDateTime now) {
        Duration openFor = Duration.between(incident.getOpeningDate(), now);
        boolean assigned = incident.getProvider() != null;

        return new ActiveIncident(incident, AttentionLevel.of(openFor, assigned), openFor.toHours());
    }
}