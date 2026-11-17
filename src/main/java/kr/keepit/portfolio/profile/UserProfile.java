package kr.keepit.portfolio.profile;
import jakarta.persistence.*;
@Entity
public class UserProfile {
    @Id @Column(length=160) String owner;
    @Column(length=50) String name="나의 이름";
    @Column(length=160) String headline="새로운 경험을 모으며 성장하는 중";
    @Column(length=2000) String bio="내가 만든 것, 도전한 것, 배운 것을 차곡차곡 기록합니다.";
    @Column(length=160) String email="";
    @Column(length = 120) String school;
    @Column(length = 30) String educationLevel;
    @Column(length = 30) String enrollmentStatus;
    @Column(length = 120) String major;
    protected UserProfile() {}
    UserProfile(String owner) { this.owner=owner; }
    public static UserProfile forRegistration(String owner, String name, String headline, EducationData education) {
        var profile = new UserProfile(owner);
        if (name != null) profile.name = name.strip();
        if (headline != null && !headline.isBlank()) profile.headline = headline.strip();
        profile.updateEducation(education);
        return profile;
    }

    void updateEducation(EducationData education) {
        if (education == null) return;
        school = clean(education.school());
        educationLevel = clean(education.level());
        enrollmentStatus = clean(education.status());
        major = clean(education.major());
    }

    EducationData education() {
        return new EducationData(school, educationLevel, enrollmentStatus, major);
    }

    private static String clean(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
