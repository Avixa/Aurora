package altn72.aurora.service;

import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.equipment.EquipmentRepository;
import altn72.aurora.modele.incident.Incident;
import altn72.aurora.modele.incident.IncidentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final IncidentRepository incidentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository, IncidentRepository incidentRepository) {
        this.equipmentRepository = equipmentRepository;
        this.incidentRepository = incidentRepository;
    }

    // F3: equipment sheet
    public Optional<Equipment> getEquipment(Long id) {
        return equipmentRepository.findByIdWithDetails(id);
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
    public Incident declareIncident(Long equipmentId, Incident incident) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(EntityNotFoundException::new);
        incident.setId(null);
        incident.setEquipment(equipment);
        incident.setOpeningDate(null);
        incident.setDescription(incident.getDescription().trim());
        return incidentRepository.save(incident);
    }

}