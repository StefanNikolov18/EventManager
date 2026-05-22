package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.EventRequest;
import bg.sofia.uni.event_management.dto.EventResponse;
import bg.sofia.uni.event_management.exceptions.AccessDeniedException;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.Event;
import bg.sofia.uni.event_management.model.User;
import bg.sofia.uni.event_management.model.enums.Role;
import bg.sofia.uni.event_management.repository.EventRepository;
import bg.sofia.uni.event_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
            .stream()
            .map(EventResponse::from)
            .toList();
    }

    public List<EventResponse> getEvents(
        String title,
        String venue,
        Long organizerId,
        Long categoryId
    ) {
        return eventRepository.findAll()
            .stream()
            .filter(e -> title == null || e.getTitle().toLowerCase().contains(title.toLowerCase()))
            .filter(e -> venue == null || e.getVenue().equalsIgnoreCase(venue))
            .filter(e -> organizerId == null || e.getOrganizer().getId().equals(organizerId))
            .filter(e -> categoryId == null ||
                e.getCategories().stream().anyMatch(c -> c.getId().equals(categoryId)))
            .map(EventResponse::from)
            .toList();
    }

    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Event is missing with id: " + id));

        return EventResponse.from(event);
    }

    public EventResponse createEvent(Long organizerId, EventRequest request) {
        User organizer = userRepository.findById(organizerId)
            .orElseThrow(() -> new NotFoundException("User is missing with id: " + organizerId));

        Event event = new Event();

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setVenue(request.venue());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setCapacity(request.capacity());
        event.setAvailableTickets(request.capacity());

        event.setOrganizer(organizer); // temporary placeholder

        Event saved = eventRepository.save(event);

        return EventResponse.from(saved);
    }

    @Transactional
    public void updateEvent(Long currentUserId, Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Event is missing with id: " + id));

        if (!event.getOrganizer().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to update this event.");
        }

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setVenue(request.venue());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setCapacity(request.capacity());

        // Optional: adjust available tickets if needed
        if (event.getAvailableTickets() > request.capacity()) {
            event.setAvailableTickets(request.capacity());
        }
        // не нужно заради Transactional
        // eventRepository.save(event);
    }

    public void deleteEvent(Long currentUserId, Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() ->
                new NotFoundException("Event is missing with id: " + eventId));

        User currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new NotFoundException("User is missing with id: " + currentUserId));

        boolean isOrganizer = event.getOrganizer().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOrganizer && !isAdmin) {
            throw new AccessDeniedException("You are not allowed to delete this event.");
        }

        eventRepository.delete(event);
    }
}
