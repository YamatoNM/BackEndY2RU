package md.impact.ecommerce;

import io.jsonwebtoken.security.Password;
import md.impact.ecommerce.dtos.RegisterRequest;
import md.impact.ecommerce.entities.User;
import md.impact.ecommerce.repositories.UserRepository;
import md.impact.ecommerce.security.JwtService;
import md.impact.ecommerce.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    }


}
