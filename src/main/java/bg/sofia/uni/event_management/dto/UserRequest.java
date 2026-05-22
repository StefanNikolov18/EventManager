package bg.sofia.uni.event_management.dto;

import bg.sofia.uni.event_management.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "First name cannot be empty")
        String firstName,

        @NotBlank(message = "Last Name cannot be empty")
        String lastName,

        @NotBlank(message = "Role cannot be empty")
        String role
)
{ }
