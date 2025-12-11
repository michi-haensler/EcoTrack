package at.ecotrack.administration.controller;

import at.ecotrack.administration.acl.KeycloakAdapter;
import at.ecotrack.administration.dto.KeycloakUserDto;
import at.ecotrack.administration.dto.SchoolClassDto;
import at.ecotrack.administration.dto.SchoolDto;
import at.ecotrack.administration.entity.School;
import at.ecotrack.administration.entity.SchoolClass;
import at.ecotrack.administration.repository.SchoolClassRepository;
import at.ecotrack.administration.repository.SchoolRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller für Administration-Modul.
 * Verwaltet Schulen, Klassen und bietet Zugriff auf Keycloak-Benutzer.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdministrationController {

    private final SchoolRepository schoolRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final KeycloakAdapter keycloakAdapter;

    // ==================== Schools ====================

    @PostMapping("/schools")
    public ResponseEntity<SchoolDto> createSchool(@Valid @RequestBody CreateSchoolRequest request) {
        if (schoolRepository.existsByCode(request.code())) {
            return ResponseEntity.badRequest().build();
        }

        School school = School.builder()
                .name(request.name())
                .code(request.code())
                .address(request.address())
                .contactEmail(request.contactEmail())
                .build();

        School saved = schoolRepository.save(school);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @GetMapping("/schools")
    public ResponseEntity<List<SchoolDto>> getAllSchools(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        List<School> schools = activeOnly
                ? schoolRepository.findByIsActiveTrue()
                : schoolRepository.findAll();

        return ResponseEntity.ok(schools.stream().map(this::toDto).toList());
    }

    @GetMapping("/schools/{id}")
    public ResponseEntity<SchoolDto> getSchool(@PathVariable UUID id) {
        return schoolRepository.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/schools/{id}")
    public ResponseEntity<SchoolDto> updateSchool(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSchoolRequest request) {
        return schoolRepository.findById(id)
                .map(school -> {
                    school.setName(request.name());
                    school.setAddress(request.address());
                    school.setContactEmail(request.contactEmail());
                    school.setIsActive(request.isActive());
                    return ResponseEntity.ok(toDto(schoolRepository.save(school)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== SchoolClasses ====================

    @PostMapping("/schools/{schoolId}/classes")
    public ResponseEntity<SchoolClassDto> createClass(
            @PathVariable UUID schoolId,
            @Valid @RequestBody CreateClassRequest request) {
        return schoolRepository.findById(schoolId)
                .map(school -> {
                    SchoolClass schoolClass = SchoolClass.builder()
                            .name(request.name())
                            .schoolYear(request.schoolYear())
                            .school(school)
                            .build();
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(toClassDto(schoolClassRepository.save(schoolClass)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/schools/{schoolId}/classes")
    public ResponseEntity<List<SchoolClassDto>> getClassesBySchool(
            @PathVariable UUID schoolId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        List<SchoolClass> classes = activeOnly
                ? schoolClassRepository.findBySchoolIdAndIsActiveTrue(schoolId)
                : schoolClassRepository.findBySchoolId(schoolId);

        return ResponseEntity.ok(classes.stream().map(this::toClassDto).toList());
    }

    @GetMapping("/classes/{id}")
    public ResponseEntity<SchoolClassDto> getClass(@PathVariable UUID id) {
        return schoolClassRepository.findById(id)
                .map(this::toClassDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/classes/{classId}/teachers/{teacherId}")
    public ResponseEntity<SchoolClassDto> addTeacherToClass(
            @PathVariable UUID classId,
            @PathVariable UUID teacherId) {
        return schoolClassRepository.findById(classId)
                .map(schoolClass -> {
                    schoolClass.addTeacher(teacherId);
                    return ResponseEntity.ok(toClassDto(schoolClassRepository.save(schoolClass)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/classes/{classId}/teachers/{teacherId}")
    public ResponseEntity<SchoolClassDto> removeTeacherFromClass(
            @PathVariable UUID classId,
            @PathVariable UUID teacherId) {
        return schoolClassRepository.findById(classId)
                .map(schoolClass -> {
                    schoolClass.removeTeacher(teacherId);
                    return ResponseEntity.ok(toClassDto(schoolClassRepository.save(schoolClass)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== Keycloak Users (via ACL) ====================

    @GetMapping("/users")
    public ResponseEntity<List<KeycloakUserDto>> getAllUsers() {
        return ResponseEntity.ok(keycloakAdapter.getAllUsers());
    }

    @GetMapping("/users/teachers")
    public ResponseEntity<List<KeycloakUserDto>> getTeachers() {
        return ResponseEntity.ok(keycloakAdapter.getTeachers());
    }

    @GetMapping("/users/students")
    public ResponseEntity<List<KeycloakUserDto>> getStudents() {
        return ResponseEntity.ok(keycloakAdapter.getStudents());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<KeycloakUserDto> getUserById(@PathVariable UUID id) {
        return keycloakAdapter.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== Mapping ====================

    private SchoolDto toDto(School school) {
        Set<SchoolClassDto> classDtos = school.getClasses().stream()
                .map(this::toClassDto)
                .collect(Collectors.toSet());

        return new SchoolDto(
                school.getId(),
                school.getName(),
                school.getCode(),
                school.getAddress(),
                school.getContactEmail(),
                school.getIsActive(),
                classDtos,
                school.getCreatedAt());
    }

    private SchoolClassDto toClassDto(SchoolClass schoolClass) {
        return new SchoolClassDto(
                schoolClass.getId(),
                schoolClass.getName(),
                schoolClass.getSchoolYear(),
                schoolClass.getSchool().getId(),
                schoolClass.getSchool().getName(),
                schoolClass.getIsActive(),
                schoolClass.getTeacherUserIds());
    }

    // ==================== Request DTOs ====================

    public record CreateSchoolRequest(
            @NotBlank String name,
            @NotBlank String code,
            String address,
            String contactEmail) {
    }

    public record UpdateSchoolRequest(
            @NotBlank String name,
            String address,
            String contactEmail,
            Boolean isActive) {
    }

    public record CreateClassRequest(
            @NotBlank String name,
            @NotBlank String schoolYear) {
    }
}
