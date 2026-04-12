package pl.coderslab.trainingapp.dto.api;

import java.time.LocalDateTime;

public record UpdateTrainingSessionRequest(
        LocalDateTime startDate,
        Integer duration
) {
}