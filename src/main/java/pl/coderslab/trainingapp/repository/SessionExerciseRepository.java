package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.trainingapp.entity.SessionExercise;

import java.util.Arrays;
import java.util.List;

public interface SessionExerciseRepository extends JpaRepository<SessionExercise, Long> {

    Arrays getSessionExercisesById(Long id);

    List<SessionExercise> getSessionExercisesByTrainingSession_IdAndTrainingSessionCustomer_Id(Long trainingSessionId, Long trainingSessionId1);

    List<SessionExercise> getSessionExercisesByTrainingSession_IdAndTrainingSession_Trainer_Id(Long trainingSessionId, Long trainingSessionTrainerId);
}
