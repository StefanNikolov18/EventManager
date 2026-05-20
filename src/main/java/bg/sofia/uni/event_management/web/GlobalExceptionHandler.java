package bg.sofia.uni.event_management.web;

import bg.sofia.uni.event_management.exceptions.AccessDeniedException;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.web.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex, HttpServletRequest req) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                404,
                ex.getMessage(),
                req.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handlerIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest req) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                req.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
        AccessDeniedException ex, HttpServletRequest req) {

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            ex.getMessage(),
            req.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(errorResponse);
    }
}
