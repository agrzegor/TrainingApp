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


    @PutMapping("/trainers")
    public TrainerDto updateTrainer(@AuthenticationPrincipal String email,
                                    @RequestBody UserDto userDto) {
        return trainerService.updateTrainerDetails(email,userDto);
    }

    @GetMapping("/trainers/me/customers")
    public List<CustomerDto> getCustomersByTrainer(@AuthenticationPrincipal String email) {
        return customerService.getAllCustomersByTrainer(email);
    }

    @GetMapping("/trainers/customers/{id}")
    public CustomerDto getCustomerById(@AuthenticationPrincipal String trainerEmail,
                                       @PathVariable Long id) {
        return trainerService.getCustomerById(trainerEmail,id);
    }
    @DeleteMapping("/trainers/customers/{customerId}")
    public void unlinkCustomerFromTrainer (@AuthenticationPrincipal String trainerEmail,
                                           @PathVariable("customerId") Long customerId){
        trainerService.unlinkCustomerFromTrainer(trainerEmail,customerId);
    }


}
