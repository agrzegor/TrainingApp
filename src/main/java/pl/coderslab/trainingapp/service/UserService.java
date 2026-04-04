package pl.coderslab.trainingapp.service;


import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.User;
import pl.coderslab.trainingapp.mappers.Mapper;

@Service
@AllArgsConstructor
public class UserService {

    private final TrainerService trainerService;
    private final CustomerService customerService;
    private  Mapper mapper;

    public UserDto createUser(final UserDto userDto) {

       User createdUser =  switch (userDto.userType()) {
            case TRAINER -> trainerService.createTrainer(userDto);
            case CUSTOMER -> customerService.createCustomer(userDto);
            default -> throw new IllegalArgumentException("Invalid input");
        };

       return mapper.toDto(createdUser);
    }

//    public UserDto loginUser(final  UserDto userDto){
//
//
//    }

}
