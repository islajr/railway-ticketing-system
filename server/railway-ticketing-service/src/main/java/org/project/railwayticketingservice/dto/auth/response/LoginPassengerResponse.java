package org.project.railwayticketingservice.dto.auth.response;

public record LoginPassengerResponse(
        String accessToken,
        String expiration
) {
    public static LoginPassengerResponse of(String accessToken, String expiration) {
        return new LoginPassengerResponse(accessToken, expiration);
    }
}
