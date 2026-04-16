package pl.coderslab.trainingapp.dto.api;

import jakarta.validation.constraints.Min;

public record UpdateSessionExercise(
        @Min(1) Integer reps,
        @Min(1) Integer series,
        @Min(0) Integer weight
) {}