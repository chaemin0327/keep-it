package kr.keepit.portfolio.profile;

import jakarta.persistence.*;

@Entity
public class Profile {
    @Id Long id = 1L;
    @Column(length = 50) String name = "나의 이름";
    @Column(length = 160) String headline = "새로운 경험을 모으며 성장하는 중";
    @Column(length = 2000) String bio = "내가 만든 것, 도전한 것, 배운 것을 차곡차곡 기록합니다.";
    @Column(length = 160) String email = "";
    public Profile() {}
}
