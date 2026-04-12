package pl.coderslab.trainingapp.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.api.CreateSessionExercisesRequest;
import pl.coderslab.trainingapp.dto.SessionExerciseDto;
import pl.coderslab.trainingapp.entity.*;
import pl.coderslab.trainingapp.mappers.Mapper;
import pl.coderslab.trainingapp.repository.SessionExerciseRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class SessionExerciseService {

    private final SessionExerciseRepository sessionExerciseRepository;
    private final UserService userService;
    private final TrainingSessionService trainingSessionService;
    private final ExerciseService exerciseService;
    private Mapper mapper;


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
    }


