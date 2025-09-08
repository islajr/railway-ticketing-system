package org.project.railwayticketingservice.exception.exceptions;

public class RtsException extends RuntimeException {

    public int status;
    public String message;
    public String timestamp;

    public RtsException(int status, String message, String timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}
