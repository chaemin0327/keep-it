package kr.keepit.portfolio.experience;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    @Override @EntityGraph(attributePaths = "links")
    List<Experience> findAll(Sort sort);
    @Override @EntityGraph(attributePaths = "links")
    Optional<Experience> findById(Long id);
    long deleteByDemoTrue();
}
