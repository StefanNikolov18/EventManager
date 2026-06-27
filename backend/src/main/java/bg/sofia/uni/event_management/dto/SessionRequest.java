package bg.sofia.uni.event_management.dto;

import bg.sofia.uni.event_management.model.enums.SessionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.Set;

public record SessionRequest(

    @NotBlank(message = "Title cannot be empty")
    String title,

    String description,

    @NotNull(message = "Start time is required")
    LocalDateTime startTime,

    @NotNull(message = "End time is required")
    LocalDateTime endTime,

    @NotNull(message = "Order index is required")
    @Positive(message = "Order index must be greater than 0")
    Integer orderIndex,

    String locationRoom,

    @NotNull(message = "Session type is required")
    SessionType type,

    Set<Long> speakerIds
) {
}