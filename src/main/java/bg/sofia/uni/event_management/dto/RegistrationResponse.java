package bg.sofia.uni.event_management.dto;

import bg.sofia.uni.event_management.model.Registration;
import bg.sofia.uni.event_management.model.enums.RegistrationStatus;

import java.time.LocalDateTime;

public record RegistrationResponse(

    Long id,
    Long eventId,
    Long userId,
    RegistrationStatus status,
    LocalDateTime registrationDate,
    String entryCode
) {

    public static RegistrationResponse from(Registration registration) {
        return new RegistrationResponse(
            registration.getId(),
            registration.getEvent().getId(),
            registration.getUser().getId(),
            registration.getStatus(),
            registration.getRegistrationDate(),
            registration.getEntryCode()
        );
    }
}