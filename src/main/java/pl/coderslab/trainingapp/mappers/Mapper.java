package pl.coderslab.trainingapp.mappers;


import org.springframework.stereotype.Component;
import pl.coderslab.trainingapp.dto.UserDto;
import pl.coderslab.trainingapp.entity.User;

@Component
public class Mapper {


    public UserDto toDto(User user){
        return new UserDto(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getUserType()
        );
    }
}
