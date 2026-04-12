package pl.coderslab.trainingapp.controller;


import org.springframework.web.bind.annotation.*;
import pl.coderslab.trainingapp.dto.ExerciseDto;
import pl.coderslab.trainingapp.service.ExerciseService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExerciseController {

    private final ExerciseService exerciseService;


    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }


    @GetMapping("/exercises/search")
    public List<ExerciseDto> getExercises(@RequestParam("search") String search) {
        return exerciseService.getExercises(search);
    }

    @GetMapping("/exercises/{id}")
    public ExerciseDto getExerciseDetails(@PathVariable("id") Long exerciseId) {
        return exerciseService.getDetailsByExerciseId(exerciseId);
    }


}
