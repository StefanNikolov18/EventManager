package bg.sofia.uni.event_management.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Exception ex) {
        super (message, ex);
    }
}
