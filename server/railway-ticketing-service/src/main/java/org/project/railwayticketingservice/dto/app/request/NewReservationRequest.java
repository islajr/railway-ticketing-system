package org.project.railwayticketingservice.dto.app.request;

import lombok.Builder;

import java.util.Date;

@Builder
public record NewReservationRequest(
    String scheduleId,
    String preferredSeat

) {
}
