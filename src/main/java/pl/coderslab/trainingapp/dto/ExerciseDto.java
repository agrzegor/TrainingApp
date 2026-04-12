package pl.coderslab.trainingapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExerciseDto(
        Long id,
        String name,
        String externalExerciseId,
        String overview,
        List<String> instruction,
        List<String> exerciseTip,
        String videoUrl
) {

}
