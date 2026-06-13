package bg.sofia.uni.event_management.controller;

import bg.sofia.uni.event_management.dto.EventRequest;
import bg.sofia.uni.event_management.dto.EventResponse;
import bg.sofia.uni.event_management.dto.UserResponse;
import bg.sofia.uni.event_management.security.SecurityUtil;
import bg.sofia.uni.event_management.service.EventService;
import bg.sofia.uni.event_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    // ===================== PUBLIC ENDPOINTS =====================

    @GetMapping
    @Operation(summary = "Get events by params")
    public List<EventResponse> getEvents(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String venue,
        @RequestParam(required = false) Long organizerId,
        @RequestParam(required = false) Long categoryId
    ) {

        return eventService.getEvents(title, venue, organizerId, categoryId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by id")
    @ApiResponse(responseCode = "200", description = "Event found")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<EventResponse> getEventById(
        @Parameter(description = "Event id")
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    // ===================== ORGANIZER ENDPOINTS =====================

    @PostMapping
    @Operation(summary = "Create event")
    @ApiResponse(responseCode = "201", description = "Event created")
    @ApiResponse(responseCode = "400", description = "Invalid event data")
    public ResponseEntity<EventResponse> createEvent(
        @RequestBody @Valid EventRequest req
    ) {
        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();

        EventResponse created = eventService.createEvent(currentUserId, req);

        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update event")
    @ApiResponse(responseCode = "200", description = "Event updated")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @ApiResponse(responseCode = "400", description = "Invalid event data")
    public ResponseEntity<Void> updateEvent(
        @Parameter(description = "Event id")
        @PathVariable Long id,

        @RequestBody @Valid EventRequest req
    ) {
        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();
        eventService.updateEvent(currentUserId, id, req);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event")
    @ApiResponse(responseCode = "204", description = "Event deleted")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<Void> deleteEvent(
        @Parameter(description = "Event id")
        @PathVariable Long id
    ) {
        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();
        eventService.deleteEvent(currentUserId, id);

        return ResponseEntity.noContent().build();
    }

    }

