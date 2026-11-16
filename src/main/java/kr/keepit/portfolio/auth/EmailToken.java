package kr.keepit.portfolio.auth;
import jakarta.persistence.*;
import java.time.Instant;
@Entity
@Table(name="email_tokens")
public class EmailToken {
    @Id @Column(length=64) String tokenHash;
    @Column(nullable=false,length=160) String email;
    @Column(nullable=false,length=20) String purpose;
    @Column(nullable=false) Instant expiresAt;
    protected EmailToken() {}
    EmailToken(String tokenHash,String email,String purpose,Instant expiresAt) { this.tokenHash=tokenHash;this.email=email;this.purpose=purpose;this.expiresAt=expiresAt; }
}
