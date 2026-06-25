package bg.sofia.uni.event_management.security;

import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static String getCurrentEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}