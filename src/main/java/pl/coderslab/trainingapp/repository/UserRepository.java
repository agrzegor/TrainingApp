package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.trainingapp.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
