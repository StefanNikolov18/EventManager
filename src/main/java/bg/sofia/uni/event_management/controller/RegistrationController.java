package bg.sofia.uni.event_management.controller;

import bg.sofia.uni.event_management.dto.RegistrationRequest;
import bg.sofia.uni.event_management.dto.RegistrationResponse;
import bg.sofia.uni.event_management.dto.UserResponse;
import bg.sofia.uni.event_management.security.SecurityUtil;
import bg.sofia.uni.event_management.service.RegistrationService;
import bg.sofia.uni.event_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;

    public RegistrationController(RegistrationService registrationService,
                                  UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
    }

    // ===================== EVENT REGISTRATIONS =====================

    @GetMapping("/events/{eventId}/registrations")
    @Operation(summary = "Get all registrations by event")
    @ApiResponse(responseCode = "200", description = "List of registrations")
    @ApiResponse(responseCode = "404", description = "Event not found")
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
        String email = SecurityUtil.getCurrentEmail();
        UserResponse user = userService.getByEmail(email);
        Long currentUserId = user.id();

        RegistrationResponse response =
            registrationService.create(currentUserId, eventId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===================== UPDATE REGISTRATION =====================

    @PutMapping("/registrations/{id}")
    @Operation(summary = "Update registration status")
    public ResponseEntity<RegistrationResponse> update(
        @PathVariable Long id,
        @RequestBody  @Valid RegistrationRequest request
    ) {
        String email = SecurityUtil.getCurrentEmail();
        UserResponse user = userService.getByEmail(email);

        RegistrationResponse response =
            registrationService.update(user.id(), id, request);

        return ResponseEntity.ok(response);
    }

    // ===================== DELETE REGISTRATION =====================

//    @DeleteMapping("/registrations/{id}")
//    @Operation(summary = "Delete registration")
//    @ApiResponse(responseCode = "204", description = "Deleted")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        registrationService.delete(id);
//        return ResponseEntity.noContent().build();
//    }

}