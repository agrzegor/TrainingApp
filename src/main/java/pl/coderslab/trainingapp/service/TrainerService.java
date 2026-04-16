package pl.coderslab.trainingapp.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.CustomerDto;
import pl.coderslab.trainingapp.dto.TrainerDto;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.dto.api.UpdateProfileRequest;
import pl.coderslab.trainingapp.entity.Customer;
import pl.coderslab.trainingapp.entity.Trainer;
import pl.coderslab.trainingapp.mappers.Mapper;
import pl.coderslab.trainingapp.repository.CustomerRepository;
import pl.coderslab.trainingapp.repository.TrainerRepository;
import pl.coderslab.trainingapp.repository.TrainingSessionRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final CustomerRepository customerRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final CustomerService customerService;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;



    public Trainer createTrainer(UserDto userDto) {
        if (trainerRepository.existsByEmail((userDto.email()))) {
            throw new IllegalArgumentException("Email already in use");
        }
        Trainer trainer = new Trainer();
        trainer.setFirstName(userDto.firstName());
        trainer.setLastName(userDto.lastName());
        trainer.setEmail(userDto.email());
        trainer.setPhone(userDto.phone());
        trainer.setUserType(userDto.userType());
        trainer.setIdentifier("#" + NanoIdUtils
                .randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 5));
        trainer.setPassword(passwordEncoder.encode(userDto.password()));

        return trainerRepository.save(trainer);
    }

    public TrainerDto updateTrainerDetails(String trainerEmail, UpdateProfileRequest request) {
        Trainer trainer = getTrainerByEmail(trainerEmail);
        Optional.ofNullable(request.firstName()).filter(s -> !s.isBlank()).ifPresent(trainer::setFirstName);
        Optional.ofNullable(request.lastName()).filter(s -> !s.isBlank()).ifPresent(trainer::setLastName);
        String phone = request.phone();
        trainer.setPhone(phone == null || phone.isBlank() ? null : phone);
        return mapper.toDto(trainerRepository.save(trainer));
    }

    public TrainerDto getTrainerDtoByEmail(String trainerEmail) {
        return mapper.toDto(getTrainerByEmail(trainerEmail));
    }

    public TrainerDto getTrainerDtoById(Long id, String callerEmail) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Trainer not found."));

        boolean isTheTrainer = trainer.getEmail().equals(callerEmail);
        boolean isAssignedCustomer = customerRepository.findCustomerByEmail(callerEmail)
                .map(c -> c.getTrainer() != null && c.getTrainer().getId().equals(id))
                .orElse(false);

        if (!isTheTrainer && !isAssignedCustomer) {
            throw new AccessDeniedException("You do not have permission to view this trainer.");
        }

        return mapper.toDto(trainer);
    }

    public Trainer getTrainerByEmail(String trainerEmail) {
        return trainerRepository.findTrainerByEmail(trainerEmail)
                .orElseThrow(() -> new NoSuchElementException("Trainer with provided email does not exist."));
    }

    public void unlinkCustomerFromTrainer(String trainerEmail, Long customerId) {
        Trainer trainer = getTrainerByEmail(trainerEmail);
        Customer customer = customerService.getCustomerById(customerId);

        if (customer.getTrainer() == null || !customer.getTrainer().getId().equals(trainer.getId())) {
            throw new IllegalArgumentException("Customer is not linked to this trainer.");
        }

        trainingSessionRepository.deleteAll(
                trainingSessionRepository.findAllByCustomer_IdAndTrainer_EmailAndStartDateAfter(
                        customerId, trainerEmail, LocalDateTime.now())
        );
        customer.setTrainer(null);
        customerRepository.save(customer);
    }

    public CustomerDto getCustomerById(String trainerEmail, Long id) {
        Trainer trainer = getTrainerByEmail(trainerEmail);

        return mapper.toDto(trainer.getCustomers()
                .stream()
                .filter(customer -> customer.getId().equals(id)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Customer with provided ID does not exist.")));
    }

}
