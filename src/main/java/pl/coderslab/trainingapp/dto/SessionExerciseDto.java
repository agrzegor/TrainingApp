package pl.coderslab.trainingapp.dto;

import lombok.Builder;

@Builder
public record SessionExerciseDto(
        Long trainingSessionId,
        Long exerciseId,
        Integer reps,
        Integer sets,
        Integer weight

) {
}
