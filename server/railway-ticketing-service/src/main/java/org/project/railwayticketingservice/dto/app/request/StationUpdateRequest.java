package org.project.railwayticketingservice.dto.app.request;

public record StationUpdateRequest(
        String name,
        String code,
        String isActive
) {
}
