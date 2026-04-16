package pl.coderslab.trainingapp.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.api.CreateSessionExercisesRequest;
import pl.coderslab.trainingapp.dto.SessionExerciseDto;
import pl.coderslab.trainingapp.dto.api.UpdateSessionExercise;
import pl.coderslab.trainingapp.entity.*;
import pl.coderslab.trainingapp.mappers.Mapper;
import pl.coderslab.trainingapp.repository.ExerciseRepository;
import pl.coderslab.trainingapp.repository.SessionExerciseRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class SessionExerciseService {

    private final SessionExerciseRepository sessionExerciseRepository;
    private final UserService userService;
    private final TrainingSessionService trainingSessionService;
    private final ExerciseService exerciseService;
    private final Mapper mapper;


    public SessionExerciseDto addExerciseToSession(String trainerEmail, Long trainingSessionId,
                                                   CreateSessionExercisesRequest request) {
        TrainingSession trainingSession = trainingSessionService
                .getTrainingSessionByIdAndEmail(trainerEmail, trainingSessionId);

        Exercise exercise = exerciseService.getExerciseById(request.exerciseId());

        SessionExercise sessionExercise = new SessionExercise();
        sessionExercise.setExercise(exercise);
        sessionExercise.setTrainingSession(trainingSession);
        sessionExercise.setReps(request.reps());
        sessionExercise.setWeight(request.weight());
        sessionExercise.setSeries(request.series());

       sessionExercise= sessionExerciseRepository.save(sessionExercise);
        return mapper.toDto(sessionExercise);

    }

    public List<SessionExerciseDto> getSessionExercises(String email, Long id){
            User user = userService.getUser(email);
            if (user.getUserType() == UserType.CUSTOMER) {
                return sessionExerciseRepository
                        .getSessionExercisesByTrainingSession_IdAndTrainingSessionCustomer_Id(id, user.getId())
                        .stream()
                        .map(mapper::toDto).toList();
            } else {
                return sessionExerciseRepository
                        .getSessionExercisesByTrainingSession_IdAndTrainingSession_Trainer_Id(id, user.getId())
                        .stream()
                        .map(mapper::toDto)
                        .toList();
            }
        }


    public void removeExerciseFromSession(String trainerEmail, Long sessionId, Long exerciseId) {
        TrainingSession trainingSession = trainingSessionService
                .getTrainingSessionByIdAndEmail(trainerEmail, sessionId);

        SessionExercise sessionExercise = sessionExerciseRepository
                .findSessionExerciseByTrainingSession_IdAndExercise_Id(trainingSession.getId(), exerciseId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Exercise not found in this session"));

        sessionExerciseRepository.deleteById(sessionExercise.getId());
    }

    public SessionExerciseDto updateExercise(String trainerEmail, Long sessionId, Long exerciseId,  UpdateSessionExercise request) {

        trainingSessionService.getTrainingSessionByIdAndEmail(trainerEmail, sessionId);

        SessionExercise exercise = sessionExerciseRepository
                .findByIdAndTrainingSession_Id(exerciseId, sessionId)
                .orElseThrow(() -> new NoSuchElementException("Exercise not found in this session"));

        Optional.ofNullable(request.reps()).ifPresent(exercise::setReps);
        Optional.ofNullable(request.series()).ifPresent(exercise::setSeries);
        Optional.ofNullable(request.weight()).ifPresent(exercise::setWeight);

        return mapper.toDto(sessionExerciseRepository.save(exercise));

    }
}


