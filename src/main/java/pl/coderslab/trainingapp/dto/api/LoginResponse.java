package pl.coderslab.trainingapp.dto.api;

public record LoginResponse(
        String token,
        Long expiresIn
) {

}
