package bg.sofia.uni.event_management.controller;

import bg.sofia.uni.event_management.dto.SessionRequest;
import bg.sofia.uni.event_management.dto.SessionResponse;
import bg.sofia.uni.event_management.dto.UserResponse;
import bg.sofia.uni.event_management.security.SecurityUtil;
import bg.sofia.uni.event_management.service.SessionService;
import bg.sofia.uni.event_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final UserService userService;

    public SessionController(SessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    // ===================== GET =====================

    @GetMapping("/{id}")
    @Operation(summary = "Get session by id")
    public ResponseEntity<SessionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getById(id));
    }

    // ===================== UPDATE =====================

    @PutMapping("/{id}")
    @Operation(summary = "Update session")
    public ResponseEntity<SessionResponse> update(
        @PathVariable Long id,
        @RequestBody @Valid SessionRequest request
    ) {
        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();

        return ResponseEntity.ok(
            sessionService.update(currentUserId, id, request)
        );
    }

    // ===================== DELETE =====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete session")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());
        Long currentUserId = user.id();

        sessionService.delete(currentUserId, id);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // ========== NESTED ENDPOINTS (EVENT CONTEXT) =========
    // =====================================================

    @GetMapping("/events/{eventId}/sessions")
    public ResponseEntity<List<SessionResponse>> getByEvent(
        @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(sessionService.getByEvent(eventId));
    }

    @PostMapping("/events/{eventId}/sessions")
    public ResponseEntity<SessionResponse> create(
        @PathVariable Long eventId,
        @RequestBody @Valid SessionRequest request
    ) {
        String email = SecurityUtil.getCurrentEmail();
        UserResponse user = userService.getByEmail(email);
        Long currentUserId = user.id();

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(sessionService.create(currentUserId, eventId, request));
    }
}
