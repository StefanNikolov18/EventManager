package bg.sofia.uni.event_management.controller;

import bg.sofia.uni.event_management.dto.TicketResponse;
import bg.sofia.uni.event_management.dto.UserResponse;
import bg.sofia.uni.event_management.security.SecurityUtil;
import bg.sofia.uni.event_management.service.TicketService;
import bg.sofia.uni.event_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    // ===================== PUBLIC ENDPOINTS =====================
    @PostMapping("/events/{eventId}/tickets")
    @Operation(summary = "Create Ticket for event")
    @ApiResponse(responseCode = "201", description = "Ticket created")
    public ResponseEntity<TicketResponse> create(@PathVariable Long eventId) {

        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();

        return ResponseEntity.status(201).body(ticketService.create(currentUserId, eventId));
    }



}
