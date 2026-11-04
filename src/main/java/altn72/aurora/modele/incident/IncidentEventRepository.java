package altn72.aurora.modele.incident;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidentEventRepository extends JpaRepository<IncidentEvent, Long> {

    // journal of one incident, oldest first, with each author
    @Query("""
        SELECT ev FROM IncidentEvent ev
        LEFT JOIN FETCH ev.author
        WHERE ev.incident.id = :incidentId
        ORDER BY ev.eventDate, ev.id
        """)
    List<IncidentEvent> findJournal(@Param("incidentId") Long incidentId);
}