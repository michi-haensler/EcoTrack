package at.htl.ecotrack.userprofile.application;

import at.htl.ecotrack.shared.error.ApiException;
import at.htl.ecotrack.shared.model.Role;
import at.htl.ecotrack.userprofile.domain.EcoUserProfile;
import at.htl.ecotrack.userprofile.domain.EcoUserProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EcoUserProfileService {

    private final EcoUserProfileRepository repository;

    public EcoUserProfileService(EcoUserProfileRepository repository) {
        this.repository = repository;
    }

    public EcoUserProfile createProfile(UUID userId,
                                        String email,
                                        String firstName,
                                        String lastName,
                                        Role role,
                                        UUID classId,
                                        String className,
                                        UUID schoolId,
                                        String schoolName) {
        EcoUserProfile profile = new EcoUserProfile();
        profile.setEcoUserId(UUID.randomUUID());
        profile.setUserId(userId);
        profile.setEmail(email);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setDisplayName(firstName + " " + lastName);
        profile.setClassId(role == Role.SCHUELER ? classId : null);
        profile.setClassName(role == Role.SCHUELER ? className : null);
        profile.setSchoolId(schoolId);
        profile.setSchoolName(schoolName);
        return repository.save(profile);
    }

    public EcoUserProfile getByEcoUserId(UUID ecoUserId) {
        return repository.findById(ecoUserId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ECO_USER_NOT_FOUND", "EcoUser nicht gefunden"));
    }

    public EcoUserProfile getByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ECO_USER_NOT_FOUND", "EcoUser nicht gefunden"));
    }

    public List<EcoUserProfile> getByClassId(UUID classId) {
        return repository.findByClassId(classId);
    }

    public List<EcoUserProfile> getBySchoolId(UUID schoolId) {
        return repository.findBySchoolId(schoolId);
    }
}
