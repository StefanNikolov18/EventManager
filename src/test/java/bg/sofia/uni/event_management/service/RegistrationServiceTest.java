package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.RegistrationResponse;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.Event;
import bg.sofia.uni.event_management.model.Registration;
import bg.sofia.uni.event_management.model.User;
import bg.sofia.uni.event_management.model.enums.RegistrationStatus;
import bg.sofia.uni.event_management.repository.EventRepository;
import bg.sofia.uni.event_management.repository.RegistrationRepository;
import bg.sofia.uni.event_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private User user;
    private Event event;
    private Registration registration;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        event = new Event();
        event.setId(1L);
        event.setAvailableTickets(10);

        registration = new Registration();
        registration.setId(1L);
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus(RegistrationStatus.CONFIRMED);
    }

    // ===================== getByEventId =====================

    @Test
    void testGetByEventIdReturnsCorrectSize() {
        when(registrationRepository.findByEventId(1L))
            .thenReturn(List.of(registration));

        List<RegistrationResponse> result = registrationService.getByEventId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetByEventIdReturnsEmptyList() {
        when(registrationRepository.findByEventId(1L))
            .thenReturn(List.of());

        assertThat(registrationService.getByEventId(1L)).isEmpty();
    }

    // ===================== getById =====================

    @Test
    void testGetByIdReturnsCorrectRegistration() {
        when(registrationRepository.findById(1L))
            .thenReturn(Optional.of(registration));

        RegistrationResponse result = registrationService.getById(1L);

        assertEquals(RegistrationResponse.from(registration), result);
    }

    @Test
    void testGetByIdThrowsNotFoundException() {
        when(registrationRepository.findById(any()))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> registrationService.getById(1L));
    }

    // ===================== create =====================

    @Test
    void testCreateThrowsWhenUserNotFound() {
        when(userRepository.findById(any()))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> registrationService.create(1L, 1L));
    }

    @Test
    void testCreateThrowsWhenEventNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> registrationService.create(1L, 1L));
    }

    @Test
    void testCreateThrowsWhenAlreadyRegistered() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.existsByUserIdAndEventId(1L, 1L))
            .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
            () -> registrationService.create(1L, 1L));
    }

    @Test
    void testCreateThrowsWhenNoTickets() {
        event.setAvailableTickets(0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.existsByUserIdAndEventId(1L, 1L))
            .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> registrationService.create(1L, 1L));
    }

    @Test
    void testCreateSuccessDecreasesTickets() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.existsByUserIdAndEventId(1L, 1L))
            .thenReturn(false);
        when(registrationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        registrationService.create(1L, 1L);

        assertEquals(9, event.getAvailableTickets());
    }

    // ===================== update =====================

    @Test
    void testUpdateTogglesStatusConfirmedToCancelled() {
        when(registrationRepository.findById(1L))
            .thenReturn(Optional.of(registration));

        registrationService.update(1L);

        assertEquals(RegistrationStatus.CANCELLED, registration.getStatus());
    }

    @Test
    void testUpdateThrowsNotFound() {
        when(registrationRepository.findById(any()))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> registrationService.update(1L));
    }

    // ===================== delete =====================

    @Test
    void testDeleteIncreasesTicketsAndDeletes() {
        when(registrationRepository.findById(1L))
            .thenReturn(Optional.of(registration));

        registrationService.delete(1L);

        assertEquals(11, event.getAvailableTickets());
        verify(registrationRepository).delete(registration);
    }

    @Test
    void testDeleteThrowsNotFound() {
        when(registrationRepository.findById(any()))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> registrationService.delete(1L));
    }
}