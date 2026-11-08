package kr.moa.portfolio.experience;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.net.http.*;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DraftControllerTest {
    @Test void onlyChosenExperienceCanAppearInRecommendations() throws Exception {
        var repo = mock(ExperienceRepository.class);
        var client = mock(HttpClient.class);
        @SuppressWarnings("unchecked") HttpResponse<String> response = mock(HttpResponse.class);
        var request = new ExperienceRequest(ExperienceType.PROJECT,"테스트 경험","기관","", LocalDate.of(2025,1,1),null,false,"","역할","협업 기록","","", "",false,"","","",List.of());
        var experience = new Experience(request,false);
        experience.id=1L;
        when(repo.findById(1L)).thenReturn(Optional.of(experience));
        when(client.send(any(HttpRequest.class),any(HttpResponse.BodyHandler.class))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        var mapper = new ObjectMapper();
        var output = mapper.writeValueAsString(Map.of("experiences",List.of(Map.of("id",1,"reason","협업 사례")),"draft","협업했습니다."));
        when(response.body()).thenReturn(mapper.writeValueAsString(Map.of("candidates",List.of(Map.of("content",Map.of("parts",List.of(Map.of("text",output))))))));
        var controller = new DraftController(repo,mapper,"test-key","gemini-2.5-flash",client);
        var result = controller.create(new DraftController.DraftRequest("협업 경험",List.of(1L)));
        assertThat(result.draft()).isEqualTo("협업했습니다.");
        assertThat(result.experiences()).extracting(DraftController.Recommendation::title).containsExactly("테스트 경험");
        var forged = mapper.writeValueAsString(Map.of("experiences",List.of(Map.of("id",1,"reason","첫째"),Map.of("id",1,"reason","둘째"),Map.of("id",1,"reason","셋째"),Map.of("id",2,"reason","허위")),"draft","초안"));
        when(response.body()).thenReturn(mapper.writeValueAsString(Map.of("candidates",List.of(Map.of("content",Map.of("parts",List.of(Map.of("text",forged))))))));
        assertThatThrownBy(() -> controller.create(new DraftController.DraftRequest("협업 경험",List.of(1L)))).isInstanceOf(ResponseStatusException.class);
    }
}
