package altn72.aurora.modele.equipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    @Query("SELECT e FROM Equipment e JOIN FETCH e.type JOIN FETCH e.station " +
            "WHERE e.station.id = :stationId ORDER BY e.reference")
    List<Equipment> findByStationIdWithType(@Param("stationId") Long stationId);

    @Query("SELECT e FROM Equipment e JOIN FETCH e.type JOIN FETCH e.station WHERE e.id = :id")
    Optional<Equipment> findByIdWithDetails(@Param("id") Long id);
}