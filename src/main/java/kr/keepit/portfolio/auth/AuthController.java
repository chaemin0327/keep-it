package kr.keepit.portfolio.auth;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AccountRepository accounts;
    private final PasswordEncoder encoder;
    AuthController(AccountRepository accounts, PasswordEncoder encoder) {this.accounts=accounts;this.encoder=encoder;}
    record Registration(@NotBlank @Email @Size(max=160) String email, @NotBlank @Size(min=10,max=72) String password) {}
    @GetMapping("/csrf") CsrfToken csrf(CsrfToken token) { return token; }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    void register(@Valid @RequestBody Registration input) {
        if (input.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"비밀번호는 UTF-8 기준 72바이트 이하여야 해요.");
        String email=input.email().strip().toLowerCase(Locale.ROOT);
        if(accounts.existsById(email)) throw new ResponseStatusException(HttpStatus.CONFLICT,"이미 가입한 이메일이에요.");
        try {accounts.saveAndFlush(new Account(email,encoder.encode(input.password())));}
        catch(org.springframework.dao.DataIntegrityViolationException e) {throw new ResponseStatusException(HttpStatus.CONFLICT,"이미 가입한 이메일이에요.");}
    }
}
