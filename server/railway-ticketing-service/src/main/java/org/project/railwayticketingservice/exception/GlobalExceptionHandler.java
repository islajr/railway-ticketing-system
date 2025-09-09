package org.project.railwayticketingservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RtsException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(RtsException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.status).body(
                new ErrorResponse(ex.getStatus(), ex.getMessage(), request.getRequestURI())
        );
    }

    // 405 - Method not allowed
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request.getRequestURI())
        );
    }

    // 400 - No Resource found Exception
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI())
        );
    }

    // Fallback handler for all unhandled Exceptions
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI())
        );
    }
}
