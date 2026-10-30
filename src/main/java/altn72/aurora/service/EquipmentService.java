package altn72.aurora.service;

import altn72.aurora.modele.auth.UserRepository;
import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.equipment.EquipmentRepository;
import altn72.aurora.modele.incident.Incident;
import altn72.aurora.modele.incident.IncidentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    public EquipmentService(EquipmentRepository equipmentRepository, IncidentRepository incidentRepository, UserRepository userRepository) {
        this.equipmentRepository = equipmentRepository;
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    // F3: equipment sheet
    public Equipment getEquipment(Long id) {
        return equipmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EquipmentNotFoundException(id));
    }
    //F3
    public List<Incident> getOngoingIncidents(Long equipmentId) {
        return incidentRepository.findOngoingByEquipmentId(equipmentId);
    }

    //F3
    public List<Incident> getPastIncidents(Long equipmentId) {
        return incidentRepository.findPastByEquipmentId(equipmentId);
    }

    //F4
    @Transactional
    public Incident declareIncident(Long equipmentId, Incident incident, Long declarantId) {
        Equipment equipment = getEquipment(equipmentId);
        incident.setId(null);
        incident.setEquipment(equipment);
        incident.setStatus(Incident.StatusEnum.NON_AFFECTE);
        incident.setProvider(null);
        incident.setAffectationDate(null);
        incident.setResolutionDate(null);
        incident.setDescription(incident.getDescription().trim());
        incident.setDeclarant(userRepository.getReferenceById(declarantId));
        return incidentRepository.save(incident);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class EquipmentNotFoundException extends RuntimeException {
        public EquipmentNotFoundException(Long id) {
            super("Equipment " + id + " does not exist");
        }
    }
}


