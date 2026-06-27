package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.EventRequest;
import bg.sofia.uni.event_management.dto.EventResponse;
import bg.sofia.uni.event_management.exceptions.AccessDeniedException;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.Event;
import bg.sofia.uni.event_management.model.User;
import bg.sofia.uni.event_management.model.enums.Role;
import bg.sofia.uni.event_management.repository.CategoryRepository;
import bg.sofia.uni.event_management.repository.EventRepository;
import bg.sofia.uni.event_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private EventService eventService;

    private User organizer;
    private User admin;
    private Event event;

    @BeforeEach
    void setUp() {

        organizer = new User();
        organizer.setId(1L);
        organizer.setRole(Role.USER);

        admin = new User();
        admin.setId(2L);
        admin.setRole(Role.ADMIN);

        event = new Event();
        event.setId(10L);
        event.setTitle("Test Event");
        event.setDescription("Desc");
        event.setVenue("Sofia");
        event.setCapacity(100);
        event.setAvailableTickets(100);
        event.setOrganizer(organizer);
    }

    private EventRequest createRequest() {
        return new EventRequest(
            "Title",
            "Desc",
            "Sofia",
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(2),
            100,
            100,
            Set.of()
        );
    }


    // ===================== getEventById =====================

    @Test
    void testGetEventByIdReturnsCorrectEvent() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        EventResponse result = eventService.getEventById(10L);

        assertEquals("Test Event", result.title());
    }

    @Test
    void testGetEventByIdThrowsNotFoundException() {
        when(eventRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> eventService.getEventById(1L));
    }

    // ===================== createEvent =====================

    @Test
    void testCreateEventSuccess() {
        EventRequest req = createRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventResponse result = eventService.createEvent(1L, req);

        assertEquals("Test Event", result.title());
    }

    @Test
    void testCreateEventUserNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> eventService.createEvent(1L, createRequest()));
    }

    // ===================== updateEvent =====================

    @Test
    void testUpdateEventSuccess() {
        EventRequest req = createRequest();

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        eventService.updateEvent(1L, 10L, req);

        assertEquals("Title", event.getTitle());
        assertEquals("Sofia", event.getVenue());
    }

    @Test
    void testUpdateEventNotOrganizerThrowsAccessDenied() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(AccessDeniedException.class,
            () -> eventService.updateEvent(999L, 10L, createRequest()));
    }

    @Test
    void testUpdateEventNotFound() {
        when(eventRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> eventService.updateEvent(1L, 10L, createRequest()));
    }

    // ===================== deleteEvent =====================

    @Test
    void testDeleteEventByOrganizerSuccess() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(organizer));

        eventService.deleteEvent(1L, 10L);

        verify(eventRepository).delete(event);
    }

    @Test
    void testDeleteEventByAdminSuccess() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        eventService.deleteEvent(2L, 10L);

        verify(eventRepository).delete(event);
    }

    @Test
    void testDeleteEventAccessDenied() {
        User randomUser = new User();
        randomUser.setId(3L);
        randomUser.setRole(Role.USER);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(3L)).thenReturn(Optional.of(randomUser));

        assertThrows(AccessDeniedException.class,
            () -> eventService.deleteEvent(3L, 10L));
    }
}
