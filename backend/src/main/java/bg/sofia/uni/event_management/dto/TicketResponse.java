package bg.sofia.uni.event_management.dto;

import bg.sofia.uni.event_management.model.Ticket;
import bg.sofia.uni.event_management.model.enums.Currency;

import java.math.BigDecimal;

public record TicketResponse(
        Long id,
        Long registrationId,
        BigDecimal price,
        Currency currency
) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getRegistration().getId(),
                ticket.getPrice(),
                ticket.getCurrency()
        );
    }
}
