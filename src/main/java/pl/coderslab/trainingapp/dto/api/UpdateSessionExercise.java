package pl.coderslab.trainingapp.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;

public record UpdateSessionExercise(
        @Min(1) Integer reps,
        @Min(1) Integer series,
        @Min(0) Integer weight
) {
    @JsonIgnore
    @AssertTrue(message = "At least one field (reps, series, or weight) must be provided")
    public boolean isAtLeastOneFieldPresent() {
        return reps != null || series != null || weight != null;
    }
}