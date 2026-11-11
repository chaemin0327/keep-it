package kr.keepit.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:legacy-category;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:legacy-schema.sql"
})
@org.springframework.security.test.context.support.WithMockUser(username="test@example.com")
@AutoConfigureMockMvc(addFilters=false)
class LegacyCategorySchemaTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired org.springframework.jdbc.core.JdbcTemplate jdbc;

    @Test void acceptsNewTypesAndPreservesExistingRowInLegacyDatabase() throws Exception {
        for (var type : new String[]{"WORK", "CUSTOM"}) {
            var data = Map.of("type",type,"title","새 분류 기록","startDate","2026-10-23","customCategory","멘토링");
            mvc.perform(post("/api/experiences").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data)))
                    .andExpect(status().isCreated()).andExpect(jsonPath("$.type").value(type));
        }
        mvc.perform(get("/api/experiences/1")).andExpect(status().isNotFound());
        org.assertj.core.api.Assertions.assertThat(jdbc.queryForObject("select title from experience where id = 1", String.class)).isEqualTo("기존에 저장한 프로젝트");
    }
}
