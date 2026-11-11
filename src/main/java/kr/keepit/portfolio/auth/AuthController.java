package kr.keepit.portfolio.auth;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AccountRepository accounts;
    private final PasswordEncoder encoder;
    private final EmailTokenService tokens;
    private final boolean verificationRequired;
    AuthController(AccountRepository accounts,PasswordEncoder encoder,EmailTokenService tokens,
                   @Value("${keepit.auth.require-verification:false}") boolean verificationRequired) {
        this.accounts=accounts;this.encoder=encoder;this.tokens=tokens;this.verificationRequired=verificationRequired;
    }
    record Registration(@NotBlank @Email @Size(max=160) String email,@NotBlank @Size(min=10,max=72) String password) {}
    record EmailRequest(@NotBlank @Email @Size(max=160) String email) {}
    record TokenRequest(@NotBlank String token) {}
    record ResetRequest(@NotBlank String token,@NotBlank @Size(min=10,max=72) String password) {}
    @GetMapping("/csrf") CsrfToken csrf(CsrfToken token) {return token;}
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) @Transactional
    java.util.Map<String,Boolean> register(@Valid @RequestBody Registration input) {
        validatePassword(input.password());
        String email=normalize(input.email());
        if(accounts.existsById(email)) throw new ResponseStatusException(HttpStatus.CONFLICT,"이미 가입한 이메일이에요.");
        try {
            Account account=new Account(email,encoder.encode(input.password())); account.verified=!verificationRequired;
            accounts.saveAndFlush(account);
            if(verificationRequired) tokens.send(email,"VERIFY");
        } catch(org.springframework.dao.DataIntegrityViolationException e) {throw new ResponseStatusException(HttpStatus.CONFLICT,"이미 가입한 이메일이에요.");}
        return java.util.Map.of("verificationRequired",verificationRequired);
    }
    @PostMapping("/verify") @ResponseStatus(HttpStatus.NO_CONTENT)
    void verify(@Valid @RequestBody TokenRequest input) {tokens.consume(input.token(),"VERIFY",null);}
    @PostMapping("/verify/resend") @ResponseStatus(HttpStatus.NO_CONTENT)
    void resend(@Valid @RequestBody EmailRequest input) {
        String email=normalize(input.email());
        accounts.findById(email).filter(a -> Boolean.FALSE.equals(a.verified)).ifPresent(a -> tokens.send(email,"VERIFY"));
    }
    @PostMapping("/password/forgot") @ResponseStatus(HttpStatus.NO_CONTENT)
    void forgot(@Valid @RequestBody EmailRequest input) {
        String email=normalize(input.email());
        accounts.findById(email).filter(a -> !Boolean.FALSE.equals(a.verified)).ifPresent(a -> tokens.send(email,"RESET"));
    }
    @PostMapping("/password/reset") @ResponseStatus(HttpStatus.NO_CONTENT)
    void reset(@Valid @RequestBody ResetRequest input) {validatePassword(input.password());tokens.consume(input.token(),"RESET",input.password());}
    private static void validatePassword(String password) {
        if(password.getBytes(StandardCharsets.UTF_8).length > 72) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"비밀번호는 UTF-8 기준 72바이트 이하여야 해요.");
    }
    private static String normalize(String email) {return email.strip().toLowerCase(Locale.ROOT);}
}
