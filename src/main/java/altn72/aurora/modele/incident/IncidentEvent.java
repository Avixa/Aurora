package altn72.aurora.modele.incident;

import altn72.aurora.modele.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// one line of an incident's journal (who, when, what)
@Entity
@Getter
@Setter
@Table(name = "evenement_incident")
public class IncidentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id")
    private User author;

    @Column(name = "date_evenement", nullable = false)
    private LocalDateTime eventDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_evenement", nullable = false, length = 30)
    private EventType type;

    @Column(name = "detail", length = 500)
    private String detail;

    public enum EventType {
        DECLARATION("Déclaration"),
        MODIFICATION("Modification"),
        AFFECTATION("Affectation"),
        REAFFECTATION("Réaffectation"),
        RESOLUTION("Résolution"),
        EVALUATION("Évaluation");

        private final String label;

        EventType(String label) { this.label = label; }

        public String getLabel() { return label; }
    }
}