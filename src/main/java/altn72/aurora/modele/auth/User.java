package altn72.aurora.modele.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "utilisateur")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "login", nullable = false, length = 50, unique = true)
    private String login;

    // BCrypt hash (60 characters), never the plain-text password
    @Column(name = "mot_de_passe", nullable = false, length = 100)
    private String password;

    @Column(name = "prenom", nullable = false, length = 50)
    private String firstName;

    @Enumerated(EnumType.STRING)
    @Column(name = "profil", nullable = false, length = 30)
    private Profile profile;

    public enum Profile {
        RESPONSABLE_EXPLOITATION("Responsable d'exploitation"),
        AGENT("Agent");

        private final String label;

        Profile(String label) { this.label = label; }

        public String getLabel() { return label; }
    }
}