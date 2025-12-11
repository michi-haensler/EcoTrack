package at.ecotrack.administration.api;

import at.ecotrack.administration.acl.KeycloakAdapter;
import at.ecotrack.administration.dto.KeycloakUserDto;
import at.ecotrack.administration.dto.SchoolClassDto;
import at.ecotrack.administration.entity.SchoolClass;
import at.ecotrack.administration.repository.SchoolClassRepository;
import at.ecotrack.shared.valueobject.ClassId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementierung der Modul-Facade.
 */
@Service
@RequiredArgsConstructor
public class AdministrationModuleFacadeImpl implements AdministrationModuleFacade {

    private final SchoolClassRepository schoolClassRepository;
    private final KeycloakAdapter keycloakAdapter;

    @Override
    public Optional<SchoolClassDto> getSchoolClass(ClassId classId) {
        return schoolClassRepository.findById(classId.value())
                .map(this::toDto);
    }

    @Override
    public Optional<KeycloakUserDto> getUser(UUID userId) {
        return keycloakAdapter.getUserById(userId);
    }

    @Override
    public List<SchoolClassDto> getClassesForTeacher(UUID teacherUserId) {
        return schoolClassRepository.findByTeacherId(teacherUserId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private SchoolClassDto toDto(SchoolClass schoolClass) {
        return new SchoolClassDto(
                schoolClass.getId(),
                schoolClass.getName(),
                schoolClass.getSchoolYear(),
                schoolClass.getSchool().getId(),
                schoolClass.getSchool().getName(),
                schoolClass.getIsActive(),
                schoolClass.getTeacherUserIds());
    }
}
