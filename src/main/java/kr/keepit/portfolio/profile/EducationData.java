package kr.keepit.portfolio.profile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EducationData(
        @Size(max = 120) String school,
        @Pattern(regexp = "|중학교|고등학교|전문대학|대학교|대학원\\(석사\\)|대학원\\(박사\\)|기타") String level,
        @Pattern(regexp = "|재학|휴학|졸업 예정|졸업|수료|중퇴|기타") String status,
        @Size(max = 120) String major) {
}
