package hr.abysalto.hiring.mid.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.abysalto.hiring.mid.entity.Favorite;
import hr.abysalto.hiring.mid.entity.User;
import hr.abysalto.hiring.mid.repository.FavoriteRepository;
import hr.abysalto.hiring.mid.repository.UserRepository;
import hr.abysalto.hiring.mid.request.LoginRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FavoriteControllerTest {

    private static final String FAVORITES_URL = "/api/v1/favorites";
    private static final String LOGIN_URL = "/api/v1/auth/login";
    private static final String TEST_EMAIL = "favoritetest@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String jwtToken;
    private static User testUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @BeforeEach
    void cleanFavorites() {
        favoriteRepository.deleteAll();
    }

    @BeforeAll
    static void setUp(
            @Autowired UserRepository userRepository,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired MockMvc mockMvc
    ) throws Exception {
        testUser = User.builder()
                       .firstName("Favorite")
                       .lastName("Tester")
                       .email(TEST_EMAIL)
                       .password(passwordEncoder.encode(TEST_PASSWORD))
                       .role("ROLE_USER")
                       .enabled(true)
                       .build();
        userRepository.save(testUser);

        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(objectMapper.writeValueAsString(loginRequest)))
                                  .andExpect(status().isOk())
                                  .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        jwtToken = json.get("token").asText();
    }

    @Test
    @DisplayName("should return 200 and empty list when user has no favorites")
    void getUserFavorites_WithNoFavorites_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get(FAVORITES_URL)
                                .header("Authorization", "Bearer " + jwtToken))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("should return 201 when adding product to favorites")
    void addToFavorites_ShouldReturnCreated() throws Exception {
        mockMvc.perform(post(FAVORITES_URL + "/1")
                                .header("Authorization", "Bearer " + jwtToken))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("should return 200 and product details after adding favorite")
    void getUserFavorites_AfterAdding_ShouldReturnProducts() throws Exception {
        favoriteRepository.save(new Favorite(testUser, 2L));

        mockMvc.perform(get(FAVORITES_URL)
                                .header("Authorization", "Bearer " + jwtToken))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @DisplayName("should return 204 when removing existing favorite")
    void removeFromFavorites_WithExistingFavorite_ShouldReturnNoContent() throws Exception {
        favoriteRepository.save(new Favorite(testUser, 3L));

        mockMvc.perform(delete(FAVORITES_URL + "/3")
                                .header("Authorization", "Bearer " + jwtToken))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return 409 when removing non-existent favorite")
    void removeFromFavorites_WithNonExistentFavorite_ShouldReturnConflict() throws Exception {
        mockMvc.perform(delete(FAVORITES_URL + "/999999")
                                .header("Authorization", "Bearer " + jwtToken))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.error").value("Favorite not found"));
    }

    @Test
    @DisplayName("should return 403 when getting favorites without auth")
    void getUserFavorites_WithoutAuth_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get(FAVORITES_URL))
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 403 when adding favorite without auth")
    void addToFavorites_WithoutAuth_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post(FAVORITES_URL + "/1"))
               .andExpect(status().isForbidden());
    }
}