package altn72.aurora.modele.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    Optional<Evaluation> findByIncidentId(Long incidentId);

    boolean existsByIncidentId(Long incidentId);
}