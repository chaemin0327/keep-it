package kr.keepit.portfolio.experience;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/experiences")
public class ExperienceController {
    private final ExperienceRepository repository;
    public ExperienceController(ExperienceRepository repository) { this.repository = repository; }

    @GetMapping
    public List<ExperienceView> list() {
        return repository.findAllByOwner(owner(), Sort.by(Sort.Order.desc("startDate"), Sort.Order.desc("id")))
                .stream().map(ExperienceView::from).toList();
    }

    @PostMapping
    public ResponseEntity<ExperienceView> create(@Valid @RequestBody ExperienceRequest request) {
        validate(request);
        var item = new Experience(request, false);
        item.owner = owner();
        var view = ExperienceView.from(repository.save(item));
        return ResponseEntity.created(URI.create("/api/experiences/" + view.id())).body(view);
    }

    public record ImportRequest(@jakarta.validation.constraints.NotEmpty @jakarta.validation.constraints.Size(max=200)
            List<@Valid @jakarta.validation.constraints.NotNull ExperienceRequest> experiences) {}

    @PostMapping("/import")
    @Transactional
    public java.util.Map<String,Integer> importExperiences(@Valid @RequestBody ImportRequest request) {
        var items=new java.util.ArrayList<Experience>();
        for(var entry:request.experiences()) {
            validate(entry);
            var item=new Experience(entry,false);
            item.owner=owner();
            items.add(item);
        }
        repository.saveAll(items);
        return java.util.Map.of("imported",items.size());
    }

    @GetMapping("/{id}")
    public ExperienceView get(@PathVariable Long id) { return ExperienceView.from(find(id)); }

    @PutMapping("/{id}")
    @Transactional
    public ExperienceView update(@PathVariable Long id, @Valid @RequestBody ExperienceRequest request) {
        validate(request);
        var item = find(id);
        item.update(request);
        return ExperienceView.from(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { repository.delete(find(id)); }

    @DeleteMapping("/demo")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDemo() { repository.deleteByOwnerAndDemoTrue(owner()); }

    private Experience find(Long id) {
        return repository.findByIdAndOwner(id, owner()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "경험을 찾을 수 없어요."));
    }

    private String owner() { return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName(); }

    private void validate(ExperienceRequest r) {
        if (r.type() == ExperienceType.CUSTOM &&
                (r.customCategory() == null || r.customCategory().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "직접 추가할 분류 이름을 입력해주세요.");
        }
        if (r.endDate() != null && !r.ongoing() && r.endDate().isBefore(r.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "종료일은 시작일 이후로 입력해주세요.");
        }
        var links = r.links() != null ? r.links()
                : r.link() == null || r.link().isBlank() ? List.<String>of() : List.of(r.link());
        for (var link : links) {
            try {
                var uri = URI.create(link.strip());
                if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                        || uri.getHost() == null) throw new IllegalArgumentException();
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "링크는 http:// 또는 https://로 시작하는 주소를 입력해주세요.");
            }
        }
    }
}
