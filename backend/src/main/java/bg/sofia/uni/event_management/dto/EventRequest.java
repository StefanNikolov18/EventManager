package bg.sofia.uni.event_management.dto;

import bg.sofia.uni.event_management.model.enums.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
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

    @NotNull(message = "Ticket price is required")
    @DecimalMin(value = "0.0", inclusive = true)
    BigDecimal ticketPrice,

    Currency currency,

    Set<Long> categoryIds
) {
}