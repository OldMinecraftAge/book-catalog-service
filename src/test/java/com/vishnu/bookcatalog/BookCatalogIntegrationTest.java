package com.vishnu.bookcatalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishnu.bookcatalog.dto.BookRequestDto;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class BookCatalogIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GoogleAuthenticator googleAuthenticator;

    @Value("${totp.secret}")
    private String totpSecret;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void fullAuthenticationAndBookFlow() throws Exception {
        // Step 1: Credentials login
        String loginJson = objectMapper.writeValueAsString(
                new com.vishnu.bookcatalog.dto.LoginRequest("admin", "password")
        );
        MvcResult loginResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String otpToken = loginResponse.get("otpToken").asText();
        assertThat(otpToken).isNotEmpty();

        // Step 2: TOTP verification
        int totpCode = googleAuthenticator.getTotpPassword(totpSecret);
        String totpJson = objectMapper.writeValueAsString(
                new com.vishnu.bookcatalog.dto.TotpRequest(totpCode)
        );
        MvcResult authResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/2fa")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + otpToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(totpJson)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode authResponse = objectMapper.readTree(authResult.getResponse().getContentAsString());
        String authToken = authResponse.get("authToken").asText();
        assertThat(authToken).isNotEmpty();

        // Step 3: Access protected endpoint - initially empty
        MvcResult getEmpty = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/books")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                )
                .andExpect(status().isOk())
                .andReturn();
        JsonNode emptyList = objectMapper.readTree(getEmpty.getResponse().getContentAsString());
        assertThat(emptyList.isArray()).isTrue();
        assertThat(emptyList.size()).isEqualTo(0);

        // Step 4: Create a new book
        BookRequestDto bookRequest = new BookRequestDto();
        bookRequest.setTitle("Integration Testing");
        bookRequest.setAuthor("Tester");
        bookRequest.setIsbn("ISBN-1234");
        bookRequest.setGenre("Tech");
        bookRequest.setPublishedDate(java.time.LocalDate.now());
        bookRequest.setSummary("Full integration test book.");
        String bookJson = objectMapper.writeValueAsString(bookRequest);

        MvcResult createResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/books")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson)
                )
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createResponse = objectMapper.readTree(createResult.getResponse().getContentAsString());
        Long bookId = createResponse.get("id").asLong();
        assertThat(bookId).isPositive();
        assertThat(createResponse.get("title").asText()).isEqualTo("Integration Testing");

        // Step 5: Retrieve the created book
        MvcResult getResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/books/" + bookId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode getResponse = objectMapper.readTree(getResult.getResponse().getContentAsString());
        assertThat(getResponse.get("id").asLong()).isEqualTo(bookId);
        assertThat(getResponse.get("isbn").asText()).isEqualTo("ISBN-1234");
    }
}
