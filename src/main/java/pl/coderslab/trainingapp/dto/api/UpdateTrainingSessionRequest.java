package pl.coderslab.trainingapp.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record UpdateTrainingSessionRequest(
        LocalDateTime startDate,
        @Min(1) Integer duration
) {
    @JsonIgnore
    @AssertTrue(message = "At least one field (startDate or duration) must be provided")
    public boolean isAtLeastOneFieldPresent() {
        return startDate != null || duration != null;
    }
}
