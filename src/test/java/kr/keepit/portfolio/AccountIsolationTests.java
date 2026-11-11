package kr.keepit.portfolio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:auth-test;DB_CLOSE_DELAY=-1", "spring.jpa.hibernate.ddl-auto=create-drop", "GEMINI_API_KEY=test-key"})
@AutoConfigureMockMvc
class AccountIsolationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Test void authenticationCsrfAndIsolation() throws Exception {
        mvc.perform(get("/api/experiences")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"a@example.com\",\"password\":\"1234567890\"}")).andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"a@example.com\",\"password\":\"1234567890\"}")).andExpect(status().isCreated());
        mvc.perform(post("/api/auth/login").with(csrf()).param("username","a@example.com").param("password","wrong"))
                .andExpect(status().isUnauthorized());
        var loggedIn=mvc.perform(post("/api/auth/login").with(csrf()).param("username","a@example.com").param("password","1234567890"))
                .andExpect(status().isNoContent()).andReturn();
        var userA=(org.springframework.mock.web.MockHttpSession)loggedIn.getRequest().getSession(false);
        var userB=registerAndLogin("b@example.com");
        var data=Map.of("type","PROJECT","title","private","startDate","2026-01-01");
        var result=mvc.perform(post("/api/experiences").session(userA).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data)))
                .andExpect(status().isCreated()).andReturn();
        String location=result.getResponse().getHeader("Location");
        var id=json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get(location).session(userB)).andExpect(status().isNotFound());
        mvc.perform(put(location).session(userB).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data))).andExpect(status().isNotFound());
        mvc.perform(delete(location).session(userB).with(csrf())).andExpect(status().isNotFound());
        mvc.perform(get("/api/experiences").session(userB)).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(post("/api/drafts").session(userB).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("question","협업", "experienceIds",new long[]{id})))).andExpect(status().isBadRequest());
        mvc.perform(get(location).session(userA)).andExpect(status().isOk());
    }
    @Test void realSessionsKeepExperienceAndProfileSeparate() throws Exception {
        var sessionA = registerAndLogin("owner-a@example.com");
        var sessionB = registerAndLogin("owner-b@example.com");
        var payload = Map.of("type", "PROJECT", "title", "A 전용 경험", "startDate", "2026-01-01");
        var created = mvc.perform(post("/api/experiences").session(sessionA).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(payload)))
                .andExpect(status().isCreated()).andReturn();
        String location = created.getResponse().getHeader("Location");
        mvc.perform(get("/api/experiences").session(sessionA)).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/experiences").session(sessionB)).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get(location).session(sessionB)).andExpect(status().isNotFound());
        mvc.perform(put("/api/profile").session(sessionA).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"A의 이름\"}")).andExpect(status().isOk());
        mvc.perform(get("/api/profile").session(sessionA)).andExpect(jsonPath("$.name").value("A의 이름"));
        mvc.perform(get("/api/profile").session(sessionB)).andExpect(jsonPath("$.name").value("나의 이름"));
        mvc.perform(post("/api/auth/logout").session(sessionA).with(csrf())).andExpect(status().isNoContent());
        mvc.perform(get("/api/experiences").session(sessionA)).andExpect(status().isUnauthorized());
        var newSessionA = login("owner-a@example.com");
        mvc.perform(get(location).session(newSessionA)).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("A 전용 경험"));
    }
    private org.springframework.mock.web.MockHttpSession registerAndLogin(String email) throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("email", email, "password", "test-password-123"))))
                .andExpect(status().isCreated());
        return login(email);
    }
    private org.springframework.mock.web.MockHttpSession login(String email) throws Exception {
        var result = mvc.perform(post("/api/auth/login").with(csrf()).param("username", email).param("password", "test-password-123"))
                .andExpect(status().isNoContent()).andReturn();
        return (org.springframework.mock.web.MockHttpSession) result.getRequest().getSession(false);
    }
}
