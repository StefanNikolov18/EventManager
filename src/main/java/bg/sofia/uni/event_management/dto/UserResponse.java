package bg.sofia.uni.event_management.dto;

import bg.sofia.uni.event_management.model.User;

public record UserResponse(long id, String email, String firstName, String lastName) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
