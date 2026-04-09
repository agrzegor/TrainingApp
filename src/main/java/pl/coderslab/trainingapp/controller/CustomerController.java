package pl.coderslab.trainingapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.trainingapp.dto.CustomerDto;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.TrainingSession;
import pl.coderslab.trainingapp.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PostMapping("/customers/{trainerIdentifier}")
    public void addCustomerToTrainer(@AuthenticationPrincipal String email,
                                     @PathVariable("trainerIdentifier") String trainerIdentifier) {
        customerService.addCustomerToTrainer(email, trainerIdentifier);
    }

    @GetMapping("/customers/me")
    public CustomerDto getCurrentCustomer(@AuthenticationPrincipal String email) {
        return customerService.getCustomerDtoByEmail(email);
    }

    @PutMapping("/customers")
    public CustomerDto updateCustomerById(@AuthenticationPrincipal String email,
                                      @RequestBody UserDto userDto) {
        return customerService.updateCustomer(email, userDto);
    }


    /*
     * @TODO
     */
    @GetMapping("/customers/session")
    public List<TrainingSession> getAllCustomerSession(@AuthenticationPrincipal String email) {

        return null;
    }
}
