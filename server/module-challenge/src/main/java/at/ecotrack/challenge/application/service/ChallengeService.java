package at.ecotrack.challenge.application.service;

import at.ecotrack.challenge.application.dto.ChallengeDto;
import at.ecotrack.challenge.domain.model.Challenge;
import at.ecotrack.challenge.domain.model.ChallengeStatus;
import at.ecotrack.challenge.domain.model.GoalUnit;
import at.ecotrack.challenge.domain.port.out.ChallengeRepository;
import at.ecotrack.shared.valueobject.ClassId;
import at.ecotrack.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Application Service für Challenge-Verwaltung.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeDto createChallenge(
            String title,
            String description,
            BigDecimal goalValue,
            GoalUnit goalUnit,
            LocalDate startDate,
            LocalDate endDate,
            ClassId classId,
            UserId createdBy,
            Integer bonusPoints) {
        Challenge challenge = Challenge.create(
                title, description, goalValue, goalUnit,
                startDate, endDate, classId, createdBy, bonusPoints);

        Challenge saved = challengeRepository.save(challenge);
        return toDto(saved);
    }

    public ChallengeDto activateChallenge(UUID challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge nicht gefunden: " + challengeId));

        challenge.activate();
        Challenge saved = challengeRepository.save(challenge);
        return toDto(saved);
    }

    public ChallengeDto closeChallenge(UUID challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge nicht gefunden: " + challengeId));

        challenge.close();
        Challenge saved = challengeRepository.save(challenge);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ChallengeDto> getChallengesForClass(ClassId classId) {
        return challengeRepository.findByClassId(classId.value())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChallengeDto> getActiveChallengesForClass(ClassId classId) {
        return challengeRepository.findByClassIdAndStatus(classId.value(), ChallengeStatus.ACTIVE)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChallengeDto getChallenge(UUID challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge nicht gefunden: " + challengeId));
        return toDto(challenge);
    }

    private ChallengeDto toDto(Challenge challenge) {
        return new ChallengeDto(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getGoalValue(),
                challenge.getGoalUnit(),
                challenge.getStatus(),
                challenge.getStartDate(),
                challenge.getEndDate(),
                challenge.getClassId(),
                challenge.getCreatedBy(),
                challenge.getBonusPoints(),
                challenge.getCreatedAt());
    }
}
