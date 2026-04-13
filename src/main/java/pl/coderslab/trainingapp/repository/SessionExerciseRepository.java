package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.coderslab.trainingapp.entity.SessionExercise;

import java.util.List;

public interface SessionExerciseRepository extends JpaRepository<SessionExercise, Long> {

    @Query("SELECT se FROM session_exercise se " +
            "WHERE se.trainingSession.id = :trainingSessionId " +
            "AND se.trainingSession.customer.id = :trainingSessionCustomerId")
    List<SessionExercise> getSessionExercisesByTrainingSession_IdAndTrainingSessionCustomer_Id(Long trainingSessionId, Long trainingSessionId1);

    @Query("SELECT se FROM session_exercise se " +
            "WHERE se.trainingSession.id = :trainingSessionId " +
            "AND se.trainingSession.trainer.id = :trainingSessionTrainerId")
    List<SessionExercise> getSessionExercisesByTrainingSession_IdAndTrainingSession_Trainer_Id(Long trainingSessionId, Long trainingSessionTrainerId);
}
