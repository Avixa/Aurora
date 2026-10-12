package altn72.aurora.service;

import altn72.aurora.modele.equipment.Equipment;
//import altn72.aurora.modele.incident.Incident;
import altn72.aurora.modele.equipment.EquipmentRepository;
//import altn72.aurora.modele.incident.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    //private final IncidentRepository incidentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
        //this.incidentRepository = incidentRepository;
    }

    // F3: equipment sheet
    public Optional<Equipment> getEquipment(Long id) {
        return equipmentRepository.findByIdWithDetails(id);
    }

//    public List<Incident> getOngoingIncidents(Long equipmentId) {
//        //return incidentRepository.findByEquipmentIdAndClosedAtIsNullOrderByOpenedAtDesc(equipmentId);
//    }
//
//    public List<Incident> getPastIncidents(Long equipmentId) {
//        return incidentRepository.findByEquipmentIdAndClosedAtIsNotNullOrderByOpenedAtDesc(equipmentId);
//    }
}