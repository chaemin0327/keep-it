package kr.keepit.portfolio.profile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserProfileRepository repository;
    public ProfileController(UserProfileRepository repository) { this.repository = repository; }

    public record ProfileData(@NotBlank @Size(max = 50) String name,
            @Size(max = 160) String headline, @Size(max = 2000) String bio,
            @Email @Size(max = 160) String email) {
        static ProfileData from(UserProfile p) { return new ProfileData(p.name, p.headline, p.bio, p.email); }
    }

    @GetMapping
    public ProfileData get() { return ProfileData.from(repository.findById(owner()).orElseGet(() -> repository.save(new UserProfile(owner())))); }

    @PutMapping
    @Transactional
    public ProfileData update(@Valid @RequestBody ProfileData request) {
        var profile = repository.findById(owner()).orElseGet(() -> repository.save(new UserProfile(owner())));
        profile.name = request.name().strip();
        profile.headline = clean(request.headline()); profile.bio = clean(request.bio());
        profile.email = clean(request.email());
        return ProfileData.from(profile);
    }

    private String owner() { return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName(); }
    private String clean(String value) { return value == null ? "" : value.strip(); }
}
