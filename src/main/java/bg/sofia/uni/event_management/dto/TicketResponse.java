package bg.sofia.uni.event_management.dto;

import bg.sofia.uni.event_management.model.Ticket;

import java.math.BigDecimal;

public record TicketResponse(
        Long id,
        Long registrationId,
        String price,
        String currency
) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getRegistration().getId(),
                ticket.getPrice().toString(),
                ticket.getCurrency().toString()
        );
    }
}
