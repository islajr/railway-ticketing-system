package org.project.railwayticketingservice.dto.auth.response;

import lombok.Builder;

@Builder
public record LoginAdminResponse(
        String accessToken,
        String expiration
) {
    public static LoginAdminResponse of(String accessToken, String expiration) {
        return new LoginAdminResponse(accessToken, expiration);
    }
}
