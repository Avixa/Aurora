package altn72.aurora.service;

import altn72.aurora.modele.auth.User;
import altn72.aurora.modele.auth.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String login, String rawPassword) {
        return userRepository.findByLogin(login)
                .filter(u -> encoder.matches(rawPassword, u.getPassword()));
    }
}