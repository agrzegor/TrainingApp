package pl.coderslab.trainingapp.controller;


import org.springframework.web.bind.annotation.*;
import pl.coderslab.trainingapp.dto.CustomerDto;
import pl.coderslab.trainingapp.dto.TrainerDto;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.Session;
import pl.coderslab.trainingapp.service.TrainerService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TrainerController {

    private final TrainerService trainerService;


    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping("/trainers")
    public List<TrainerDto> getAll() {
        return trainerService.readAllTrainers();
    }

    /**
     *
     * @TODO
     */
    @GetMapping("/trainers/me")
    public TrainerDto getLoggedProfile() {
        return null;
    }


    @PutMapping("/trainers/{id}")
    public TrainerDto updateTrainer(@PathVariable("id") Long id,
                                           @RequestBody UserDto userDto) {
        return trainerService.updateTrainerDetails(userDto, id);
    }

    @DeleteMapping("/trainers/{id}")
    public void deleteTreiner(@PathVariable("id") Long id){
        trainerService.deleteTrainerAccount(id);
    }

    /**
     *
     * @TODO
     */
    @GetMapping("/trainers/me/customers")
    public List<CustomerDto> getCustomersByTrainer(){
        return null;
    }

    /**
     *
     * @TODO
     */
    @GetMapping("/trainers/me/session")
    public List<Session> getSessionsByTrainer(){
        return null;
    }
}
