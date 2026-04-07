package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.trainingapp.entity.Customer;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Customer getCustomerById(Long id);

    List<Customer> findAllByTrainer_Id(Long trainerId);

    List<Customer> findAllByTrainer_Email(String email);

    Customer getCustomersByEmail(String email);
}
