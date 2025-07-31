package org.project.railwayticketingservice.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RtsException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(RtsException ex) {
        return ResponseEntity.status(HttpStatusCode.valueOf(ex.status)).body(new ErrorResponse(ex.message));
    }
}
