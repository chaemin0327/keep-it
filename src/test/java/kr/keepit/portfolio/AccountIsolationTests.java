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
        mvc.perform(post("/api/auth/login").with(csrf()).param("username","a@example.com").param("password","1234567890"))
                .andExpect(status().isNoContent());
        var data=Map.of("type","PROJECT","title","private","startDate","2026-01-01");
        var result=mvc.perform(post("/api/experiences").with(user("a@example.com")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data)))
                .andExpect(status().isCreated()).andReturn();
        String location=result.getResponse().getHeader("Location");
        var id=json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get(location).with(user("b@example.com"))).andExpect(status().isNotFound());
        mvc.perform(put(location).with(user("b@example.com")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data))).andExpect(status().isNotFound());
        mvc.perform(delete(location).with(user("b@example.com")).with(csrf())).andExpect(status().isNotFound());
        mvc.perform(get("/api/experiences").with(user("b@example.com"))).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(post("/api/drafts").with(user("b@example.com")).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("question","협업", "experienceIds",new long[]{id})))).andExpect(status().isBadRequest());
        mvc.perform(get(location).with(user("a@example.com"))).andExpect(status().isOk());
    }
}
