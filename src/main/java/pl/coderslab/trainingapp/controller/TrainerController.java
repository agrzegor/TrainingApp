package pl.coderslab.trainingapp.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.trainingapp.dto.CustomerDto;
import pl.coderslab.trainingapp.dto.TrainerDto;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.TrainingSession;
import pl.coderslab.trainingapp.service.CustomerService;
import pl.coderslab.trainingapp.service.TrainerService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TrainerController {

    private final TrainerService trainerService;
    private final CustomerService customerService;


    public TrainerController(TrainerService trainerService, CustomerService customerService) {
        this.trainerService = trainerService;
        this.customerService = customerService;
    }

    @GetMapping("/trainers")
    public List<TrainerDto> getAll() {
        return trainerService.readAllTrainers();
    }


    @PutMapping("/trainers/{id}")
    public TrainerDto updateTrainer(@PathVariable("id") Long id,
                                    @RequestBody UserDto userDto) {
        return trainerService.updateTrainerDetails(userDto, id);
    }

    @DeleteMapping("/trainers/{id}")
    public void deleteTreiner(@PathVariable("id") Long id) {
        trainerService.deleteTrainerAccount(id);
    }

    @GetMapping("/trainers/me/customers")
    public List<CustomerDto> getCustomersByTrainer(@AuthenticationPrincipal String email) {
        return customerService.getAllCustomersByTrainer(email);
    }

    /**
     *
     * @TODO
     */
    @GetMapping("/trainers/me/session")
    public List<TrainingSession> getSessionsByTrainer(@AuthenticationPrincipal String email) {

        return List.of();
    }
}
