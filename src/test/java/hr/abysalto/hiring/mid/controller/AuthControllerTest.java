package hr.abysalto.hiring.mid.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.abysalto.hiring.mid.entity.User;
import hr.abysalto.hiring.mid.repository.UserRepository;
import hr.abysalto.hiring.mid.request.LoginRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    private static final String LOGIN_URL = "/api/v1/auth/login";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setUp(@Autowired UserRepository userRepository,
                       @Autowired PasswordEncoder passwordEncoder) {
        User user = User.builder()
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .role("ROLE_USER")
                .enabled(true)
                .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("should return 200 and AuthResponse for valid credentials")
    void login_WithValidCredentials_ShouldReturnOkAndAuthResponse() throws Exception {
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.token").isNotEmpty())
               .andExpect(jsonPath("$.token").isString())
               .andExpect(jsonPath("$.email").value(TEST_EMAIL))
               .andExpect(jsonPath("$.firstName").value(TEST_FIRST_NAME))
               .andExpect(jsonPath("$.lastName").value(TEST_LAST_NAME))
               .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("should return 401 for wrong password")
    void login_WithWrongPassword_ShouldReturnUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, "wrongPassword");

        mockMvc.perform(post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
               .andExpect(status().isUnauthorized())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }
}
