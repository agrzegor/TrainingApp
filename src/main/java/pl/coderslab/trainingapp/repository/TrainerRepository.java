package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.trainingapp.entity.Trainer;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByIdentifier(String identifier);

    Optional<Trainer> findTrainerByEmail(String email);

    boolean existsByEmail(String email);
}
