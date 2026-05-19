package bg.sofia.uni.event_management.model.enums;

public enum Role {
    ADMIN,
    USER;

    public static Role parseRole(String role) {
        return Role.valueOf(role.trim().toUpperCase());
    }
}
