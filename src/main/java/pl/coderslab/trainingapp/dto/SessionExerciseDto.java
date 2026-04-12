package pl.coderslab.trainingapp.dto;

import lombok.Builder;

@Builder
public record SessionExerciseDto(
        Long id,
        Long trainingSessionId,
        String exerciseName,
        Long exerciseId,
        Integer reps,
        Integer series,
        Integer weight

) {
}
