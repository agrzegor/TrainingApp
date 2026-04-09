package pl.coderslab.trainingapp.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TrainingSessionDto(
        Long trainerId,
        Long customerId,
        LocalDateTime createdAt,
        LocalDateTime startDate,
        int duration) {
}
