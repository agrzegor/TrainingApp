package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.coderslab.trainingapp.entity.SessionExercise;

import java.util.List;

@Repository
public interface SessionExerciseRepository extends JpaRepository<SessionExercise, Long> {

    @Query("SELECT se FROM session_exercise se " +
            "WHERE se.trainingSession.id = :trainingSessionId " +
            "AND se.trainingSession.customer.id = :trainingSessionCustomerId")
    List<SessionExercise> getSessionExercisesByTrainingSession_IdAndTrainingSessionCustomer_Id(
           @Param("trainingSessionId") Long trainingSessionId,
           @Param("trainingSessionCustomerId") Long trainingSessionCustomerId);

    @Query("SELECT se FROM session_exercise se " +
            "WHERE se.trainingSession.id = :trainingSessionId " +
            "AND se.trainingSession.trainer.id = :trainingSessionTrainerId")
    List<SessionExercise> getSessionExercisesByTrainingSession_IdAndTrainingSession_Trainer_Id(
            @Param("trainingSessionId") Long trainingSessionId,
            @Param("trainingSessionTrainerId") Long trainingSessionTrainerId);


    List<SessionExercise> findSessionExerciseByTrainingSession_IdAndExercise_Id(Long trainingSessionId, Long exerciseId);
}
