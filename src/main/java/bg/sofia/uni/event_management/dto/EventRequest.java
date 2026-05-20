package bg.sofia.uni.event_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record EventRequest(

    @NotBlank(message = "Title cannot be empty")
    String title,

    String description,

    @NotBlank(message = "Venue cannot be empty")
    String venue,

    @NotNull(message = "Start time is required")
    LocalDateTime startTime,

    @NotNull(message = "End time is required")
    LocalDateTime endTime,

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be greater than 0")
    Integer capacity,

    @NotNull(message = "Available tickets is required")
    @Min(value = 0, message = "Available tickets cannot be negative")
    Integer availableTickets,

    // IDs of categories associated with this event
    Set<Long> categoryIds
) {
}