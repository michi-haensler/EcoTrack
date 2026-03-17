package at.htl.ecotrack.administration.api;

import at.htl.ecotrack.administration.application.AuthDtos;
import at.htl.ecotrack.administration.application.AuthService;
import at.htl.ecotrack.shared.model.Role;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/users")
    public AuthDtos.UserPage users(@RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "size", defaultValue = "20") int size,
                                   @RequestParam(name = "role", required = false) Role role) {
        return authService.getUsers(page, size, role);
    }

    @GetMapping("/classes")
    public List<AuthDtos.ClassResponse> classes() {
        return authService.getClasses();
    }

    @PostMapping("/classes")
    public AuthDtos.ClassResponse createClass(@Valid @RequestBody AuthDtos.CreateClassRequest request) {
        return authService.createClass(request);
    }
}
