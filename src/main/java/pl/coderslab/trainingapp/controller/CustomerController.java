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

    /**
     *
     * @param email
    *
     * Spring:
     * Pobiera Authentication z SecurityContextHolder
     * Wyciąga z niego .getPrincipal()
     * Wstrzykuje to jako parametr metody
     */
    @PostMapping("/customers/{trainerIdentifier}")
    public void addCustomerToTrainer(@AuthenticationPrincipal String email,
                                     @PathVariable("trainerIdentifier") String trainerIdentifier) {
        customerService.addCustomerToTrainer(email, trainerIdentifier);
    }

    @GetMapping("/customers/{id}")
    public CustomerDto getCustomerById(@PathVariable("id") Long id) {
        return customerService.getCustomerById(id);

    }

    @PutMapping("/customers/{id}")
    public CustomerDto updateCustomerById(@PathVariable("id") Long id,
                                       UserDto userDto) {
        return customerService.updateCustomerById(id, userDto);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomerById(@PathVariable("id") Long id) {
        customerService.deleteCustomer(id);
    }

//    @GetMapping("/customers/trainerid={trainerId}")
//    public List<CustomerDto> getAllCustomersByTrainer(@PathVariable("trainerId") Long trainerId){
//        return  customerService.getAllCustomersByTrainer(trainerId);
//    }
    /**
     *
     * @TODO
     */
    @GetMapping("/customers/{id}/session")
    public List<TrainingSession> getAllCustomerSession(@PathVariable("id") Long id) {

        return null;
    }
}
