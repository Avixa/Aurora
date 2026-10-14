package altn72.aurora.modele.incident;

import altn72.aurora.modele.auth.User;
import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.provider.Provider;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "incident")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipment equipment;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "date_ouverture", nullable = false)
    private Instant openingDate;

    @Column(name = "statut", nullable = false, length = 20)
    private String statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestataire_id")
    private Provider provider;

    @Column(name = "date_affectation")
    private Instant affectationDate;

    @Column(name = "date_resolution")
    private Instant resolutionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declarant_id")
    private User declarant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Instant dateOuverture) {
        this.openingDate = dateOuverture;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Instant getAffectationDate() {
        return affectationDate;
    }

    public void setAffectationDate(Instant affectationDate) {
        this.affectationDate = affectationDate;
    }

    public Instant getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(Instant resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public User getDeclarant() {
        return declarant;
    }

    public void setDeclarant(User declarant) {
        this.declarant = declarant;
    }

}