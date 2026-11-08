package kr.keepit.portfolio.experience;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record ExperienceRequest(
        @NotNull(message = "경험 종류를 선택해주세요.") ExperienceType type,
        @NotBlank(message = "제목을 입력해주세요.") @Size(max = 120) String title,
        @Size(max = 160) String organization,
        @Size(max = 160) String location,
        @NotNull(message = "시작일을 입력해주세요.") LocalDate startDate,
        LocalDate endDate,
        boolean ongoing,
        @Size(max = 300) String participants,
        @Size(max = 200) String role,
        @Size(max = 10000) String description,
        @Size(max = 1000) String outcome,
        @Size(max = 300) String tags,
        @Size(max = 1000) String link,
        boolean featured,
        @Size(max = 30, message = "분류 이름은 30자 이내로 입력해주세요.") String customCategory,
        @Size(max = 40) String placement,
        @Size(max = 100) String awardName,
        @Size(max = 10, message = "링크는 최대 10개까지 추가할 수 있어요.")
        List<@NotBlank @Size(max = 1000) String> links,
        @Size(max = 5000) String challenge,
        @Size(max = 160) String credentialNumber
) {}
