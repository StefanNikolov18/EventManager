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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RegistrationService(
        RegistrationRepository registrationRepository,
        EventRepository eventRepository,
        UserRepository userRepository
    ) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    // ===================== GET BY EVENT =====================

    public List<RegistrationResponse> getByEventId(Long eventId) {
        return registrationRepository.findByEventId(eventId)
            .stream()
            .map(RegistrationResponse::from)
            .toList();
    }

    // ===================== GET BY ID =====================

    public RegistrationResponse getById(Long id) {
        Registration reg = registrationRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Registration not found with id: " + id));

        return RegistrationResponse.from(reg);
    }

    // ===================== CREATE =====================

    @Transactional
    public RegistrationResponse create(Long userId, Long eventId) {

        // 1. validate user
        User user = userRepository.findById(userId)
            .orElseThrow(() ->
                new NotFoundException("User not found with id: " + userId));

        // 2. validate event
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() ->
                new NotFoundException("Event not found with id: " + eventId));

        // 3. prevent duplicate registration
        boolean exists = registrationRepository
            .existsByUserIdAndEventId(userId, eventId);

        if (exists) {
            throw new IllegalArgumentException("User already registered for this event.");
        }

        // 4. capacity check
        if (event.getAvailableTickets() <= 0) {
            throw new IllegalArgumentException("No available tickets for this event.");
        }

        // 5. reduce capacity
        event.setAvailableTickets(event.getAvailableTickets() - 1);

        // 6. create registration
        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus(RegistrationStatus.CONFIRMED);

        // 7. generate entry code
        registration.setEntryCode(UUID.randomUUID().toString());

        Registration saved = registrationRepository.save(registration);

        return RegistrationResponse.from(saved);
    }

    // ===================== UPDATE =====================

    @Transactional
    public RegistrationResponse update(Long id) {

        Registration reg = registrationRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Registration not found with id: " + id));

        // пример: toggle status (можеш да го промениш после)
        if (reg.getStatus() == RegistrationStatus.CONFIRMED) {
            reg.setStatus(RegistrationStatus.CANCELLED);
        } else {
            reg.setStatus(RegistrationStatus.CONFIRMED);
        }

        return RegistrationResponse.from(reg);
    }

    // ===================== DELETE =====================

    @Transactional
    public void delete(Long id) {

        Registration reg = registrationRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Registration not found with id: " + id));

        // върни билет обратно
        Event event = reg.getEvent();
        event.setAvailableTickets(event.getAvailableTickets() + 1);

        registrationRepository.delete(reg);
    }
}