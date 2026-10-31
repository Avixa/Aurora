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

    // unresolved incidents with every info the dashboard must display
    @Query("""
        SELECT i FROM Incident i
        JOIN FETCH i.equipment e
        JOIN FETCH e.station
        LEFT JOIN FETCH i.provider
        WHERE i.resolutionDate IS NULL
        ORDER BY i.openingDate
        """)
    List<Incident> findOngoingWithDetails();

    // resolved incidents, most recently resolved first
    @Query("""
        SELECT i FROM Incident i
        JOIN FETCH i.equipment e
        JOIN FETCH e.station
        LEFT JOIN FETCH i.provider
        WHERE i.resolutionDate IS NOT NULL
        ORDER BY i.resolutionDate DESC
        """)
    List<Incident> findResolvedWithDetails();


}
