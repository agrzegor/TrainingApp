package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.trainingapp.entity.User;

import java.net.http.HttpHeaders;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>  {
    Optional<User> findByEmail(String email);

}
