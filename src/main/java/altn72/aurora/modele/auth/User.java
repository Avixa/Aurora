package altn72.aurora.modele.auth;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateur")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "login", nullable = false, length = 50, unique = true)
    private String login;

    // BCrypt hash (60 characters), never the plain-text password
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String firstName;

    @Enumerated(EnumType.STRING)
    @Column(name = "profil", nullable = false, length = 30)
    private Profile profile;

    public enum Profile {
        RESPONSABLE_EXPLOITATION,
        AGENT_STATION
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
}