package org.project.railwayticketingservice.exception.exceptions;

public class RtsException extends RuntimeException {

    public int status;
    public String message;

    public RtsException(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
