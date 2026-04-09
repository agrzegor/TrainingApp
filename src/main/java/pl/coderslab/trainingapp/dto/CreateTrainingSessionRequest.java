package pl.coderslab.trainingapp.dto;

import java.time.LocalDateTime;

public record CreateTrainingSessionRequest(
        Long customerId,
        LocalDateTime startDate,
        Integer duration
) {
}
