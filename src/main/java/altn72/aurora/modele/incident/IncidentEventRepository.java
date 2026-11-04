package altn72.aurora.modele.incident;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidentEventRepository extends JpaRepository<IncidentEvent, Long> {

    // journal of one incident, oldest first, with each author
    // the only native SQL query of the project (§7.6), analysed in journal.md
    @Query(value = """
        SELECT id, incident_id, auteur_id, date_evenement, type_evenement, detail
        FROM evenement_incident
        WHERE incident_id = :incidentId
        ORDER BY date_evenement, id
        """, nativeQuery = true)
    List<IncidentEvent> findJournal(@Param("incidentId") Long incidentId);
}