package pl.coderslab.trainingapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;


public record TrainerDto(
        Long id,
        String firstName,
        String lastName,
      String phone
      //  List<CustomerDto> customerDtoList
) {
}
