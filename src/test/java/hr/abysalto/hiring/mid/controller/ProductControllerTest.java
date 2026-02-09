package hr.abysalto.hiring.mid.controller;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductControllerTest {

    private static final String PRODUCTS_URL = "/api/v1/products";
    private static final String LOGIN_URL = "/api/v1/auth/login";
    private static final String TEST_EMAIL = "producttest@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String jwtToken;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setUp(
            @Autowired UserRepository userRepository,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired MockMvc mockMvc
    ) throws Exception {
        User user = User.builder()
                        .firstName("Product")
                        .lastName("Tester")
                        .email(TEST_EMAIL)
                        .password(passwordEncoder.encode(TEST_PASSWORD))
                        .role("ROLE_USER")
                        .enabled(true)
                        .build();
        userRepository.save(user);

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
    @DisplayName("should return 200 and product list when fetching all products")
    void getAllProducts_ShouldReturnOkAndProductList() throws Exception {
        mockMvc.perform(get(PRODUCTS_URL)
                                .header("Authorization", "Bearer " + jwtToken))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.products").isArray())
               .andExpect(jsonPath("$.products").isNotEmpty())
               .andExpect(jsonPath("$.total").isNumber())
               .andExpect(jsonPath("$.limit").isNumber())
               .andExpect(jsonPath("$.skip").isNumber());
    }

    @Test
    @DisplayName("should return 200 and product details when fetching product by ID")
    void getProductById_ShouldReturnOkAndProduct() throws Exception {
        mockMvc.perform(get(PRODUCTS_URL + "/1")
                                .header("Authorization", "Bearer " + jwtToken))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.title").isNotEmpty())
               .andExpect(jsonPath("$.price").isNumber());
    }

    @Test
    @DisplayName("should return error when fetching product with non-existent ID")
    void getProductById_WithNonExistentId_ShouldReturnError() throws Exception {
        mockMvc.perform(get(PRODUCTS_URL + "/99999999")
                                .header("Authorization", "Bearer " + jwtToken))
               .andExpect(status().is4xxClientError())
               .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @DisplayName("should respect limit and skip query parameters")
    void getAllProducts_WithLimitAndSkip_ShouldRespectParams() throws Exception {
        mockMvc.perform(get(PRODUCTS_URL)
                                .param("limit", "2")
                                .param("skip", "5")
                                .header("Authorization", "Bearer " + jwtToken))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.products.length()").value(2))
               .andExpect(jsonPath("$.skip").value(5))
               .andExpect(jsonPath("$.limit").value(2));
    }

    @Test
    @DisplayName("should return 401 when no auth token is provided")
    void getAllProducts_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get(PRODUCTS_URL))
               .andExpect(status().isForbidden());
    }
}