package bg.sofia.uni.event_management.controller;

import bg.sofia.uni.event_management.dto.RegistrationResponse;
import bg.sofia.uni.event_management.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    // ===================== EVENT REGISTRATIONS =====================

    @GetMapping("/events/{eventId}/registrations")
    @Operation(summary = "Get all registrations by event")
    @ApiResponse(responseCode = "200", description = "List of registrations")
    public List<RegistrationResponse> getByEvent(@PathVariable Long eventId) {
        return registrationService.getByEventId(eventId);
    }

    // ===================== SINGLE REGISTRATION =====================

    @GetMapping("/registrations/{id}")
    @Operation(summary = "Get registration by id")
    @ApiResponse(responseCode = "200", description = "Registration found")
    @ApiResponse(responseCode = "404", description = "Registration not found")
    public ResponseEntity<RegistrationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.getById(id));
    }

    // ===================== CREATE REGISTRATION =====================

    @PostMapping("/events/{eventId}/registrations")
    @Operation(summary = "Create registration")
    @ApiResponse(responseCode = "201", description = "Registration created")
    public ResponseEntity<RegistrationResponse> create(
        @PathVariable Long eventId
    ) {
        long currentUserId = getCurrentUserId(); // TODO: JWT later

        RegistrationResponse response =
            registrationService.create(currentUserId, eventId);

        return ResponseEntity.status(201).body(response);
    }

    // ===================== UPDATE REGISTRATION =====================

    @PutMapping("/registrations/{id}")
    @Operation(summary = "Update registration status")
    public ResponseEntity<RegistrationResponse> update(
        @PathVariable Long id
    ) {
        RegistrationResponse response =
            registrationService.update(id);

        return ResponseEntity.ok(response);
    }

    // ===================== DELETE REGISTRATION =====================

    @DeleteMapping("/registrations/{id}")
    @Operation(summary = "Delete registration")
    @ApiResponse(responseCode = "204", description = "Deleted")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        registrationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== TEMP JWT PLACEHOLDER =====================

    private long getCurrentUserId() {
        return 1L; // placeholder
    }
}