package bg.sofia.uni.event_management.controller;

import bg.sofia.uni.event_management.dto.PresentationMaterialRequest;
import bg.sofia.uni.event_management.dto.PresentationMaterialResponse;
import bg.sofia.uni.event_management.dto.UserResponse;
import bg.sofia.uni.event_management.security.SecurityUtil;
import bg.sofia.uni.event_management.service.PresentationMaterialService;
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
public class PresentationMaterialController {

    private final PresentationMaterialService materialService;
    private final UserService userService;

    public PresentationMaterialController(PresentationMaterialService materialService,
                                          UserService userService) {
        this.materialService = materialService;
        this.userService = userService;
    }

    @GetMapping("/speakers/{speakerId}/materials")
    @Operation(summary = "Get all materials for a speaker")
    public ResponseEntity<List<PresentationMaterialResponse>> getBySpeaker(
        @Parameter(description = "Speaker id")
        @PathVariable Long speakerId) {

        return ResponseEntity.ok(
            materialService.getBySpeaker(speakerId)
        );
    }

    @GetMapping("/materials/{id}")
    @Operation(summary = "Get material by id")
    @ApiResponse(responseCode = "200", description = "Material found")
    @ApiResponse(responseCode = "404", description = "Material not found")
    public ResponseEntity<PresentationMaterialResponse> getById(
        @Parameter(description = "Material id")
        @PathVariable Long id) {

        return ResponseEntity.ok(materialService.getById(id));
    }

    @PostMapping("/speakers/{speakerId}/materials")
    @Operation(summary = "Create presentation material for a speaker")
    @ApiResponse(responseCode = "201", description = "Material created")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<PresentationMaterialResponse> create(
        @Parameter(description = "Speaker id")
        @PathVariable Long speakerId,
        @RequestBody @Valid PresentationMaterialRequest request) {

        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());

        PresentationMaterialResponse response =
            materialService.create(user.id(), speakerId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/materials/{id}")
    @Operation(summary = "Delete material by id")
    @ApiResponse(responseCode = "204", description = "Material deleted")
    @ApiResponse(responseCode = "404", description = "Material not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<Void> delete(
        @Parameter(description = "Material id")
        @PathVariable Long id) {

        UserResponse user = userService.getByEmail(SecurityUtil.getCurrentEmail());

        materialService.delete(user.id(), id);

        return ResponseEntity.noContent().build();
    }
}