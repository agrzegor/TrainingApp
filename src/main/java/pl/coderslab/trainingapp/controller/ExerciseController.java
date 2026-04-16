package pl.coderslab.trainingapp.controller;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.trainingapp.dto.ExerciseDto;
import pl.coderslab.trainingapp.service.ExerciseService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
public class ExerciseController {

    private final ExerciseService exerciseService;


    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }


    @GetMapping("/exercises/search")
    public List<ExerciseDto> getExercises(
            @RequestParam("search")
            @NotBlank(message = "Search query must not be blank")
            @Size(max = 100, message = "Search query must not exceed 100 characters")
            String search) {
        return exerciseService.getExercises(search);
    }

    @GetMapping("/exercises/{id}")
    public ExerciseDto getExerciseDetails(@PathVariable("id") Long exerciseId) {
        return exerciseService.getDetailsByExerciseId(exerciseId);
    }


}
