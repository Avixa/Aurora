package altn72.aurora.modele.incident;

import altn72.aurora.modele.auth.User;
import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.provider.Provider;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident")
@Getter
@Setter
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipment equipment;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Opening date is required")
    @PastOrPresent(message = "Opening date cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime openingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestataire_id")
    private Provider provider;

    @Column(name = "date_affectation")
    private LocalDateTime affectationDate;

    @Column(name = "date_resolution")
    private LocalDateTime resolutionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declarant_id")
    private User declarant;

    public enum StatusEnum {
        NON_AFFECTE,
        AFFECTE,
        RESOLU
    }

}