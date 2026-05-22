package bg.sofia.uni.event_management.controller;

import bg.sofia.uni.event_management.dto.UserRequest;
import bg.sofia.uni.event_management.dto.UserResponse;
import bg.sofia.uni.event_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ===================== ADMIN ENDPOINTS =====================

    @GetMapping()
    @Operation(summary = "Get all users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> getUserById(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "200", description = "User updated")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "400", description = "User failed to update")
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "User id")
            @PathVariable long id,

            @RequestBody @Valid UserRequest req
    ) {
        userService.updateUser(id, req);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== CURRENT USER (JWT) =====================
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    @ApiResponse(responseCode = "200", description = "User found")
    public ResponseEntity<UserResponse> getCurrentUser() {
        long currentUserId = getCurrentUserId(); // TODO: replace with JWT
        return ResponseEntity.ok(userService.getUserById(currentUserId));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user")
    @ApiResponse(responseCode = "200", description = "User updated")
    @ApiResponse(responseCode = "400", description = "User failed to update")
    public ResponseEntity<Void> updateCurrentUser(@RequestBody @Valid UserRequest req) {
        long currentUserId = getCurrentUserId(); // TODO: replace with JWT
        userService.updateUser(currentUserId, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete current user")
    @ApiResponse(responseCode = "204", description = "User deleted")
    public ResponseEntity<Void> deleteCurrentUser() {
        long currentUserId = getCurrentUserId(); // TODO: replace with JWT
        userService.deleteUser(currentUserId);
        return ResponseEntity.noContent().build();
    }

    // ===================== HELPERS =====================

    private long getCurrentUserId() {
        // TODO: replace with
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // return ((UserDetails) auth.getPrincipal()).getId();
        return 1L; // hardcoded placeholder за сега
    }

}
