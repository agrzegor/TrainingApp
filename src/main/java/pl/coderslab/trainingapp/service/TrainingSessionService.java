package pl.coderslab.trainingapp.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.api.CreateTrainingSessionRequest;
import pl.coderslab.trainingapp.dto.TrainingSessionDto;
import pl.coderslab.trainingapp.dto.api.UpdateTrainingSessionRequest;
import pl.coderslab.trainingapp.entity.*;
import pl.coderslab.trainingapp.mappers.Mapper;
import pl.coderslab.trainingapp.repository.TrainerRepository;
import pl.coderslab.trainingapp.repository.TrainingSessionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private TrainerRepository trainerRepository;
    private CustomerService customerService;
    private UserService userService;
    private Mapper mapper;


    public TrainingSessionDto createSession(String trainerEmail, CreateTrainingSessionRequest request) {
        TrainingSession trainingSession = new TrainingSession();

        Trainer trainer = trainerRepository.findTrainersByEmail(trainerEmail).orElseThrow();
        Customer customer = customerService.getCustomerById(request.customerId());
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
                .getTrainingSessionByIdAndTrainer_Email(sessionId, emailTrainer);

        TrainingSession trainingSession = trainingSessionOpt.map(trainingSession1 -> {
            trainingSession1.setStartDate(Optional.ofNullable(updTrainingSessionRequest.startDate())
                    .orElse(trainingSession1.getStartDate()));
            trainingSession1.setDuration(Optional.ofNullable(updTrainingSessionRequest.duration())
                    .orElse(trainingSession1.getDuration()));
            return trainingSession1;
        }).orElseThrow(() -> new NoSuchElementException("No session with provided trainer"));

        return mapper.toDto(trainingSessionRepository.save(trainingSession));

    }


    public TrainingSession getTrainingSessionByIdAndEmail(String email, Long id) {
        return trainingSessionRepository.getTrainingSessionByIdAndTrainer_Email(id, email)
                .orElseThrow(() -> new NoSuchElementException("Training session do not exists"));
    }


    public List<TrainingSessionDto> getSessions(String email) {
        User user = userService.getUser(email);
        if (user.getUserType() == UserType.CUSTOMER) {
            return trainingSessionRepository.getTrainingSessionsByCustomer_Id(user.getId())
                    .stream()
                    .map(mapper::toDto)
                    .toList();
        } else {
            return trainingSessionRepository.getTrainingSessionsByTrainer_Id(user.getId())
                    .stream()
                    .map(mapper::toDto)
                    .toList();

        }
    }
}