package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;

@Builder
public record NewTrainResponse(
        String trainId,
        String trainName,
        String isActive,
        String capacity
) {
}
