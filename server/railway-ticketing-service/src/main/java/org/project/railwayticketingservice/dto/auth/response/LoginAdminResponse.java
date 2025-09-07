package org.project.railwayticketingservice.dto.auth.response;

import lombok.Builder;

@Builder
public record LoginAdminResponse(
        String accessToken,
        String refreshToken
) {
    public static LoginAdminResponse of(String accessToken, String refreshToken) {
        return new LoginAdminResponse(accessToken, refreshToken);
    }
}
