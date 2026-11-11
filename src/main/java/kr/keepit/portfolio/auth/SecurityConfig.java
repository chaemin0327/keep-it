package kr.keepit.portfolio.auth;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpStatus;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder encoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService users(AccountRepository accounts) {
        return email -> accounts.findById(email.toLowerCase(java.util.Locale.ROOT).strip())
                .map(a -> User.withUsername(a.email).password(a.passwordHash).disabled(Boolean.FALSE.equals(a.verified)).roles("USER").build())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }
    @Bean SecurityFilterChain security(HttpSecurity http, AccountRepository accounts) throws Exception {
        return http.addFilterBefore(new RequestRateLimitFilter(),org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new SessionVersionFilter(accounts),org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class).authorizeHttpRequests(a -> a.requestMatchers("/api/auth/**", "/login.html", "/recover.html", "/auth.js", "/recover.js", "/style.css", "/brand-mark.svg", "/error").permitAll().anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint((req,res,ex) -> {
                    if(req.getRequestURI().startsWith("/api/")) res.sendError(401); else res.sendRedirect("/login.html");
                }))
                .formLogin(f -> f.loginProcessingUrl("/api/auth/login")
                        .successHandler((req,res,auth) -> {
                            var account=accounts.findById(auth.getName()).orElseThrow();
                            req.getSession().setAttribute("accountVersion", account.sessionVersion == null ? 0L : account.sessionVersion);
                            res.setStatus(204);
                        })
                        .failureHandler((req,res,ex) -> res.sendError(401)))
                .logout(l -> l.logoutUrl("/api/auth/logout").logoutSuccessHandler((req,res,auth) -> res.setStatus(204)))
                .build();
    }
}
