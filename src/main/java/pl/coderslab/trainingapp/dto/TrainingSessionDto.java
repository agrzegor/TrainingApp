package pl.coderslab.trainingapp.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TrainingSessionDto(
        Long id,
        Long trainerId,
        Long customerId,
        String customerFirstName,
        String customerLastName,
        LocalDateTime createdAt,
        LocalDateTime startDate,
        int duration) {
}
