package metty1337.cloudfilestorage.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import metty1337.cloudfilestorage.TestcontainersConfiguration;
import metty1337.cloudfilestorage.dto.request.SignInRequest;
import metty1337.cloudfilestorage.dto.request.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerValidationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldReturn400_blankUsernameOnSignUp() throws Exception {
        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignUpRequest("", "validpass"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_usernameTooShortOnSignUp() throws Exception {
        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignUpRequest("ab", "validpass"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_usernameTooLongOnSignUp() throws Exception {
        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignUpRequest("averylongusernamethatexceeds", "validpass"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_blankPasswordOnSignUp() throws Exception {
        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignUpRequest("validuser", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_passwordTooShortOnSignUp() throws Exception {
        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignUpRequest("validuser", "ab"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_blankUsernameOnSignIn() throws Exception {
        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignInRequest("", "validpass"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_passwordTooShortOnSignIn() throws Exception {
        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignInRequest("validuser", "ab"))))
                .andExpect(status().isBadRequest());
    }
}