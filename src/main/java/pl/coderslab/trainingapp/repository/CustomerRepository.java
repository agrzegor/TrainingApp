package pl.coderslab.trainingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.trainingapp.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Customer getCustomerById(Long id);
}
