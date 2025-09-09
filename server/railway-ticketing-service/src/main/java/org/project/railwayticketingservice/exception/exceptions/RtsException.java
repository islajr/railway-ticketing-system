package org.project.railwayticketingservice.exception.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class RtsException extends RuntimeException {

//    public LocalDateTime timestamp;
    public HttpStatus status;
//    public String error;
    public String message;
//    public String path;

    public RtsException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
