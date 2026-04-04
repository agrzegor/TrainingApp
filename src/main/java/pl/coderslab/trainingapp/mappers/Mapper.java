package pl.coderslab.trainingapp.mappers;


import org.springframework.stereotype.Component;
import pl.coderslab.trainingapp.dto.CustomerDto;
import pl.coderslab.trainingapp.dto.TrainerDto;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.Customer;
import pl.coderslab.trainingapp.entity.Trainer;
import pl.coderslab.trainingapp.entity.User;

import java.util.List;

@Component
public class Mapper {


    public UserDto toDto(User user){
       return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userType(user.getUserType()).build();

    }

    public CustomerDto toDto(Customer customer){
        return new CustomerDto(
                customer.getFirstName(),
                customer.getLastName()
        );
    }

    public TrainerDto toDto(Trainer trainer){
        return new TrainerDto(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName()
        );
    }
}
