package bg.sofia.uni.event_management.dto;

import jakarta.validation.constraints.NotNull;
import bg.sofia.uni.event_management.model.enums.RegistrationStatus;

public record RegistrationRequest(

    @NotNull(message = "Status is required")
    RegistrationStatus status
) {
}