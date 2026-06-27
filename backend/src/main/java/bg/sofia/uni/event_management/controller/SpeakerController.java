package bg.sofia.uni.event_management.controller;

import bg.sofia.uni.event_management.dto.SpeakerRequest;
import bg.sofia.uni.event_management.dto.SpeakerResponse;
import bg.sofia.uni.event_management.dto.UserResponse;
import bg.sofia.uni.event_management.security.SecurityUtil;
import bg.sofia.uni.event_management.service.SpeakerService;
import bg.sofia.uni.event_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/speakers")
public class SpeakerController {

    private final SpeakerService speakerService;
    private final UserService userService;

    public SpeakerController(SpeakerService speakerService,
                             UserService userService) {
        this.speakerService = speakerService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get speaker by id")
    @ApiResponse(responseCode = "200", description = "Speaker found")
    @ApiResponse(responseCode = "404", description = "Speaker not found")
    public ResponseEntity<SpeakerResponse> getSpeakerById(
        @Parameter(description = "Speaker id")
        @PathVariable Long id) {

        return ResponseEntity.ok(speakerService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update speaker")
    @ApiResponse(responseCode = "200", description = "Speaker updated")
    @ApiResponse(responseCode = "404", description = "Speaker not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<Void> updateSpeaker(
        @PathVariable Long id,
        @RequestBody @Valid SpeakerRequest request) {

        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());

        speakerService.update(user.id(), id, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete speaker")
    @ApiResponse(responseCode = "204", description = "Speaker deleted")
    @ApiResponse(responseCode = "404", description = "Speaker not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<Void> deleteSpeaker(
        @PathVariable Long id) {

        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());

        speakerService.delete(user.id(), id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sessions/{sessionId}/speakers")
    @Operation(summary = "Get speakers by session")
    public ResponseEntity<List<SpeakerResponse>> getBySession(
        @PathVariable Long sessionId) {

        return ResponseEntity.ok(
            speakerService.getBySession(sessionId)
        );
    }

    @PostMapping("/sessions/{sessionId}/speakers")
    @Operation(summary = "Create speaker for session")
    public ResponseEntity<SpeakerResponse> create(
        @PathVariable Long sessionId,
        @RequestBody @Valid SpeakerRequest request
    ) {
        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());

        SpeakerResponse response =
            speakerService.create(user.id(), sessionId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response);
    }
}