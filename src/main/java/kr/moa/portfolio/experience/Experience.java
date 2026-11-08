package kr.moa.portfolio.experience;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Experience {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR) @Column(nullable = false, length = 32)
    ExperienceType type;
    @Column(nullable = false, length = 120) String title;
    @Column(length = 160) String organization;
    @Column(length = 160) String location;
    @Column(nullable = false) LocalDate startDate;
    LocalDate endDate;
    boolean ongoing;
    @Column(length = 300) String participants;
    @Column(length = 200) String role;
    @Column(length = 10000) String description;
    @Column(length = 1000) String outcome;
    @Column(length = 5000) String challenge;
    @Column(length = 160) String credentialNumber;
    @Column(length = 300) String tags;
    @Column(length = 1000) String link;
    boolean featured;
    boolean demo;
    @Column(length = 30) String customCategory;
    @Column(length = 40) String placement;
    @Column(length = 100) String awardName;
    @ElementCollection
    @CollectionTable(name = "experience_links", joinColumns = @JoinColumn(name = "experience_id"))
    @OrderColumn(name = "link_order")
    @Column(name = "url", length = 1000, nullable = false)
    List<String> links = new ArrayList<>();

    protected Experience() {}

    public Experience(ExperienceRequest request, boolean demo) {
        update(request);
        this.demo = demo;
    }

    public void update(ExperienceRequest r) {
        type = r.type(); title = clean(r.title()); organization = clean(r.organization());
        location = clean(r.location()); startDate = r.startDate();
        endDate = r.ongoing() ? null : r.endDate(); ongoing = r.ongoing();
        participants = clean(r.participants()); role = clean(r.role());
        description = clean(r.description()); outcome = clean(r.outcome());
        if (r.challenge() != null) challenge = clean(r.challenge());
        if (r.credentialNumber() != null) credentialNumber = clean(r.credentialNumber());
        tags = clean(r.tags()); featured = r.featured();
        var requestedLinks = r.links() != null ? r.links()
                : clean(r.link()).isEmpty() ? List.<String>of() : List.of(r.link());
        var normalizedLinks = requestedLinks.stream().map(String::strip).distinct().toList();
        links.clear(); links.addAll(normalizedLinks);
        link = links.isEmpty() ? "" : links.getFirst();
        boolean resultType = type == ExperienceType.COMPETITION || type == ExperienceType.AWARD;
        placement = resultType ? clean(r.placement()) : "";
        awardName = resultType ? clean(r.awardName()) : "";
        customCategory = type == ExperienceType.CUSTOM
                ? clean(r.customCategory()).replaceAll("(?U)\\s+", " ") : "";
    }

    List<String> resolvedLinks() {
        if (!links.isEmpty()) return List.copyOf(links);
        return clean(link).isEmpty() ? List.of() : List.of(link);
    }

    private static String clean(String value) { return value == null ? "" : value.strip(); }
}
