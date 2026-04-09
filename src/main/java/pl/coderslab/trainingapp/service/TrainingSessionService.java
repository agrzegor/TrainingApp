package pl.coderslab.trainingapp.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.CreateTrainingSessionRequest;
import pl.coderslab.trainingapp.dto.TrainingSessionDto;
import pl.coderslab.trainingapp.dto.UpdateTrainingSessionRequest;
import pl.coderslab.trainingapp.entity.Customer;
import pl.coderslab.trainingapp.entity.Trainer;
import pl.coderslab.trainingapp.entity.TrainingSession;
import pl.coderslab.trainingapp.mappers.Mapper;
import pl.coderslab.trainingapp.repository.CustomerRepository;
import pl.coderslab.trainingapp.repository.SessionExerciseRepository;
import pl.coderslab.trainingapp.repository.TrainerRepository;
import pl.coderslab.trainingapp.repository.TrainingSessionRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Setter
@Getter
@Builder
@AllArgsConstructor
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private TrainerRepository trainerRepository;
    private CustomerRepository customerRepository;
    private SessionExerciseRepository sessionExerciseRepository;
    private Mapper mapper;


    public TrainingSessionDto createSession(String trainerEmail, CreateTrainingSessionRequest request) {
        TrainingSession trainingSession = new TrainingSession();

        Trainer trainer = trainerRepository.findTrainersByEmail(trainerEmail).orElseThrow();
        Customer customer = customerRepository.findCustomerById(request.customerId()).orElseThrow();
        LocalDateTime createdAt = LocalDateTime.now();

        trainingSession.setTrainer(trainer);
        trainingSession.setCustomer(customer);
        trainingSession.setCreatedAt(createdAt);
        trainingSession.setStartDate(request.startDate());
        trainingSession.setDuration(request.duration());

        TrainingSession saveTrainingSession = trainingSessionRepository.save(trainingSession);
        return mapper.toDto(saveTrainingSession);
    }

    public TrainingSessionDto updateSession(String emailTrainer,
                                            UpdateTrainingSessionRequest updTrainingSessionRequest,
                                            Long sessionId) {

       Optional<TrainingSession> trainingSessionOpt = trainingSessionRepository
                .getTrainingSessionByIdAndTrainer_Email(sessionId,emailTrainer);

        TrainingSession trainingSession = trainingSessionOpt.map(trainingSession1 -> {
                    trainingSession1.setStartDate(Optional.ofNullable(updTrainingSessionRequest.startDate())
                            .orElse(trainingSession1.getStartDate()));
                    trainingSession1.setDuration(Optional.ofNullable(updTrainingSessionRequest.duration())
                            .orElse(trainingSession1.getDuration()));
                    return trainingSession1;
                }).orElseThrow(() -> new NoSuchElementException("No session with provided trainer"));

        return mapper.toDto(trainingSessionRepository.save(trainingSession));

    }

}
