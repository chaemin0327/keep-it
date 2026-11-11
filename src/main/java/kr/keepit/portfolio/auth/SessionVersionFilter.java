package kr.keepit.portfolio.auth;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/** Reject sessions created before a password reset. */
public class SessionVersionFilter extends OncePerRequestFilter {
    private final AccountRepository accounts;
    SessionVersionFilter(AccountRepository accounts) {this.accounts=accounts;}
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        var session=request.getSession(false);
        if(auth!=null && auth.isAuthenticated() && session!=null) {
            Object version=session.getAttribute("accountVersion");
            var account=accounts.findById(auth.getName());
            if(!(version instanceof Long) || account.isEmpty() || ((Long)version).longValue()!=(account.get().sessionVersion==null?0L:account.get().sessionVersion.longValue())) {
                session.invalidate(); SecurityContextHolder.clearContext();
                if(request.getRequestURI().startsWith("/api/")) response.sendError(401); else response.sendRedirect("/login.html");
                return;
            }
        }
        chain.doFilter(request,response);
    }
}
