package altn72.aurora.service;

import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.equipment.EquipmentRepository;
import altn72.aurora.modele.auth.UserRepository;
import altn72.aurora.modele.incident.*;
import altn72.aurora.modele.provider.Provider;
import altn72.aurora.modele.provider.ProviderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.time.Clock;

@Service
@Transactional(readOnly = true)
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final Clock clock;

    public IncidentService(IncidentRepository incidentRepository, EquipmentRepository equipmentRepository,
                           UserRepository userRepository, ProviderRepository providerRepository,
                           Clock clock) {
        this.incidentRepository = incidentRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.clock = clock;
    }

    // F4
    @Transactional
    public Incident declareIncident(Long equipmentId, Incident incident, Long declarantId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EquipmentService.EquipmentNotFoundException(equipmentId));
        incident.setId(null);
        incident.setEquipment(equipment);
        incident.setDeclarant(userRepository.getReferenceById(declarantId));
        incident.setStatus(Incident.StatusEnum.NON_AFFECTE);
        incident.setProvider(null);
        incident.setAffectationDate(null);
        incident.setResolutionDate(null);
        incident.setDescription(incident.getDescription().trim());
        return incidentRepository.save(incident);
    }

    // F5
    public Incident getIncident(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
    }

    public List<Incident> getIncidents() {
        return incidentRepository.findAll();
    }

    @Transactional
    public Incident assignProvider(Long incidentId, Long providerId) {
        Incident incident = getIncident(incidentId);
        if (incident.getStatus() == Incident.StatusEnum.RESOLU) {
            throw new ResolvedIncidentException(incidentId);
        }

        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException(providerId));
        incident.setProvider(provider);
        incident.setStatus(Incident.StatusEnum.AFFECTE);
        incident.setAffectationDate(LocalDateTime.now());
        return incidentRepository.save(incident);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ProviderNotFoundException extends RuntimeException {
        public ProviderNotFoundException(Long id) {
            super("Provider " + id + " does not exist");
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ResolvedIncidentException extends RuntimeException {
        public ResolvedIncidentException(Long id) {
            super("L'incident " + id + " est déjà résolu.");
        }
    }

    private static class IncidentNotFoundException extends RuntimeException {
        public IncidentNotFoundException(Long id) {
            super("Incident " + id + " does not exist");
        }
    }

    // records the resolution date; a resolved incident cannot be resolved again
    @Transactional
    public Incident resolve(Long incidentId) {
        Incident incident = getIncident(incidentId);
        if (incident.getStatus() == Incident.StatusEnum.RESOLU) {
            throw new ResolvedIncidentException(incidentId);
        }
        incident.setStatus(Incident.StatusEnum.RESOLU);
        incident.setResolutionDate(LocalDateTime.now(clock));
        return incidentRepository.save(incident);
    }

    public List<Incident> getResolvedIncidents() {
        return incidentRepository.findResolvedWithDetails();
    }
}