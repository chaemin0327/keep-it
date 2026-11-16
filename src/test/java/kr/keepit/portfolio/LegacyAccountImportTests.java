package kr.keepit.portfolio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:legacy-import;DB_CLOSE_DELAY=-1","spring.jpa.hibernate.ddl-auto=create-drop"})
@Transactional
class LegacyAccountImportTests {
    @Autowired JdbcTemplate jdbc;
    @Test void onlyUnownedRecordsGoToSpecifiedAccount() throws Exception {
        jdbc.update("INSERT INTO accounts(email,password_hash) VALUES (?,?)","owner@example.test","hash");
        jdbc.update("INSERT INTO experience(type,title,start_date,ongoing,featured,demo,owner) VALUES ('PROJECT','legacy',DATE '2026-01-01',false,false,false,NULL)");
        jdbc.update("INSERT INTO experience(type,title,start_date,ongoing,featured,demo,owner) VALUES ('PROJECT','other',DATE '2026-01-01',false,false,false,'other@example.test')");
        new LegacyAccountImport(jdbc,"owner@example.test").run();
        assertThat(jdbc.queryForObject("SELECT owner FROM experience WHERE title='legacy'",String.class)).isEqualTo("owner@example.test");
        assertThat(jdbc.queryForObject("SELECT owner FROM experience WHERE title='other'",String.class)).isEqualTo("other@example.test");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM user_profile WHERE owner='owner@example.test'",Long.class)).isEqualTo(1);
    }
    @Test void unknownAccountCannotClaimRecords() {
        assertThatThrownBy(() -> new LegacyAccountImport(jdbc,"missing@example.test").run()).isInstanceOf(IllegalStateException.class);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM experience WHERE owner IS NULL",Long.class)).isGreaterThan(0);
    }
}
