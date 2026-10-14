package altn72.aurora.modele.equipment;

import altn72.aurora.modele.station.Station;
import jakarta.persistence.*;

@Entity
@Table(name = "equipement")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // RG9: reference unique
    @Column(name = "reference", nullable = false, length = 30, unique = true)
    private String reference;

    @Column(name = "libelle", nullable = false, length = 150)
    private String label;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_equipement_id", nullable = false)
    private EquipmentType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_fonctionnement", nullable = false, length = 20)
    private OperatingState operatingState;

    public enum OperatingState {
        EN_SERVICE,
        DEGRADE,
        HORS_SERVICE
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public EquipmentType getType() { return type; }
    public void setType(EquipmentType type) { this.type = type; }

    public Station getStation() { return station; }
    public void setStation(Station station) { this.station = station; }

    public OperatingState getOperatingState() { return operatingState; }
    public void setOperatingState(OperatingState operatingState) { this.operatingState = operatingState; }
}