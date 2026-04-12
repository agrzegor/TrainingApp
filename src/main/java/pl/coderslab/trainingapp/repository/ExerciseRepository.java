package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.trainingapp.entity.Exercise;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {


    Optional<Exercise> findExerciseById(Long id);
    HttpHeaders findByName(String name);

    Optional<Exercise> findExerciseByExternalExerciseId(String exerciseId);
}
