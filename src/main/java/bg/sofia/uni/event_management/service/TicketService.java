package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.TicketResponse;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.Registration;
import bg.sofia.uni.event_management.model.Ticket;
import bg.sofia.uni.event_management.model.enums.Currency;
import bg.sofia.uni.event_management.repository.RegistrationRepository;
import bg.sofia.uni.event_management.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RegistrationRepository registrationRepository;

    public TicketService(TicketRepository ticketRepository,
                         RegistrationRepository registrationRepository) {
        this.ticketRepository = ticketRepository;
        this.registrationRepository = registrationRepository;
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
}