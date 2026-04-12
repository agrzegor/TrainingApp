package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.trainingapp.entity.TrainingSession;

import java.util.List;
import java.util.Optional;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    Optional<TrainingSession> getTrainingSessionByIdAndTrainer_Email(Long id, String trainerEmail);

    TrainingSession getTrainingSessionById(Long id);

    List<TrainingSession> getTrainingSessionsByCustomer_Id(Long customerId);

    List<TrainingSession> getTrainingSessionsByTrainer_Id(Long trainerId);
}
