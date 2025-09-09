package org.project.railwayticketingservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RtsException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(RtsException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.status).body(
                new ErrorResponse(ex.getStatus(), ex.getMessage(), request.getRequestURI()));
    }

    /*@ExceptionHandler(value = RtsException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(RtsException ex) {
        return ResponseEntity.status(HttpStatusCode.valueOf(ex.status)).body(new ErrorResponse(ex.message));
    }*/
}
