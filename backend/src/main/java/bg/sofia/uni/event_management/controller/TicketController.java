package bg.sofia.uni.event_management.controller;

import bg.sofia.uni.event_management.dto.TicketResponse;
import bg.sofia.uni.event_management.dto.UserResponse;
import bg.sofia.uni.event_management.security.SecurityUtil;
import bg.sofia.uni.event_management.service.TicketService;
import bg.sofia.uni.event_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    // ===================== MY TICKETS =====================

    @GetMapping("/tickets/me")
    @Operation(summary = "Get current user's tickets")
    @ApiResponse(responseCode = "200", description = "List of tickets")
    public List<TicketResponse> getMyTickets() {
        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();
        return ticketService.getTicketsByUserId(currentUserId);
    }

    // ===================== PUBLIC ENDPOINTS =====================
    @PostMapping("/events/{eventId}/tickets")
    @Operation(summary = "Create Ticket for event")
    @ApiResponse(responseCode = "201", description = "Ticket created")
    public ResponseEntity<TicketResponse> createTicket(@PathVariable Long eventId) {

        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();

        return ResponseEntity.status(201).body(ticketService.create(currentUserId, eventId));
    }

    @GetMapping("events/{eventId}/tickets/me")
    @Operation(summary = "Get current user ticket")
    @ApiResponse(responseCode = "200", description = "Ticket returned for event")
    public ResponseEntity<TicketResponse> getCurrentTicket(@PathVariable Long eventId) {
        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();

        return ResponseEntity.status(200).body(ticketService.getCurrentTicket(currentUserId, eventId));
    }

    @GetMapping("/tickets/{id}")
    @Operation(summary = "Get ticket by id (organizer)")
    @ApiResponse(responseCode = "200", description = "Ticket returned")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();

        return ResponseEntity.ok(ticketService.getTicketById(id, currentUserId));
    }

    @DeleteMapping("/events/{eventId}/tickets/{id}")
    @Operation(summary = "Delete ticket (organizer)")
    @ApiResponse(responseCode = "204", description = "Ticket deleted")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long eventId, @PathVariable Long id) {
        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();

        ticketService.deleteTicket(eventId, id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/events/{eventId}/tickets/me")
    @Operation(summary = "Delete my ticket")
    @ApiResponse(responseCode = "204", description = "Ticket deleted")
    public ResponseEntity<Void> deleteMyTicket(@PathVariable Long eventId) {
        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();

        ticketService.deleteMyTicket(currentUserId, eventId);
        return ResponseEntity.noContent().build();
    }

}
