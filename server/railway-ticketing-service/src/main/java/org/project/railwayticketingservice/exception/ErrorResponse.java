package org.project.railwayticketingservice.exception;

public record ErrorResponse(
        int status,
        String message
) {
}
