package pl.coderslab.trainingapp.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.Customer;
import pl.coderslab.trainingapp.repository.CustomerRepository;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(UserDto userDto) {

        Customer customer = new Customer();
        customer.setFirstName(userDto.firstName());
        customer.setLastName(userDto.lastName());
        customer.setEmail(userDto.email());
        customer.setPhone(userDto.phone());
        customer.setUserType(userDto.userType());

        return customerRepository.save(customer);
    }

    public void addCustomerToTrainer() {

    }

    public Customer getCustomerById(Long id) {
        return customerRepository.getCustomerById(id);

    }

    public Customer updateCustomerById(Long id, UserDto userDto) {
        Customer customer = customerRepository.findById(id)
                .map(customerUpd -> {
                    customerUpd.setFirstName(userDto.firstName());
                    customerUpd.setLastName(userDto.lastName());
                    customerUpd.setPhone(userDto.phone());
                    customerUpd.setEmail(userDto.email());
                    return customerUpd;
                }).orElseThrow(() -> new NoSuchElementException("No customer with provided id"));
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
