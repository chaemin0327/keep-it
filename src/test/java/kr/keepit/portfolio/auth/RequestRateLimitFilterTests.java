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
    @Test void renderHeaderIsUsedOnlyWhenExplicitlyEnabled() throws Exception {
        var filter=new RequestRateLimitFilter(null,true);
        for(int i=0;i<20;i++) {
            var request=new MockHttpServletRequest("POST","/api/auth/login");
            request.setRemoteAddr("192.0.2.1");request.addHeader("CF-Connecting-IP","198.51.100.1");
            var response=new MockHttpServletResponse();filter.doFilter(request,response,new MockFilterChain());
            assertThat(response.getStatus()).isEqualTo(200);
        }
        var limited=new MockHttpServletRequest("POST","/api/auth/login");
        limited.setRemoteAddr("192.0.2.1");limited.addHeader("CF-Connecting-IP","198.51.100.1");
        var limitedResponse=new MockHttpServletResponse();filter.doFilter(limited,limitedResponse,new MockFilterChain());
        assertThat(limitedResponse.getStatus()).isEqualTo(429);
        var different=new MockHttpServletRequest("POST","/api/auth/login");
        different.setRemoteAddr("192.0.2.1");different.addHeader("CF-Connecting-IP","198.51.100.2");
        var response=new MockHttpServletResponse();filter.doFilter(different,response,new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(200);
    }
}
