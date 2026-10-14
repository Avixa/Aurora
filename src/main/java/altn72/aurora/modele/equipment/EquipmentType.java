package altn72.aurora.modele.equipment;

import jakarta.persistence.*;

@Entity
@Table(name = "type_equipement")
public class EquipmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, length = 30, unique = true)
    private String code;

    @Column(name = "libelle", nullable = false, length = 100, unique = true)
    private String label;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}