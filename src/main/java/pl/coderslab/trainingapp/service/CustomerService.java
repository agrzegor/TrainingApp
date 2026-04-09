package pl.coderslab.trainingapp.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.CustomerDto;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.Customer;
import pl.coderslab.trainingapp.entity.Trainer;
import pl.coderslab.trainingapp.mappers.Mapper;
import pl.coderslab.trainingapp.repository.CustomerRepository;
import pl.coderslab.trainingapp.repository.TrainerRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TrainerRepository trainerRepository;
    private Mapper mapper;
    private PasswordEncoder passwordEncoder;

    public Customer createCustomer(UserDto userDto) {

        Customer customer = new Customer();
        customer.setFirstName(userDto.firstName());
        customer.setLastName(userDto.lastName());
        customer.setEmail(userDto.email());
        customer.setPhone(userDto.phone());
        customer.setUserType(userDto.userType());
        customer.setPassword(passwordEncoder.encode(userDto.password()));

        return (customerRepository.save(customer));

    }

    public void addCustomerToTrainer(String email, String trainerIdentifier) {
        Trainer trainer1 = trainerRepository.findByIdentifier(trainerIdentifier)
                .orElseThrow(() -> new NoSuchElementException("Trainer with provided identifier do not exists."));
        Customer customer = customerRepository.getCustomersByEmail(email);
        customer.setTrainer(trainer1);
        customerRepository.save(customer);
    }


    public CustomerDto getCustomerDtoByEmail(String customerEmail) {
        return mapper.toDto(customerRepository.findCustomerByEmail(customerEmail)
                .orElseThrow(() -> new NoSuchElementException("Customer with provided ID do not exists.")));

    }


    public List<CustomerDto> getAllCustomersByTrainer(String email) {
        List<Customer> customerList = customerRepository.findAllByTrainer_Email(email);
        List<CustomerDto> customerDtoList = new ArrayList<>();

        for (Customer customer : customerList) {
            CustomerDto customerDto = mapper.toDto(customer);
            customerDtoList.add(customerDto);
        }
        return customerDtoList;

    }

    public CustomerDto updateCustomer(String customerEmail, UserDto userDto) {
        Customer customer = customerRepository.findCustomerByEmail(customerEmail)
                .map(customerUpd -> {
                    customerUpd.setFirstName(Optional.ofNullable(userDto.firstName())
                            .orElse(customerUpd.getFirstName()));
                    customerUpd.setLastName(Optional.ofNullable(userDto.lastName())
                            .orElse(customerUpd.getLastName()));
                    customerUpd.setPhone(Optional.ofNullable(userDto.phone())
                            .orElse(customerUpd.getPhone()));
                    return customerUpd;
                }).orElseThrow(() -> new NoSuchElementException("No customer with provided id"));
        return mapper.toDto(customerRepository.save(customer));
    }

    private Customer getCustomerByEmail(String customerEmail) {
        return customerRepository.findCustomerByEmail(customerEmail)
                .orElseThrow(() -> new NoSuchElementException("User with provided ID not exist."));
    }

    Customer getCustomerById(Long customerId) {
        return customerRepository.findCustomerById(customerId)
                .orElseThrow(() -> new NoSuchElementException("User with provided ID not exist."));
    }
}
