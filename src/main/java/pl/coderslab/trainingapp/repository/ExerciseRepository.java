package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.trainingapp.entity.Exercise;

import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {


    Optional<Exercise> findExerciseById(Long id);

    Optional<Exercise> findExerciseByExternalExerciseId(String exerciseId);

}
