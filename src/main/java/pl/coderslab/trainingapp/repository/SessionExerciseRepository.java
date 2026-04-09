package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.trainingapp.entity.SessionExercise;

public interface SessionExerciseRepository extends JpaRepository<SessionExercise, Long> {

}
