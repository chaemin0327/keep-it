package kr.keepit.portfolio.auth;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.filter.OncePerRequestFilter;

/** Fixed-window limits; production counters are shared through PostgreSQL. */
public class RequestRateLimitFilter extends OncePerRequestFilter {
    private record Window(long until,int count) {}
    private final ConcurrentHashMap<String,Window> windows=new ConcurrentHashMap<>();
    private final org.springframework.jdbc.core.JdbcTemplate jdbc;
    public RequestRateLimitFilter() { this.jdbc=null; }
    public RequestRateLimitFilter(org.springframework.jdbc.core.JdbcTemplate jdbc) { this.jdbc=jdbc; }
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
        String path=request.getRequestURI();
        boolean auth=request.getMethod().equals("POST") && (path.equals("/api/auth/login") || path.equals("/api/auth/register") || path.startsWith("/api/auth/password/") || path.equals("/api/auth/verify/resend"));
        boolean draft=request.getMethod().equals("POST") && path.equals("/api/drafts");
        if(!auth && !draft) {chain.doFilter(request,response);return;}
        long now=System.currentTimeMillis();
        String key=request.getRemoteAddr()+":"+(auth?"auth":"draft");
        Window window;
        if(jdbc != null) {
            long until=(now / 60000 + 1) * 60000;
            try {
                String hash=java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(key.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
                Integer count=jdbc.queryForObject("INSERT INTO keepit.request_limits (rate_key,expires_at,request_count) VALUES (?,?,1) ON CONFLICT (rate_key) DO UPDATE SET expires_at=CASE WHEN request_limits.expires_at <= ? THEN EXCLUDED.expires_at ELSE request_limits.expires_at END, request_count=CASE WHEN request_limits.expires_at <= ? THEN 1 ELSE request_limits.request_count+1 END RETURNING request_count",Integer.class,hash,until,now,now);
                if(java.util.concurrent.ThreadLocalRandom.current().nextInt(100)==0) jdbc.update("DELETE FROM keepit.request_limits WHERE expires_at < ?",now-120000);
                window=new Window(until,count);
            } catch(Exception ex) { response.sendError(503); return; }
        } else {
            if(windows.size()>10000) windows.entrySet().removeIf(e->e.getValue().until()<now);
            if(windows.size()>10000) {response.sendError(429);return;}
            window=windows.compute(key,(k,w)->w==null||w.until()<now?new Window(now+60000,1):new Window(w.until(),w.count()+1));
        }
        if(window.count()>(auth?20:10)) {response.setHeader("Retry-After",Long.toString(Math.max(1,(window.until()-now+999)/1000)));response.sendError(429);return;}
        chain.doFilter(request,response);
    }
}
