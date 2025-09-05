package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;

@Builder
public record UserDetailsResponse(
        String firstName,
        String lastName
) {
}
