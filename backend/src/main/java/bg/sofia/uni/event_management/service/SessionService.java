package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.SessionRequest;
import bg.sofia.uni.event_management.dto.SessionResponse;
import bg.sofia.uni.event_management.exceptions.AccessDeniedException;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.Event;
import bg.sofia.uni.event_management.model.Session;
import bg.sofia.uni.event_management.model.Speaker;
import bg.sofia.uni.event_management.repository.EventRepository;
import bg.sofia.uni.event_management.repository.SessionRepository;
import bg.sofia.uni.event_management.repository.SpeakerRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SpeakerRepository speakerRepository;
    private final EventRepository eventRepository;

    public SessionService(SessionRepository sessionRepository,
                          SpeakerRepository speakerRepository,
                          EventRepository eventRepository) {
        this.sessionRepository = sessionRepository;
        this.speakerRepository = speakerRepository;
        this.eventRepository = eventRepository;
    }

    // ===================== GET =====================

    public SessionResponse getById(Long id) {
        Session session = sessionRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Session not found with id: " + id));

        return SessionResponse.from(session);
    }

    // ===================== GET BY EVENT =====================

    public List<SessionResponse> getByEvent(Long eventId) {

        return sessionRepository.findByEventId(eventId)
            .stream()
            .map(SessionResponse::from)
            .toList();
    }

    // ===================== CREATE =====================

    @Transactional
    public SessionResponse create(Long currentUserId, Long eventId, SessionRequest request) {

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() ->
                new NotFoundException("Event not found with id: " + eventId));

        if (!event.getOrganizer().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Not allowed");
        }

        Session session = new Session();

        session.setEvent(event);
        session.setTitle(request.title());
        session.setDescription(request.description());
        session.setStartTime(request.startTime());
        session.setEndTime(request.endTime());
        session.setOrderIndex(request.orderIndex());
        session.setLocationRoom(request.locationRoom());
        session.setType(request.type());

        // speakers (Many-to-Many)
        if (request.speakerIds() != null && !request.speakerIds().isEmpty()) {

            Set<Speaker> speakers = new HashSet<>(speakerRepository.findAllById(request.speakerIds()));

            if (speakers.size() != request.speakerIds().size()) {
                throw new NotFoundException("Some speakers not found");
            }

            session.setSpeakers(speakers);
        }

        return SessionResponse.from(sessionRepository.save(session));
    }

    // ===================== UPDATE =====================

    @Transactional
    public SessionResponse update(Long currentUserId, Long id, SessionRequest request) {

        Session session = sessionRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Session not found with id: " + id));

        Event event = session.getEvent();

        if (!event.getOrganizer().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Not allowed");
        }

        session.setTitle(request.title());
        session.setDescription(request.description());
        session.setStartTime(request.startTime());
        session.setEndTime(request.endTime());
        session.setOrderIndex(request.orderIndex());
        session.setLocationRoom(request.locationRoom());
        session.setType(request.type());

        // speakers update (replace set)
        if (request.speakerIds() != null) {

            Set<Speaker> speakers = new HashSet<>(speakerRepository.findAllById(request.speakerIds()));

            if (speakers.size() != request.speakerIds().size()) {
                throw new NotFoundException("Some speakers not found");
            }

            session.setSpeakers(speakers);
        }

        return SessionResponse.from(session);
    }

    // ===================== DELETE =====================

    @Transactional
    public void delete(Long currentUserId, Long id) {

        Session session = sessionRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Session not found with id: " + id));

        Event event = session.getEvent();

        if (!event.getOrganizer().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Not allowed");
        }

        // Clear ManyToMany association to avoid FK constraint issues
        session.getSpeakers().clear();

        sessionRepository.delete(session);
    }
}