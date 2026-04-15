package pl.coderslab.trainingapp.dto.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateTrainingSessionRequest(
        @NotNull Long customerId,
        @NotNull LocalDateTime startDate,
        @NotNull @Min(1) Integer duration
) {
}
