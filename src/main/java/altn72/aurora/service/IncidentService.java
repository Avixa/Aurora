package altn72.aurora.service;

import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.equipment.EquipmentRepository;
import altn72.aurora.modele.auth.UserRepository;
import altn72.aurora.modele.incident.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    public IncidentService(IncidentRepository incidentRepository,
                           EquipmentRepository equipmentRepository,
                           UserRepository userRepository) {
        this.incidentRepository = incidentRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
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

    private static class IncidentNotFoundException extends RuntimeException {
        public IncidentNotFoundException(Long id) {
            super("Incident " + id + " does not exist");
        }
    }
}