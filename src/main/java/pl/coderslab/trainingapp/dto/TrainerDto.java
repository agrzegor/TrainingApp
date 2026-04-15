package pl.coderslab.trainingapp.dto;

import lombok.Builder;

@Builder
public record TrainerDto(
        String identifier,
        String firstName,
        String lastName,
      String phone

) {
}
