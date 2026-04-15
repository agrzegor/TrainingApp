package pl.coderslab.trainingapp.dto.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateSessionExercisesRequest(
        @NotNull Long exerciseId,
        @NotNull @Min(1) Integer reps,
        @NotNull @Min(1) Integer series,
        @NotNull @Min(0) Integer weight
) {

}
