package kr.keepit.portfolio.auth;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BrevoEmailDelivery implements EmailDelivery {
    private final HttpClient client;
    private final ObjectMapper json;
    private final String apiKey,from;
    @org.springframework.beans.factory.annotation.Autowired
    public BrevoEmailDelivery(ObjectMapper json,@Value("${keepit.mail.api-key:}") String apiKey,@Value("${keepit.auth.mail-from:}") String from) {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(),json,apiKey,from);
    }
    BrevoEmailDelivery(HttpClient client,ObjectMapper json,String apiKey,String from) {this.client=client;this.json=json;this.apiKey=apiKey;this.from=from;}
    @Override public void send(String recipient,String subject,String text) {
        if(apiKey.isBlank() || from.isBlank()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"이메일 발송 설정이 필요해요.");
        try {
            var payload=Map.of("sender",Map.of("email",from,"name","keep it"),"to",List.of(Map.of("email",recipient)),"subject",subject,"textContent",text);
            var req=HttpRequest.newBuilder(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .timeout(Duration.ofSeconds(15)).header("Content-Type","application/json").header("api-key",apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(payload))).build();
            var response=client.send(req,HttpResponse.BodyHandlers.discarding());
            if(response.statusCode()/100!=2) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"이메일을 보내지 못했어요. 잠시 후 다시 시도해주세요.");
        } catch(InterruptedException ex) {Thread.currentThread().interrupt();throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"이메일 발송이 중단됐어요.");}
        catch(ResponseStatusException ex) {throw ex;}
        catch(Exception ex) {throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"이메일을 보내지 못했어요. 잠시 후 다시 시도해주세요.");}
    }
}
