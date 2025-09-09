package org.project.railwayticketingservice.dto.auth.response;

public record LoginPassengerResponse(
        String accessToken,
        String refreshToken
) {
    public static LoginPassengerResponse of(String accessToken, String refreshToken) {
        return new LoginPassengerResponse(accessToken, refreshToken);
    }
}
