package pl.coderslab.trainingapp.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.trainingapp.dto.CustomerDto;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.service.CustomerService;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PostMapping("/customers/{trainerIdentifier}")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void addCustomerToTrainer(@AuthenticationPrincipal String email,
                                     @PathVariable("trainerIdentifier") String trainerIdentifier) {
        customerService.addCustomerToTrainer(email, trainerIdentifier);
    }

    @GetMapping("/customers/me")
    public CustomerDto getCurrentCustomer(@AuthenticationPrincipal String email) {
        return customerService.getCustomerDtoByEmail(email);
    }

    @PutMapping("/customers")
    public CustomerDto updateCustomerById( @AuthenticationPrincipal String email,
                                     @Valid @RequestBody UserDto userDto) {
        return customerService.updateCustomer(email, userDto);
    }

    @DeleteMapping("/customers/me/trainer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainer(@AuthenticationPrincipal String customerEmail) {
        customerService.selfUnlinkFromTrainer(customerEmail);
    }

}
