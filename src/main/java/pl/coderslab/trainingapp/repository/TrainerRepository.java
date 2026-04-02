package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.trainingapp.entity.Trainer;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
}
