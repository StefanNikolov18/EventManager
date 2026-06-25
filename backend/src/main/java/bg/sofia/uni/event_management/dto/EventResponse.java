package bg.sofia.uni.event_management.dto;

import bg.sofia.uni.event_management.model.Event;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record EventResponse(
    Long id,
    Long organizerId,
    String title,
    String description,
    String venue,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer capacity,
    Integer availableTickets,
    Set<String> categories
) {

    public static EventResponse from(Event event) {
        return new EventResponse(
            event.getId(),
            event.getOrganizer().getId(),
            event.getTitle(),
            event.getDescription(),
            event.getVenue(),
            event.getStartTime(),
            event.getEndTime(),
            event.getCapacity(),
            event.getAvailableTickets(),
            event.getCategories()
                .stream()
                .map(category -> category.getCategoryName())
                .collect(Collectors.toSet())
        );
    }
}
