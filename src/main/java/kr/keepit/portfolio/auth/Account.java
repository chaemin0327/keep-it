package kr.keepit.portfolio.auth;
import jakarta.persistence.*;
@Entity
@Table(name="accounts")
public class Account {
    @Id @Column(length=160) String email;
    @Column(nullable=false, length=100) String passwordHash;
    protected Account() {}
    Account(String email, String passwordHash) { this.email=email; this.passwordHash=passwordHash; }
}
