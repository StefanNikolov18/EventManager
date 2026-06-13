package bg.sofia.uni.event_management.controller;

import bg.sofia.uni.event_management.dto.AdminRequest;
import bg.sofia.uni.event_management.dto.UserRequest;
import bg.sofia.uni.event_management.dto.UserResponse;
import bg.sofia.uni.event_management.security.SecurityUtil;
import bg.sofia.uni.event_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @Operation(summary = "Get all users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> getUserById(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "200", description = "User updated")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "400", description = "User failed to update")
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "User id")
            @PathVariable long id,

            @RequestBody @Valid AdminRequest req
    ) {
        userService.updateUser(id, req);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user profile")
    @ApiResponse(responseCode = "200", description = "User found")
    public ResponseEntity<UserResponse> getCurrentUser() {
        // Extract user ID from SecurityContext
        String email = SecurityUtil.getCurrentEmail();

        return ResponseEntity.ok(userService.getByEmail(email));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update current user")
    @ApiResponse(responseCode = "200", description = "User updated")
    @ApiResponse(responseCode = "400", description = "User failed to update")
    public ResponseEntity<Void> updateCurrentUser(@RequestBody @Valid UserRequest req) {
        String emailSec = SecurityUtil.getCurrentEmail();

        userService.updateByEmail(emailSec, req);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete current user")
    @ApiResponse(responseCode = "204", description = "User deleted")
    public ResponseEntity<Void> deleteCurrentUser() {
        // Extract user ID from SecurityContext
        String email = SecurityUtil.getCurrentEmail();

        userService.deleteByEmail(email);

        return ResponseEntity.noContent().build();
    }

    // ===================== HELPER =====================

}
