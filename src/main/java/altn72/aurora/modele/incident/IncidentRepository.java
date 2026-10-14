package altn72.aurora.modele.incident;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    @Query("SELECT i FROM Incident i " +
            "WHERE i.equipment.id = :equipmentId AND i.resolutionDate IS NULL " +
            "ORDER BY i.openingDate DESC")
    List<Incident> findOngoingByEquipmentId(Long equipmentId);

    @Query("SELECT i FROM Incident i " +
            "WHERE i.equipment.id = :equipmentId AND i.resolutionDate IS NOT NULL " +
            "ORDER BY i.openingDate DESC")
    List<Incident> findPastByEquipmentId(Long equipmentId);
}
