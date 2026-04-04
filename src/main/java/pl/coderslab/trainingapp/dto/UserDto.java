package pl.coderslab.trainingapp.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import pl.coderslab.trainingapp.entity.UserType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserDto(
        String firstName,
        String lastName,
        String email,
        String phone,
        UserType userType,
        String password
) {
}
