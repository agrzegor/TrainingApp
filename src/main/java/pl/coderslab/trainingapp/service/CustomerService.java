package pl.coderslab.trainingapp.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.CustomerDto;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.dto.api.UpdateProfileRequest;
import pl.coderslab.trainingapp.entity.Customer;
import pl.coderslab.trainingapp.entity.Trainer;
import pl.coderslab.trainingapp.mappers.Mapper;
import pl.coderslab.trainingapp.repository.CustomerRepository;
import pl.coderslab.trainingapp.repository.TrainerRepository;
import pl.coderslab.trainingapp.repository.TrainingSessionRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TrainerRepository trainerRepository;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final TrainingSessionRepository trainingSessionRepository;

    public Customer createCustomer(UserDto userDto) {
        if (customerRepository.existsByEmail(userDto.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

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
        Trainer trainer = trainerRepository.findByIdentifier(trainerIdentifier)
                .orElseThrow(() -> new NoSuchElementException("Trainer with provided identifier does not exist."));
        Customer customer = getCustomerByEmail(email);

        if (customer.getTrainer() != null) {
            throw new IllegalArgumentException("Customer is already assigned to a trainer. Unlink first before reassigning.");
        }

        customer.setTrainer(trainer);
        customerRepository.save(customer);
    }


    public CustomerDto getCustomerDtoByEmail(String customerEmail) {
        return mapper.toDto(customerRepository.findCustomerByEmail(customerEmail)
                .orElseThrow(() -> new NoSuchElementException("Customer with provided email does not exist.")));

    }


    public List<CustomerDto> getAllCustomersByTrainer(String email) {
        List<Customer> customerList = customerRepository.findAllByTrainer_Email(email);

        return customerList.stream().map(mapper::toDto).toList();
    }

    public CustomerDto updateCustomer(String customerEmail, UpdateProfileRequest request) {
        Customer customer = customerRepository.findCustomerByEmail(customerEmail)
                .map(customerUpd -> {
                    Optional.ofNullable(request.firstName()).filter(s -> !s.isBlank()).ifPresent(customerUpd::setFirstName);
                    Optional.ofNullable(request.lastName()).filter(s -> !s.isBlank()).ifPresent(customerUpd::setLastName);
                    String phone = request.phone();
                    customerUpd.setPhone(phone == null || phone.isBlank() ? null : phone);
                    return customerUpd;
                }).orElseThrow(() -> new NoSuchElementException("No customer with provided id"));
        return mapper.toDto(customerRepository.save(customer));
    }

    private Customer getCustomerByEmail(String customerEmail) {
        return customerRepository.findCustomerByEmail(customerEmail)
                .orElseThrow(() -> new NoSuchElementException("User with provided ID does not exist."));
    }

    Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("User with provided ID does not exist."));
    }

    public void selfUnlinkFromTrainer(String email) {
        Customer customer = getCustomerByEmail(email);
        if (customer.getTrainer() == null) {
            throw new NoSuchElementException("Customer is not linked to any trainer.");
        }
        trainingSessionRepository.deleteAll(
                trainingSessionRepository.findAllByCustomer_IdAndTrainer_EmailAndStartDateAfter(
                        customer.getId(), customer.getTrainer().getEmail(), LocalDateTime.now())
        );
        customer.setTrainer(null);
        customerRepository.save(customer);
    }
}
