package md.impact.ecommerce;

import md.impact.ecommerce.dtos.AuthResponse;
import md.impact.ecommerce.dtos.LoginRequest;
import md.impact.ecommerce.dtos.MeResponse;
import md.impact.ecommerce.dtos.RegisterRequest;
import md.impact.ecommerce.entities.Role;
import md.impact.ecommerce.entities.User;
import md.impact.ecommerce.exceptions.ApiException;
import md.impact.ecommerce.repositories.UserRepository;
import md.impact.ecommerce.security.JwtService;
import md.impact.ecommerce.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;

    AuthService authService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }
    @Test
    void register_hashes_the_password_before_saving(){
var req = new RegisterRequest("elev@impact.md",
        "parola-in-clar");
when(userRepository.existsByEmail(req.email())).thenReturn(false);
when(passwordEncoder.encode(req.password())).thenReturn(
        "$2b$10$hash-fals-pentru-test");
when(userRepository.save(any(User.class))).thenAnswer(
        invocation -> invocation.getArgument(0));
when(jwtService.generate(any(User.class))).thenReturn("jwt-fals");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPasswordHash()).isEqualTo(
                "$2b$10$hash-fals-pentru-test");
        assertThat(captor.getValue().getPasswordHash()).isNotEqualTo(
               req.password());
        AuthResponse response = authService.register(req);
        assertThat(response.email()).isEqualTo("elev@impact.md");
        assertThat(response.role()).isEqualTo("USER");
        assertThat(response.token()).isEqualTo("jwt-false");



    }
    @Test
    void register_rejects_an_email_that_already_exists(){
var req = new RegisterRequest("already@impact.md",
        "anything");
when(userRepository.existsByEmail(req.email())).thenReturn(true);
assertThatThrownBy(() -> authService.register(req))
        .isInstanceOf(ApiException.class)
        .extracting(e ->
                ((ApiException) e).getStatus())
        .isEqualTo(HttpStatus.CONFLICT);

verify(userRepository, never()).save(any());

    }
    @Test
    void login_with_correct_password_returns_a_token(){
        var req = new LoginRequest(
                "user@impact.md", "user123");
        User user = mock(User.class);
        when(user.getEmail()).thenReturn(req.email());
        when(user.getPasswordHash()).thenReturn("hash-saved");
        when(user.getRole()).thenReturn(Role.USER);
        when(userRepository.findByEmail(req.email()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.password(),
                "hash-saved")).thenReturn(true);

        when(jwtService.generate(user)).thenReturn("jwt-false");
        AuthResponse response = authService.login(req);

        assertThat(response.token()).isEqualTo("jwt-fals");
        assertThat(response.role()).isEqualTo("USER");
    }

}
