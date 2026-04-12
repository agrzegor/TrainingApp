package pl.coderslab.trainingapp.dto.api;

public record CreateSessionExercisesRequest(
        Long exerciseId,
        Integer reps,
        Integer series,
        Integer weight
) {

}
