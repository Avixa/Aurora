package altn72.aurora.service;

import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.station.Station;
import altn72.aurora.modele.equipment.EquipmentRepository;
import altn72.aurora.modele.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationRepository stationRepository;
    private final EquipmentRepository equipmentRepository;

    public StationService(StationRepository stationRepository, EquipmentRepository equipmentRepository) {
        this.stationRepository = stationRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public List<Station> getStations() {
        return stationRepository.findAll();
    }

    public Optional<Station> getStation(Long id) {
        return stationRepository.findById(id);
    }

    // F2: equipments of a station, with their type and operating state
    public List<Equipment> getEquipmentsByStation(Long stationId) {
        return equipmentRepository.findByStationIdWithType(stationId);
    }
}