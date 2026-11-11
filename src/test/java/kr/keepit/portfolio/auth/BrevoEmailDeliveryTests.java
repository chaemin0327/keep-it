package kr.keepit.portfolio.auth;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
class BrevoEmailDeliveryTests {
    @Test void sendsViaHttpsAndReportsFailureWithoutLeakingCredentials() throws Exception {
        var client=mock(HttpClient.class);
        @SuppressWarnings("unchecked") HttpResponse<Void> response=mock(HttpResponse.class);
        when(client.send(any(HttpRequest.class),any(HttpResponse.BodyHandler.class))).thenReturn(response);
        when(response.statusCode()).thenReturn(201);
        var delivery=new BrevoEmailDelivery(client,new ObjectMapper(),"private-test-key","sender@example.test");
        delivery.send("recipient@example.test","제목","내용");
        var request=org.mockito.ArgumentCaptor.forClass(HttpRequest.class);
        verify(client).send(request.capture(),any(HttpResponse.BodyHandler.class));
        assertThat(request.getValue().uri().toString()).isEqualTo("https://api.brevo.com/v3/smtp/email");
        assertThat(request.getValue().headers().firstValue("api-key")).contains("private-test-key");
        when(response.statusCode()).thenReturn(401);
        assertThatThrownBy(()->delivery.send("recipient@example.test","제목","내용"))
                .isInstanceOf(ResponseStatusException.class).hasMessageNotContaining("private-test-key");
    }
}
