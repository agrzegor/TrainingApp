package pl.coderslab.trainingapp.dto.api;

import java.util.List;

public record SearchExerciseResponse(
        boolean success,
        List<ExternalExercise> data
) {
    public record ExternalExercise(
            String name,
            String exerciseId) {
    }
}
