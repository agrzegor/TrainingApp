package pl.coderslab.trainingapp.mappers;


import org.springframework.stereotype.Component;
import pl.coderslab.trainingapp.dto.*;
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
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhone()
        );
    }

    public TrainerDto toDto(Trainer trainer) {
        return new TrainerDto(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getPhone()
        );
    }

    public TrainingSessionDto toDto(TrainingSession trainingSession) {
        return TrainingSessionDto.builder()
                .trainerId(trainingSession.getTrainer().getId())
                .customerId(trainingSession.getCustomer().getId())
                .createdAt(trainingSession.getCreatedAt())
                .startDate(trainingSession.getStartDate())
                .duration(trainingSession.getDuration())
                .build();

    }

    public SessionExerciseDto toDto(SessionExercise  sessionExercise){
        return SessionExerciseDto.builder()
//                .trainingSessionId(sessionExercise.getTrainingSession().getId())
//                .exerciseId(sessionExercise.getTrainingSession().getId())
//                .reps(sessionExercise.getReps())
//                .sets(sessionExercise.getSets())
//                .weight(sessionExercise.getWeight())
                .build();
    }
}
