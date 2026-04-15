package pl.coderslab.trainingapp.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {
}
