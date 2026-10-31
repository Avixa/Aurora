package altn72.aurora.modele.evaluation;

import altn72.aurora.modele.incident.Incident;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "evaluation")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // one evaluation per incident
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false, unique = true)
    private Incident incident;

    // note from 1 to 5
    @NotNull(message = "La note est obligatoire.")
    @Min(value = 1, message = "La note doit être comprise entre 1 et 5.")
    @Max(value = 5, message = "La note doit être comprise entre 1 et 5.")
    @Column(name = "note", nullable = false)
    private Integer rating;

    @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères.")
    @Column(name = "commentaire", length = 1000)
    private String comment;

    @Column(name = "date_evaluation", nullable = false)
    private LocalDateTime evaluationDate;
}