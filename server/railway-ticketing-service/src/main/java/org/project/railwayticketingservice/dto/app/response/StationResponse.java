package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;

@Builder
public record StationResponse(
        Long id,
        String name,
        String code,
        String lga
) {
}
