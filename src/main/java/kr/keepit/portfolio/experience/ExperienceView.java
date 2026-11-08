package kr.keepit.portfolio.experience;

import java.time.LocalDate;
import java.util.List;

public record ExperienceView(Long id, ExperienceType type, String title, String organization,
        String location, LocalDate startDate, LocalDate endDate, boolean ongoing,
        String participants, String role, String description, String outcome,
        String tags, String link, boolean featured, boolean demo, String customCategory,
        String placement, String awardName, List<String> links, String challenge, String credentialNumber) {
    static ExperienceView from(Experience e) {
        return new ExperienceView(e.id, e.type, e.title, e.organization, e.location,
                e.startDate, e.endDate, e.ongoing, e.participants, e.role,
                e.description, e.outcome, e.tags, e.link, e.featured, e.demo,
                e.customCategory == null ? "" : e.customCategory,
                e.placement == null ? "" : e.placement, e.awardName == null ? "" : e.awardName,
                e.resolvedLinks(), e.challenge == null ? "" : e.challenge, e.credentialNumber == null ? "" : e.credentialNumber);
    }
}
