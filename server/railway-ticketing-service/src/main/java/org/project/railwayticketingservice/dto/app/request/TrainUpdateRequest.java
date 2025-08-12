package org.project.railwayticketingservice.dto.app.request;

public record TrainUpdateRequest(
    String name,
    String isActive
) {
}
