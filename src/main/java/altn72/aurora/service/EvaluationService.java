package altn72.aurora.service;

import altn72.aurora.modele.evaluation.Evaluation;
import altn72.aurora.modele.evaluation.EvaluationRepository;
import altn72.aurora.modele.incident.Incident;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final IncidentService incidentService;
    private final Clock clock;

    public EvaluationService(EvaluationRepository evaluationRepository,
                             IncidentService incidentService,
                             Clock clock) {
        this.evaluationRepository = evaluationRepository;
        this.incidentService = incidentService;
        this.clock = clock;
    }

    public Optional<Evaluation> getEvaluation(Long incidentId) {
        return evaluationRepository.findByIncidentId(incidentId);
    }

    // 3 states : resolved, assigned to a provider, and not evaluated yet
    public boolean isEvaluable(Incident incident) {
        return incident.getStatus() == Incident.StatusEnum.RESOLU
                && incident.getProvider() != null
                && !evaluationRepository.existsByIncidentId(incident.getId());
    }

    public Incident getEvaluableIncident(Long incidentId) {
        Incident incident = incidentService.getIncident(incidentId);
        if (!isEvaluable(incident)) {
            throw new EvaluationNotAllowedException(incidentId);
        }
        return incident;
    }

    @Transactional
    public Evaluation evaluate(Long incidentId, Evaluation evaluation) {
        evaluation.setId(null);
        evaluation.setIncident(getEvaluableIncident(incidentId));
        evaluation.setEvaluationDate(LocalDateTime.now(clock));
        return evaluationRepository.save(evaluation);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EvaluationNotAllowedException extends RuntimeException {
        public EvaluationNotAllowedException(Long incidentId) {
            super("L'incident " + incidentId + " ne peut pas être évalué : il doit être résolu, "
                    + "affecté à un prestataire et pas encore évalué.");
        }
    }
}