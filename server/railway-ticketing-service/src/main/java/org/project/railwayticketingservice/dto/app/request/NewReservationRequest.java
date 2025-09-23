package org.project.railwayticketingservice.dto.app.request;

import lombok.Builder;

@Builder
public record NewReservationRequest(
    String scheduleId,
    String preferredSeat

) {
}
