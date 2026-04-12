package pl.coderslab.trainingapp.dto.api;

import java.util.List;


public record GetExerciseDetailsResponse(
        boolean success,
        ExternalDetails data
) {
    public record ExternalDetails(String overview,
                                  List<String> instructions,
                                  List<String> exerciseTips,
                                  String videoUrl) {
    }
}
