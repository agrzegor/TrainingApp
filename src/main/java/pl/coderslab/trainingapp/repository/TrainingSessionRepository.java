package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.coderslab.trainingapp.entity.TrainingSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    Optional<TrainingSession> getTrainingSessionByIdAndTrainer_Email(Long id, String trainerEmail);

    List<TrainingSession> getTrainingSessionsByCustomer_Id(Long customerId);

    List<TrainingSession> getTrainingSessionsByTrainer_Id(Long trainerId);

    List<TrainingSession> findAllByCustomer_IdAndTrainer_EmailAndStartDateAfter(Long customerId, String trainerEmail, LocalDateTime startDateAfter);

    @Query("SELECT ts FROM TrainingSession ts WHERE ts.trainer.id = :trainerId " +
            "AND (:excludeSessionId IS NULL OR ts.id != :excludeSessionId) AND (" +
            "ts.startDate BETWEEN :startDate AND TIMESTAMPADD(MINUTE, :duration, :startDate) OR " +
            ":startDate BETWEEN ts.startDate AND TIMESTAMPADD(MINUTE, ts.duration, ts.startDate))")
    List<TrainingSession> findOverlappingSessions(@Param("trainerId") Long trainerId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("duration") int duration,
                                                  @Param("excludeSessionId") Long excludeSessionId);
}
