package kr.keepit.portfolio.auth;
import java.security.*;
import java.time.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmailTokenService {
    private final EmailTokenRepository tokens;
    private final AccountRepository accounts;
    private final PasswordEncoder encoder;
    private final EmailDelivery mail;
    private final String publicUrl,from;
    private final SecureRandom random = new SecureRandom();
    EmailTokenService(EmailTokenRepository tokens,AccountRepository accounts,PasswordEncoder encoder,EmailDelivery mail,
                      @Value("${keepit.auth.public-url:http://localhost:8087}") String publicUrl,
                      @Value("${keepit.auth.mail-from:}") String from) {
        this.tokens=tokens;this.accounts=accounts;this.encoder=encoder;this.mail=mail;this.publicUrl=publicUrl.replaceAll("/+$","");this.from=from;
    }
    @Transactional
    public void send(String email,String purpose) {
        byte[] bytes=new byte[32];random.nextBytes(bytes);
        String raw=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokens.deleteByEmailAndPurpose(email,purpose);
        tokens.save(new EmailToken(hash(raw),email,purpose,Instant.now().plus(Duration.ofMinutes(30))));
        String subject=purpose.equals("VERIFY")?"킵잇 이메일 인증":"킵잇 비밀번호 재설정";
        String text="아래 링크를 열어주세요. 링크는 30분 동안 유효하며 한 번만 사용할 수 있어요.\n\n"+publicUrl+"/recover.html#"+purpose.toLowerCase(Locale.ROOT)+"/"+raw;
        mail.send(email,subject,text);
    }
    @Transactional
    public void consume(String raw,String purpose,String newPassword) {
        if(raw==null || !raw.matches("[A-Za-z0-9_-]{43}")) throw invalid();
        var token=tokens.findByTokenHash(hash(raw)).orElseThrow(EmailTokenService::invalid);
        if(!purpose.equals(token.purpose)||!token.expiresAt.isAfter(Instant.now())) throw invalid();
        var account=accounts.findById(token.email).orElseThrow(EmailTokenService::invalid);
        if(purpose.equals("VERIFY")) account.verified=true;
        else {
            account.passwordHash=encoder.encode(newPassword);
            account.sessionVersion=(account.sessionVersion == null ? 0L : account.sessionVersion)+1;
        }
        tokens.delete(token);
    }
    private static ResponseStatusException invalid() {return new ResponseStatusException(HttpStatus.BAD_REQUEST,"링크가 만료되었거나 이미 사용됐어요.");}
    private static String hash(String value) {
        try {return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}
        catch(NoSuchAlgorithmException e) {throw new IllegalStateException(e);}
    }
}
