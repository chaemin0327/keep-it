package kr.keepit.portfolio.auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface EmailTokenRepository extends JpaRepository<EmailToken,String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) Optional<EmailToken> findByTokenHash(String hash);
    void deleteByEmailAndPurpose(String email,String purpose);
}
