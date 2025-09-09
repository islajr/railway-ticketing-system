package org.project.railwayticketingservice.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = HttpStatus.valueOf(status).getReasonPhrase();
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(HttpStatus status, String message, String path) {
        this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path);
    }
}
