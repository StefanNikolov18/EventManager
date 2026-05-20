package bg.sofia.uni.event_management.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
    public AccessDeniedException(String message, Exception ex) {
        super(message, ex);
    }


}
