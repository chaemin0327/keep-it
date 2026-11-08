package kr.moa.portfolio;

import kr.moa.portfolio.experience.*;
import kr.moa.portfolio.profile.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Component
public class DemoData implements CommandLineRunner {
    private final ExperienceRepository experiences;
    private final ProfileRepository profiles;
    public DemoData(ExperienceRepository experiences, ProfileRepository profiles) {
        this.experiences = experiences; this.profiles = profiles;
    }

    @Override @Transactional
    public void run(String... args) {
        // 프로필을 최초 실행 표시로 사용하므로 예시 삭제 후 재시작해도 다시 생기지 않습니다.
        if (profiles.existsById(1L)) return;
        profiles.save(new Profile());
        add(ExperienceType.PROJECT, "일상의 발견을 기록하는 지도", "교내 개발 동아리", "부산", "2026-09-01", null,
                "팀원 3명", "백엔드 개발", "좋아하는 장소에 짧은 기록을 남기는 위치 기반 서비스. 팀원들과 기획부터 API 설계, 배포까지 함께했습니다.",
                "위치 기반 검색 API와 쪽지 작성 기능 구현", "Java, Spring Boot, PostgreSQL", true);
        add(ExperienceType.COMPETITION, "캠퍼스 문제 해결 해커톤", "대학 창업지원단", "교내 메이커스페이스", "2026-08-22", "2026-08-23",
                "팀 모먼트 · 4명", "서비스 기획 · 개발", "빈 강의실을 쉽게 찾을 수 있는 캠퍼스 공간 공유 서비스를 제안하고 이틀 동안 프로토타입을 구현했습니다.",
                "우수상", "해커톤, 협업, 프로토타입", true);
        add(ExperienceType.CERTIFICATE, "SQL 개발자 (SQLD)", "한국데이터산업진흥원", "", "2026-07-10", null,
                "", "", "데이터 모델링과 SQL 기본·활용을 공부하고 자격을 취득했습니다.",
                "최종 합격", "SQL, 데이터베이스", true);
        add(ExperienceType.PAPER, "경량 모델을 활용한 이미지 분류 실험", "교내 학부생 연구 발표회", "정보컴퓨터공학 연구실", "2026-05-01", "2026-06-20",
                "공동저자 2명", "실험 설계 · 결과 분석", "제한된 컴퓨팅 환경에서 이미지 분류 모델의 정확도와 추론 속도를 비교한 학부 연구 기록입니다.",
                "포스터 발표", "Python, 딥러닝, 실험", false);
        add(ExperienceType.OVERSEAS, "도쿄 대학생 글로벌 교류", "국제교류본부", "일본 도쿄", "2026-01-12", "2026-01-19",
                "교류 프로그램 참가자", "참가 학생", "현지 대학생들과 도시의 생활 문제를 조사하고, 서로의 아이디어를 나누는 팀 프로젝트에 참여했습니다.",
                "공동 발표 및 프로그램 수료", "글로벌, 교류, 팀워크", false);
        add(ExperienceType.ACTIVITY, "개발 동아리 백엔드 스터디", "교내 개발 동아리", "부산", "2025-09-01", "2025-12-20",
                "스터디원 5명", "스터디 운영", "매주 Java와 HTTP를 학습하고 작은 API를 함께 만들었습니다. 발표와 코드 리뷰를 통해 배운 내용을 정리했습니다.",
                "12회 스터디 진행", "Java, HTTP, 코드 리뷰", false);
    }

    private void add(ExperienceType type, String title, String organization, String location,
            String start, String end, String people, String role, String description,
            String outcome, String tags, boolean featured) {
        experiences.save(new Experience(new ExperienceRequest(type, title, organization, location,
                LocalDate.parse(start), end == null ? null : LocalDate.parse(end),
                type == ExperienceType.PROJECT, people, role, description, outcome, tags, "", featured, "", "", "", null, "", ""), true));
    }
}
