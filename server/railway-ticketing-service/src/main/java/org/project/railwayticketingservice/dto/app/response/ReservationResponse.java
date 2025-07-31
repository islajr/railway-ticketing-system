package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;

@Builder
public record ReservationResponse(
        String reservationId,
        String train,
        String seatNumber,
        Long dateTime   // could be string, idk.
) {
}
