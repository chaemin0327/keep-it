package kr.keepit.portfolio;

import java.util.Locale;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Explicit local-only migration; never assign unowned data to an arbitrary first user. */
@Component
@Profile("!production")
@ConditionalOnProperty(name="keepit.legacy.import-email")
public class LegacyAccountImport implements CommandLineRunner {
    private final JdbcTemplate jdbc;
    private final String email;
    LegacyAccountImport(JdbcTemplate jdbc, @org.springframework.beans.factory.annotation.Value("${keepit.legacy.import-email}") String email) {
        this.jdbc=jdbc;this.email=email.strip().toLowerCase(Locale.ROOT);
    }
    @Override @Transactional
    public void run(String... args) {
        if(email.isBlank() || jdbc.queryForObject("SELECT COUNT(*) FROM accounts WHERE email = ?",Long.class,email)!=1)
            throw new IllegalStateException("기존 기록을 옮길 계정이 없습니다. 가입한 이메일을 정확히 지정해주세요.");
        long existingProfile=jdbc.queryForObject("SELECT COUNT(*) FROM user_profile WHERE owner = ?",Long.class,email);
        if(existingProfile==0) jdbc.update("INSERT INTO user_profile (owner,name,headline,bio,email) SELECT ?,name,headline,bio,email FROM profile WHERE id = 1",email);
        int imported=jdbc.update("UPDATE experience SET owner = ? WHERE owner IS NULL",email);
        org.slf4j.LoggerFactory.getLogger(LegacyAccountImport.class).info("기존 로컬 경험 {}개를 지정한 계정에 연결했습니다.", imported);
    }
}
