package pl.coderslab.trainingapp.service;

import jakarta.transaction.Transactional;
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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final CustomerService customerService;
    private final UserService userService;
    private final TrainerService trainerService;
    private final Mapper mapper;


    public TrainingSessionDto createSession(String trainerEmail, CreateTrainingSessionRequest request) {
        TrainingSession trainingSession = new TrainingSession();

        Trainer trainer = trainerService.getTrainerByEmail(trainerEmail);
        Customer customer = customerService.getCustomerById(request.customerId());

        if (customer.getTrainer() == null || !customer.getTrainer().getId().equals(trainer.getId())) {
            throw new IllegalArgumentException("Customer is not assigned to this trainer.");
        }

        LocalDateTime createdAt = LocalDateTime.now();

        trainingSession.setTrainer(trainer);
        trainingSession.setCustomer(customer);
        trainingSession.setCreatedAt(createdAt);


        validateDate(trainer.getId(), request.startDate(), request.duration(), null);

        trainingSession.setStartDate(request.startDate());
        trainingSession.setDuration(request.duration());

        TrainingSession saveTrainingSession = trainingSessionRepository.save(trainingSession);
        return mapper.toDto(saveTrainingSession);
    }

    private void validateDate(Long trainerId, LocalDateTime startDate, int duration, Long excludeSessionId) {
        List<TrainingSession> trainingSessions = trainingSessionRepository
                .findOverlappingSessions(trainerId, startDate, duration, excludeSessionId);

        if(!trainingSessions.isEmpty()){
            throw new IllegalArgumentException("Provided data range overlaps with existing sessions");
        }
    }

    public TrainingSessionDto updateSession(String emailTrainer,
                                            UpdateTrainingSessionRequest updTrainingSessionRequest,
                                            Long sessionId) {

        Optional<TrainingSession> trainingSessionOpt = trainingSessionRepository
                .getTrainingSessionByIdAndTrainer_Email(sessionId, emailTrainer);

        TrainingSession trainingSession = trainingSessionOpt
                .orElseThrow(() -> new NoSuchElementException("No session with provided trainer"));

        LocalDateTime newStartDate = Optional.ofNullable(updTrainingSessionRequest.startDate())
                .orElse(trainingSession.getStartDate());
        int newDuration = Optional.ofNullable(updTrainingSessionRequest.duration())
                .orElse(trainingSession.getDuration());

        validateDate(trainingSession.getTrainer().getId(), newStartDate, newDuration, sessionId);

        trainingSession.setStartDate(newStartDate);
        trainingSession.setDuration(newDuration);

        return mapper.toDto(trainingSessionRepository.save(trainingSession));

    }

    public TrainingSession getTrainingSessionByIdAndEmail(String email, Long id) {
        return trainingSessionRepository.getTrainingSessionByIdAndTrainer_Email(id, email)
                .orElseThrow(() -> new NoSuchElementException("Training session does not exist."));
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

    public void deleteFutureTrainingSession(String trainerEmail, Long customerId) {
        List<TrainingSession> futureSessions = trainingSessionRepository
                .findAllByCustomer_IdAndTrainer_EmailAndStartDateAfter(customerId, trainerEmail, LocalDateTime.now());
        trainingSessionRepository.deleteAll(futureSessions);
    }

    public void deleteSession(String trainerEmail, Long sessionId) {
        TrainingSession session = trainingSessionRepository
                .getTrainingSessionByIdAndTrainer_Email(sessionId, trainerEmail)
                .orElseThrow(() -> new NoSuchElementException("Training session does not exist."));
        if (!session.getStartDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Only future sessions can be deleted.");
        }
        trainingSessionRepository.delete(session);
    }

}