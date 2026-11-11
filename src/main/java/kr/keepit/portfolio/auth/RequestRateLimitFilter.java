package kr.keepit.portfolio.auth;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.filter.OncePerRequestFilter;

/** Best-effort per-process limit; production needs a shared, proxy-aware limiter. */
public class RequestRateLimitFilter extends OncePerRequestFilter {
    private record Window(long until,int count) {}
    private final ConcurrentHashMap<String,Window> windows=new ConcurrentHashMap<>();
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
        String path=request.getRequestURI();
        boolean auth=request.getMethod().equals("POST") && (path.equals("/api/auth/login") || path.equals("/api/auth/register") || path.startsWith("/api/auth/password/") || path.equals("/api/auth/verify/resend"));
        boolean draft=request.getMethod().equals("POST") && path.equals("/api/drafts");
        if(!auth && !draft) {chain.doFilter(request,response);return;}
        long now=System.currentTimeMillis();
        if(windows.size()>10000) windows.entrySet().removeIf(e->e.getValue().until()<now);
        if(windows.size()>10000) {response.sendError(429);return;}
        String key=request.getRemoteAddr()+":"+(auth?"auth":"draft");
        Window window=windows.compute(key,(k,w)->w==null||w.until()<now?new Window(now+60000,1):new Window(w.until(),w.count()+1));
        if(window.count()>(auth?20:10)) {response.setHeader("Retry-After",Long.toString(Math.max(1,(window.until()-now+999)/1000)));response.sendError(429);return;}
        chain.doFilter(request,response);
    }
}
