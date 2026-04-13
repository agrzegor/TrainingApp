package pl.coderslab.trainingapp.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.controller.ErrorHandler;
import pl.coderslab.trainingapp.dto.CustomerDto;
import pl.coderslab.trainingapp.dto.TrainerDto;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.Customer;
import pl.coderslab.trainingapp.entity.Trainer;
import pl.coderslab.trainingapp.mappers.Mapper;
import pl.coderslab.trainingapp.repository.CustomerRepository;
import pl.coderslab.trainingapp.repository.TrainerRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final ErrorHandler errorHandler;
    private Mapper mapper;
    private PasswordEncoder passwordEncoder;

    public Trainer createTrainer(UserDto userDto) {

        Trainer trainer = new Trainer();
        trainer.setFirstName(userDto.firstName());
        trainer.setLastName(userDto.lastName());
        trainer.setEmail(userDto.email());
        trainer.setPhone(userDto.phone());
        trainer.setUserType(userDto.userType());
        trainer.setIdentifier("#" + NanoIdUtils
                .randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 5));
        trainer.setPassword(passwordEncoder.encode(userDto.password()));
        try {
            return trainerRepository.save(trainer);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Email already in use");
        }
    }

    public TrainerDto updateTrainerDetails(String trainerEmail, UserDto userDto) {
        Trainer trainer = getTrainerByEmail(trainerEmail);
        trainer.setFirstName(Optional.ofNullable(userDto.firstName()).orElse(trainer.getFirstName()));
        trainer.setLastName(Optional.ofNullable(userDto.lastName()).orElse(trainer.getLastName()));
        trainer.setPhone(Optional.ofNullable(userDto.phone()).orElse(trainer.getPhone()));
        return mapper.toDto(trainerRepository.save(trainer));
    }

    private Trainer getTrainerByEmail(String trainerEmail) {
        return trainerRepository.findTrainersByEmail(trainerEmail)
                .orElseThrow(() -> new NoSuchElementException("User with provided ID not exist."));
    }

    public void unlinkCustomerFromTrainer(String trainerEmail, Long customerId) {
        Trainer trainer = getTrainerByEmail(trainerEmail);
        Customer customer = customerService.getCustomerById(customerId);

        if (customer.getTrainer() != null && customer.getTrainer().getId().equals(trainer.getId())) {
            customer.setTrainer(null);
            customerRepository.save(customer);
        }
    }

    public CustomerDto getCustomerById(String trainerEmail, Long id) {
        Trainer trainer = getTrainerByEmail(trainerEmail);

        return mapper.toDto(trainer.getCustomers()
                .stream()
                .filter(customer -> customer.getId().equals(id)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Customer with provided ID do not exists.")));
    }

}
