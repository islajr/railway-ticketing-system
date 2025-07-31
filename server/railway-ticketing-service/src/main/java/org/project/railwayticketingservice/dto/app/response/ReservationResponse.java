package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;
import org.project.railwayticketingservice.entity.Time;

@Builder
public record ReservationResponse(
        String reservationId,
        String train,
        String seatNumber,
        String origin,
        Time time

) {
}
