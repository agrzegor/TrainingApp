package pl.coderslab.trainingapp.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.trainingapp.dto.CustomerDto;
import pl.coderslab.trainingapp.dto.TrainerDto;
import pl.coderslab.trainingapp.dto.api.UpdateProfileRequest;
import pl.coderslab.trainingapp.service.CustomerService;
import pl.coderslab.trainingapp.service.TrainerService;
import pl.coderslab.trainingapp.service.TrainingSessionService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TrainerController {

    private final TrainerService trainerService;
    private final CustomerService customerService;
    private final TrainingSessionService trainingSessionService;


    public TrainerController(TrainerService trainerService, CustomerService customerService, TrainingSessionService trainingSessionService) {
        this.trainerService = trainerService;
        this.customerService = customerService;
        this.trainingSessionService = trainingSessionService;
    }
    @GetMapping("/trainers/me")
    public TrainerDto getMe(@AuthenticationPrincipal(expression = "username") String email) {
        return trainerService.getTrainerDtoByEmail(email);
    }

    @GetMapping("/trainers/{id}")
    public TrainerDto getTrainerById(@AuthenticationPrincipal(expression = "username") String email, @PathVariable Long id) {
        return trainerService.getTrainerDtoById(id, email);
    }

    @PutMapping("/trainers")
    public TrainerDto updateTrainer(@AuthenticationPrincipal(expression = "username") String email,
                                    @RequestBody @Valid UpdateProfileRequest request) {
        return trainerService.updateTrainerDetails(email, request);
    }

    @GetMapping("/trainers/me/customers")
    public List<CustomerDto> getCustomersByTrainer(@AuthenticationPrincipal(expression = "username") String email) {
        return customerService.getAllCustomersByTrainer(email);
    }

    @GetMapping("/trainers/customers/{id}")
    public CustomerDto getCustomerById(@AuthenticationPrincipal(expression = "username") String trainerEmail,
                                       @PathVariable Long id) {
        return trainerService.getCustomerById(trainerEmail,id);
    }
    @DeleteMapping("/trainers/customers/{customerId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void unlinkCustomerFromTrainer (@AuthenticationPrincipal(expression = "username") String trainerEmail,
                                           @PathVariable("customerId") Long customerId){
        trainerService.unlinkCustomerFromTrainer(trainerEmail, customerId);
        trainingSessionService.deleteFutureTrainingSession(trainerEmail, customerId);
    }

}
