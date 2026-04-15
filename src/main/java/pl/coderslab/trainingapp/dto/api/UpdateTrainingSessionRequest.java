package pl.coderslab.trainingapp.dto.api;

import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record UpdateTrainingSessionRequest(
        LocalDateTime startDate,
        @Min(1) Integer duration
) {
}
