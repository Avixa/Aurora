package altn72.aurora.service;

import altn72.aurora.modele.incident.IncidentEvent;
import altn72.aurora.modele.incident.IncidentEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class IncidentEventService {

    private final IncidentEventRepository incidentEventRepository;

    public IncidentEventService(IncidentEventRepository incidentEventRepository) {
        this.incidentEventRepository = incidentEventRepository;
    }

    // who did what on the incident, and when, oldest first
    public List<IncidentEvent> getJournal(Long incidentId) {
        return incidentEventRepository.findJournal(incidentId);
    }
}