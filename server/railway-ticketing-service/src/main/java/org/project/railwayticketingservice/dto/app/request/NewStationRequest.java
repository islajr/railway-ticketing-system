package org.project.railwayticketingservice.dto.app.request;

public record NewStationRequest(
        String name,
        String code,
        String LGA
) {
}
