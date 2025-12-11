package at.ecotrack.administration.repository;

import at.ecotrack.administration.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data Repository für SchoolClass.
 */
@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, UUID> {

    List<SchoolClass> findBySchoolId(UUID schoolId);

    List<SchoolClass> findBySchoolIdAndIsActiveTrue(UUID schoolId);

    @Query("SELECT sc FROM SchoolClass sc WHERE :teacherId MEMBER OF sc.teacherUserIds")
    List<SchoolClass> findByTeacherId(@Param("teacherId") UUID teacherId);

    @Query("SELECT sc FROM SchoolClass sc WHERE sc.school.id = :schoolId AND sc.schoolYear = :year")
    List<SchoolClass> findBySchoolIdAndYear(
            @Param("schoolId") UUID schoolId,
            @Param("year") String year);
}
