package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.TicketResponse;
import bg.sofia.uni.event_management.exceptions.AccessDeniedException;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.Event;
import bg.sofia.uni.event_management.model.Registration;
import bg.sofia.uni.event_management.model.Ticket;
import bg.sofia.uni.event_management.model.User;
import bg.sofia.uni.event_management.model.enums.Currency;
import bg.sofia.uni.event_management.model.enums.Role;
import bg.sofia.uni.event_management.repository.RegistrationRepository;
import bg.sofia.uni.event_management.repository.TicketRepository;
import bg.sofia.uni.event_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository,
                         RegistrationRepository registrationRepository,
                         UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TicketResponse create(Long userId, Long eventId) {
        Registration registration = registrationRepository
                .findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() ->
                        new NotFoundException("No registration found for user " + userId + " on event " + eventId));

        boolean ticketExists = ticketRepository.existsByRegistrationId(registration.getId());
        if (ticketExists) {
            throw new IllegalArgumentException("Ticket already exists for this registration.");
        }

        Ticket ticket = new Ticket();
        ticket.setRegistration(registration);
        ticket.setPrice(BigDecimal.ZERO);
        ticket.setCurrency(Currency.BGN);

        Ticket saved = ticketRepository.save(ticket);

        return TicketResponse.from(saved);
    }

    public TicketResponse getCurrentTicket(Long userId, Long eventId) {
        Registration registration = registrationRepository.findByUserIdAndEventId(userId,eventId)
                .orElseThrow(() ->
                        new NotFoundException("No registration found for user " + userId + " on event " + eventId));


        Ticket ticket = ticketRepository.findByRegistrationId(registration.getId())
                .orElseThrow(() -> new NotFoundException("Ticket does not exist for this event " + eventId));

        return TicketResponse.from(ticket);
    }

    public TicketResponse getTicketById(Long ticketId, Long currentUserId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id " + ticketId));

        Long organizerId = ticket.getRegistration().getEvent().getOrganizer().getId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found with id " + currentUserId));

        boolean isOrganizer = organizerId.equals(currentUserId);
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOrganizer && !isAdmin) {
            throw new AccessDeniedException("Only the event organizer or an admin can view this ticket");
        }

        return TicketResponse.from(ticket);
    }

    @Transactional
    public void deleteTicket(Long eventId, Long ticketId, Long currentUserId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id " + ticketId));

        Long organizerId = ticket.getRegistration().getEvent().getOrganizer().getId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found with id " + currentUserId));

        boolean isOrganizer = organizerId.equals(currentUserId);
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOrganizer && !isAdmin) {
            throw new AccessDeniedException("Only the event organizer or an admin can delete this ticket");
        }

        // restore available tickets on the event
        Event event = ticket.getRegistration().getEvent();
        event.setAvailableTickets(event.getAvailableTickets() + 1);

        // delete ticket first, then registration (avoids Hibernate cascade issues)
        Registration registration = ticket.getRegistration();
        ticketRepository.delete(ticket);
        registrationRepository.delete(registration);
    }

    @Transactional
    public void deleteMyTicket(Long userId, Long eventId) {
        Registration registration = registrationRepository
                .findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() ->
                        new NotFoundException("No registration found for user " + userId + " on event " + eventId));

        Ticket ticket = ticketRepository.findByRegistrationId(registration.getId())
                .orElseThrow(() ->
                        new NotFoundException("No ticket found for user " + userId + " on event " + eventId));

        // restore available tickets on the event
        Event event = registration.getEvent();
        event.setAvailableTickets(event.getAvailableTickets() + 1);

        // delete ticket first, then registration (avoids Hibernate cascade issues)
        ticketRepository.delete(ticket);
        registrationRepository.delete(registration);
    }
}