package pl.coderslab.trainingapp.dto;


import pl.coderslab.trainingapp.entity.UserType;


public record UserDto(
        String firstName,
        String lastName,
        String email,
        String phone,
        UserType userType
) {
}
