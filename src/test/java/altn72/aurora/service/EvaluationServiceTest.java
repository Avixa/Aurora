package altn72.aurora.service;

import altn72.aurora.modele.evaluation.Evaluation;
import altn72.aurora.modele.evaluation.EvaluationRepository;
import altn72.aurora.modele.incident.Incident;
import altn72.aurora.modele.provider.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private IncidentService incidentService;

    private Clock fixedClock;
    private EvaluationService evaluationService;

    private static final Long INCIDENT_ID = 42L;
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 3, 18, 0, 0);

    @BeforeEach
    void setUp() {
        Instant fixedInstant = NOW.atZone(ZoneId.systemDefault()).toInstant();
        fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());

        evaluationService = new EvaluationService(evaluationRepository, incidentService, fixedClock);
    }

    // ------------------------------------------------------------------------
    // Tests for getEvaluation
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("getEvaluation Tests")
    class GetEvaluationTests {

        @Test
        @DisplayName("Should return evaluation when it exists for incident ID")
        void getEvaluation_WhenFound_ShouldReturnEvaluation() {
            Evaluation evaluation = new Evaluation();
            when(evaluationRepository.findByIncidentId(INCIDENT_ID)).thenReturn(Optional.of(evaluation));

            Optional<Evaluation> result = evaluationService.getEvaluation(INCIDENT_ID);

            assertThat(result).contains(evaluation);
            verify(evaluationRepository).findByIncidentId(INCIDENT_ID);
        }

        @Test
        @DisplayName("Should return empty optional when evaluation does not exist")
        void getEvaluation_WhenNotFound_ShouldReturnEmptyOptional() {
            when(evaluationRepository.findByIncidentId(INCIDENT_ID)).thenReturn(Optional.empty());

            Optional<Evaluation> result = evaluationService.getEvaluation(INCIDENT_ID);

            assertThat(result).isEmpty();
        }
    }

    // ------------------------------------------------------------------------
    // Tests for isEvaluable
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("isEvaluable Tests")
    class IsEvaluableTests {

        @Test
        @DisplayName("Should return true when incident is RESOLU, assigned to provider, and not yet evaluated")
        void isEvaluable_WhenAllConditionsMet_ShouldReturnTrue() {
            Incident incident = createIncident(Incident.StatusEnum.RESOLU, mock(Provider.class));
            when(evaluationRepository.existsByIncidentId(INCIDENT_ID)).thenReturn(false);

            boolean result = evaluationService.isEvaluable(incident);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when incident status is not RESOLU")
        void isEvaluable_WhenStatusNotResolu_ShouldReturnFalse() {
            Incident incident = createIncident(Incident.StatusEnum.AFFECTE, mock(Provider.class));

            boolean result = evaluationService.isEvaluable(incident);

            assertThat(result).isFalse();
            verifyNoInteractions(evaluationRepository);
        }

        @Test
        @DisplayName("Should return false when incident provider is null")
        void isEvaluable_WhenProviderIsNull_ShouldReturnFalse() {
            Incident incident = createIncident(Incident.StatusEnum.RESOLU, null);

            boolean result = evaluationService.isEvaluable(incident);

            assertThat(result).isFalse();
            verifyNoInteractions(evaluationRepository);
        }

        @Test
        @DisplayName("Should return false when incident has already been evaluated")
        void isEvaluable_WhenAlreadyEvaluated_ShouldReturnFalse() {
            Incident incident = createIncident(Incident.StatusEnum.RESOLU, mock(Provider.class));
            when(evaluationRepository.existsByIncidentId(INCIDENT_ID)).thenReturn(true);

            boolean result = evaluationService.isEvaluable(incident);

            assertThat(result).isFalse();
        }
    }

    // ------------------------------------------------------------------------
    // Tests for getEvaluableIncident
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("getEvaluableIncident Tests")
    class GetEvaluableIncidentTests {

        @Test
        @DisplayName("Should return incident when it is evaluable")
        void getEvaluableIncident_WhenEvaluable_ShouldReturnIncident() {
            Incident incident = createIncident(Incident.StatusEnum.RESOLU, mock(Provider.class));
            when(incidentService.getIncident(INCIDENT_ID)).thenReturn(incident);
            when(evaluationRepository.existsByIncidentId(INCIDENT_ID)).thenReturn(false);

            Incident result = evaluationService.getEvaluableIncident(INCIDENT_ID);

            assertThat(result).isEqualTo(incident);
        }

        @Test
        @DisplayName("Should throw EvaluationNotAllowedException when incident is not evaluable")
        void getEvaluableIncident_WhenNotEvaluable_ShouldThrowException() {
            Incident incident = createIncident(Incident.StatusEnum.AFFECTE, mock(Provider.class));
            when(incidentService.getIncident(INCIDENT_ID)).thenReturn(incident);

            assertThatThrownBy(() -> evaluationService.getEvaluableIncident(INCIDENT_ID))
                    .isInstanceOf(EvaluationService.EvaluationNotAllowedException.class)
                    .hasMessageContaining("L'incident " + INCIDENT_ID + " ne peut pas être évalué");
        }
    }

    // ------------------------------------------------------------------------
    // Tests for evaluate
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("evaluate Tests")
    class EvaluateTests {

        @Test
        @DisplayName("Should successfully save evaluation with cleared ID, attached incident, and set evaluation date")
        void evaluate_WhenValid_ShouldSaveEvaluation() {
            // Given
            Incident incident = createIncident(Incident.StatusEnum.RESOLU, mock(Provider.class));
            when(incidentService.getIncident(INCIDENT_ID)).thenReturn(incident);
            when(evaluationRepository.existsByIncidentId(INCIDENT_ID)).thenReturn(false);

            Evaluation newEvaluation = new Evaluation();
            newEvaluation.setId(99L); // Pre-existing ID should be reset to null

            when(evaluationRepository.save(any(Evaluation.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Evaluation saved = evaluationService.evaluate(INCIDENT_ID, newEvaluation);

            // Then
            assertThat(saved.getIncident()).isEqualTo(incident);
            assertThat(saved.getEvaluationDate()).isEqualTo(NOW);
            verify(evaluationRepository).save(newEvaluation);
        }

        @Test
        @DisplayName("Should throw exception and not save evaluation when incident is not evaluable")
        void evaluate_WhenNotEvaluable_ShouldThrowExceptionAndNotSave() {
            Incident incident = createIncident(Incident.StatusEnum.RESOLU, null); // No provider -> Not evaluable
            when(incidentService.getIncident(INCIDENT_ID)).thenReturn(incident);

            Evaluation newEvaluation = new Evaluation();

            assertThatThrownBy(() -> evaluationService.evaluate(INCIDENT_ID, newEvaluation))
                    .isInstanceOf(EvaluationService.EvaluationNotAllowedException.class);

            verify(evaluationRepository, never()).save(any());
        }
    }

    // Helper method to build stubbed Incident
    private Incident createIncident(Incident.StatusEnum status, Provider provider) {
        Incident incident = mock(Incident.class);
        Mockito.lenient().when(incident.getId()).thenReturn(INCIDENT_ID);
        Mockito.lenient().when(incident.getStatus()).thenReturn(status);
        Mockito.lenient().when(incident.getProvider()).thenReturn(provider);
        return incident;
    }
}