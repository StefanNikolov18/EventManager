package bg.sofia.uni.event_management.dto;

import bg.sofia.uni.event_management.model.Session;
import bg.sofia.uni.event_management.model.enums.SessionType;

import java.time.LocalDateTime;
import java.util.List;

public record SessionResponse(

    Long id,

    Long eventId,

    String title,

    String description,

    LocalDateTime startTime,

    LocalDateTime endTime,

    Integer orderIndex,

    String locationRoom,

    SessionType type,

    List<SpeakerResponse> speakers
) {

    public static SessionResponse from(Session session) {
        return new SessionResponse(
            session.getId(),
            session.getEvent().getId(),
            session.getTitle(),
            session.getDescription(),
            session.getStartTime(),
            session.getEndTime(),
            session.getOrderIndex(),
            session.getLocationRoom(),
            session.getType(),
            session.getSpeakers()
                .stream()
                .map(SpeakerResponse::from)
                .toList()
        );
    }
}