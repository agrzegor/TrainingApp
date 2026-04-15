package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.trainingapp.entity.Customer;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {



    List<Customer> findAllByTrainer_Email(String email);

    Optional<Customer> findCustomerByEmail(String email);

    Optional<Customer> findCustomerById(Long id);

    boolean existsByEmail(String email);
}
