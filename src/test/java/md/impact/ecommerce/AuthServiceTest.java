package md.impact.ecommerce;

import java.util.Optional;

import md.impact.ecommerce.repositories.UserRepository;
import md.impact.ecommerce.security.JwtService;
import md.impact.ecommerce.services.AuthService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    
    AuthService authService;
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }
    @Test
    void register_hashes_the_password_before_saving() {
        // Given
        var req = new RegisterRequest("pupil@gmail.com", "1488");
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt_token");

        ArgumentCaptor<User> Captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(Captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("hashed_password");
        assertThat(Captor.getValue().getPassword()).isEqualTo(req.password());
        AuthResponse response = authService.register(req);
        assertThat(response.token()).isEqualTo("jwt_token");
        assertThat(response.role()).isEqualTo("USER");

        @Test
        void register_rejects_an_email_that_already_exists() {
            // Given
            var req = new RegisterRequest("pupil1@gmail.com", "1234");
            when(userRepository.existsByEmail(req.email())).thenReturn(true);
            assertThatThrownBy(() -> authService.register(req))
                    .isInstanceOf(ApiException.class)
                    .extracting(e -> ((ApiException) e).getStatus())
                    .isEqualTo(HttpStatus.CONFLICT);
            verify(userRepository, never()).save(any());


        }
        @test
        void login_with_correct_password_return_a_jwt_token() {
            // Given
            var req = new LoginRequest("user@gmail.com", "4321");
        User user = mock(User.class);
        when(user.getEmail()).thenReturn(req.email());
        when(user.getPasswordHash()).thenReturn("hashed_password");
        when(user.getRole()).thenReturn(role.USER);
        when(userRepository.findByEmail(req.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.password(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.generate(user)).thenReturn("jwt_false");
        AuthResponse response = authService.login(req);

        assertThat(response.token()).isEqualTo("jwt_false");
        assertThat(response.role()).isEqualTo("USER");
}