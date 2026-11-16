package kr.keepit.portfolio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import kr.keepit.portfolio.auth.EmailDelivery;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:email-test;DB_CLOSE_DELAY=-1","spring.jpa.hibernate.ddl-auto=create-drop","keepit.auth.require-verification=true","keepit.auth.public-url=https://example.test","keepit.auth.mail-from=sender@example.test"})
@AutoConfigureMockMvc
class EmailAccountTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired org.springframework.jdbc.core.JdbcTemplate jdbc;
    @MockitoBean EmailDelivery mail;
    @Test void verifyResetAndSingleUse() throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email","user@example.test","password","first-password-123"))))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.verificationRequired").value(true));
        mvc.perform(post("/api/auth/login").with(csrf()).param("username","user@example.test").param("password","first-password-123")).andExpect(status().isUnauthorized());
        var captured=org.mockito.ArgumentCaptor.forClass(String.class);verify(mail).send(eq("user@example.test"),anyString(),captured.capture());
        String verifyToken=captured.getValue().split("#verify/")[1].trim();
        mvc.perform(post("/api/auth/verify").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("token",verifyToken)))).andExpect(status().isNoContent());
        mvc.perform(post("/api/auth/verify").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("token",verifyToken)))).andExpect(status().isBadRequest());
        var login=mvc.perform(post("/api/auth/login").with(csrf()).param("username","user@example.test").param("password","first-password-123")).andExpect(status().isNoContent()).andReturn();
        var session=(org.springframework.mock.web.MockHttpSession)login.getRequest().getSession(false);
        mvc.perform(post("/api/auth/password/forgot").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"user@example.test\"}")).andExpect(status().isNoContent());
        verify(mail,times(2)).send(eq("user@example.test"),anyString(),captured.capture());
        String resetToken=captured.getAllValues().getLast().split("#reset/")[1].trim();
        mvc.perform(post("/api/auth/password/reset").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("token",resetToken,"password","new-password-456")))).andExpect(status().isNoContent());
        mvc.perform(post("/api/auth/password/reset").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("token",resetToken,"password","new-password-456")))).andExpect(status().isBadRequest());
        mvc.perform(get("/api/experiences").session(session)).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login").with(csrf()).param("username","user@example.test").param("password","first-password-123")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login").with(csrf()).param("username","user@example.test").param("password","new-password-456")).andExpect(status().isNoContent());
    }
    @Test void expiredTokensAreRejectedAndStoredAsHashes() throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("email","expire@example.test","password","first-password-123"))))
                .andExpect(status().isCreated());
        var captured=org.mockito.ArgumentCaptor.forClass(String.class);
        verify(mail).send(eq("expire@example.test"),anyString(),captured.capture());
        String raw=captured.getValue().split("#verify/")[1].trim();
        assertThat(jdbc.queryForObject("select count(*) from email_tokens where token_hash = ?", Long.class, raw)).isZero();
        jdbc.update("update email_tokens set expires_at = ? where email = ?",java.sql.Timestamp.from(java.time.Instant.now().minusSeconds(10)),"expire@example.test");
        mvc.perform(post("/api/auth/verify").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("token",raw)))).andExpect(status().isBadRequest());
    }
}
