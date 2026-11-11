package kr.keepit.portfolio.auth;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import static org.assertj.core.api.Assertions.assertThat;
class RequestRateLimitFilterTests {
    @Test void limitsSensitiveRequestsPerClient() throws Exception {
        var filter=new RequestRateLimitFilter();
        for(int i=1;i<=21;i++) {
            var request=new MockHttpServletRequest("POST","/api/auth/login");request.setRemoteAddr("192.0.2.1");
            var response=new MockHttpServletResponse();
            filter.doFilter(request,response,new MockFilterChain());
            assertThat(response.getStatus()).isEqualTo(i<=20?200:429);
        }
        var another=new MockHttpServletRequest("POST","/api/auth/login");another.setRemoteAddr("192.0.2.2");
        var response=new MockHttpServletResponse();filter.doFilter(another,response,new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(200);
    }
}
