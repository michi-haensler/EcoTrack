package at.ecotrack.userprofile.api;

import at.ecotrack.shared.valueobject.ClassId;
import at.ecotrack.shared.valueobject.EcoUserId;
import at.ecotrack.userprofile.dto.EcoUserDto;
import at.ecotrack.userprofile.dto.LeaderboardEntryDto;
import at.ecotrack.userprofile.dto.MilestoneDto;
import at.ecotrack.userprofile.entity.EcoUser;
import at.ecotrack.userprofile.repository.EcoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementierung der Modul-Facade.
 */
@Service
@RequiredArgsConstructor
public class UserProfileModuleFacadeImpl implements UserProfileModuleFacade {

    private final EcoUserRepository ecoUserRepository;

    @Override
    public Optional<EcoUserDto> getEcoUser(EcoUserId ecoUserId) {
        return ecoUserRepository.findById(ecoUserId.value())
                .map(this::toDto);
    }

    @Override
    public Optional<EcoUserDto> getEcoUserByUserId(UUID userId) {
        return ecoUserRepository.findByUserId(userId)
                .map(this::toDto);
    }

    @Override
    public List<LeaderboardEntryDto> getClassLeaderboard(ClassId classId, int limit) {
        AtomicInteger rank = new AtomicInteger(1);
        return ecoUserRepository.findByClassIdOrderByPointsDesc(classId.value())
                .stream()
                .limit(limit)
                .map(user -> new LeaderboardEntryDto(
                        rank.getAndIncrement(),
                        user.getId(),
                        user.getUserId(),
                        user.getTotalPoints(),
                        user.getLevel().getDisplayName()))
                .toList();
    }

    private EcoUserDto toDto(EcoUser user) {
        List<MilestoneDto> milestoneDtos = user.getMilestones().stream()
                .map(m -> new MilestoneDto(
                        m.getId(),
                        m.getName(),
                        m.getRequiredPoints(),
                        m.getBadgeAsset(),
                        m.getDescription()))
                .toList();

        return new EcoUserDto(
                user.getId(),
                user.getUserId(),
                user.getClassId(),
                user.getTotalPoints(),
                user.getLevel(),
                milestoneDtos,
                user.getCreatedAt());
    }
}
