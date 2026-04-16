package pl.coderslab.trainingapp.mappers;


import org.springframework.stereotype.Component;
import pl.coderslab.trainingapp.dto.*;
import pl.coderslab.trainingapp.dto.api.GetExerciseDetailsResponse;
import pl.coderslab.trainingapp.entity.*;

@Component
public class Mapper {


    public UserDto toDto(User user) {
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userType(user.getUserType()).build();

    }

    public CustomerDto toDto(Customer customer) {
        Long trainerId = customer.getTrainer() != null ? customer.getTrainer().getId() : null;
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhone(),
                trainerId
        );
    }

    public TrainerDto toDto(Trainer trainer) {
        return TrainerDto.builder()
                .identifier(trainer.getIdentifier())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .phone(trainer.getPhone())
                .build();
    }

    public TrainingSessionDto toDto(TrainingSession trainingSession) {
        Trainer trainer = trainingSession.getTrainer();
        Customer customer = trainingSession.getCustomer();
        return TrainingSessionDto.builder()
                .id(trainingSession.getId())
                .trainerId(trainer != null ? trainer.getId() : null)
                .customerId(customer != null ? customer.getId() : null)
                .customerFirstName(customer != null ? customer.getFirstName() : null)
                .customerLastName(customer != null ? customer.getLastName() : null)
                .createdAt(trainingSession.getCreatedAt())
                .startDate(trainingSession.getStartDate())
                .duration(trainingSession.getDuration())
                .build();
    }

    public SessionExerciseDto toDto(SessionExercise sessionExercise) {
        Exercise exercise = sessionExercise.getExercise();
        return SessionExerciseDto.builder()
                .id(sessionExercise.getId())
                .exerciseName(exercise != null ? exercise.getName() : null)
                .exerciseId(exercise != null ? exercise.getId() : null)
                .trainingSessionId(sessionExercise.getTrainingSession().getId())
                .reps(sessionExercise.getReps())
                .series(sessionExercise.getSeries())
                .weight(sessionExercise.getWeight())
                .build();
    }

    public ExerciseDto toDto(Exercise exercise) {
        return ExerciseDto.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .externalExerciseId(exercise.getExternalExerciseId())
                .build();
    }

    public ExerciseDto toDto(Exercise exercise, GetExerciseDetailsResponse response) {
        return ExerciseDto.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .overview(response.data().overview())
                .instruction(response.data().instructions())
                .exerciseTip(response.data().exerciseTips())
                .videoUrl(response.data().videoUrl())
                .externalExerciseId(exercise.getExternalExerciseId())
                .build();
    }
}
