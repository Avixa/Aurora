package altn72.aurora.service;

import altn72.aurora.modele.auth.User;
import altn72.aurora.modele.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private BCryptPasswordEncoder passwordEncoder;

    private static final String LOGIN = "jdoe";
    private static final String RAW_PASSWORD = "SecretPassword123";

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Nested
    @DisplayName("authenticate Tests")
    class AuthenticateTests {

        @Test
        @DisplayName("Should return User when login exists and password matches BCrypt hash")
        void authenticate_WhenLoginExistsAndPasswordMatches_ShouldReturnUser() {
            // Given
            String hashedPassword = passwordEncoder.encode(RAW_PASSWORD);
            User user = mock(User.class);
            when(user.getPassword()).thenReturn(hashedPassword);
            when(userRepository.findByLogin(LOGIN)).thenReturn(Optional.of(user));

            // When
            Optional<User> result = userService.authenticate(LOGIN, RAW_PASSWORD);

            // Then
            assertThat(result).contains(user);
            verify(userRepository).findByLogin(LOGIN);
        }

        @Test
        @DisplayName("Should return empty optional when user is found but password does not match")
        void authenticate_WhenUserExistsButPasswordMismatches_ShouldReturnEmptyOptional() {
            // Given
            String hashedPassword = passwordEncoder.encode("DifferentPassword");
            User user = mock(User.class);
            when(user.getPassword()).thenReturn(hashedPassword);
            when(userRepository.findByLogin(LOGIN)).thenReturn(Optional.of(user));

            // When
            Optional<User> result = userService.authenticate(LOGIN, RAW_PASSWORD);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findByLogin(LOGIN);
        }

        @Test
        @DisplayName("Should return empty optional when login does not exist in repository")
        void authenticate_WhenLoginDoesNotExist_ShouldReturnEmptyOptional() {
            // Given
            when(userRepository.findByLogin(LOGIN)).thenReturn(Optional.empty());

            // When
            Optional<User> result = userService.authenticate(LOGIN, RAW_PASSWORD);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findByLogin(LOGIN);
        }
    }
}