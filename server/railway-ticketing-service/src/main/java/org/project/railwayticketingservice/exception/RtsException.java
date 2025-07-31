package org.project.railwayticketingservice.exception;

public class RtsException extends RuntimeException {

    int status;
    String message;

    public RtsException(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
