package org.project.railwayticketingservice.dto.auth.response;

import lombok.Builder;

@Builder
public record LoginAdminResponse(
        String accessToken,
        String refreshToken
) {
}
