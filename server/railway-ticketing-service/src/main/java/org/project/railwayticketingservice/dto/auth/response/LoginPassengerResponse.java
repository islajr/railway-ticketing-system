package org.project.railwayticketingservice.dto.auth.response;

public record LoginPassengerResponse(
        String accessToken,
        String refreshToken
) {
}
