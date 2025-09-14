package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;
import org.project.railwayticketingservice.entity.Reservation;
import org.project.railwayticketingservice.entity.Time;

import java.time.LocalDateTime;

@Builder
public record ReservationResponse(
        String reservationId,
        String train,
        String seatNumber,
        String origin,
        LocalDateTime time

) {
    public static ReservationResponse from(Reservation reservation) {
        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .train(reservation.getSchedule().getTrain().getName())
                .seatNumber(reservation.getScheduleSeat().getLabel())
                .origin(reservation.getSchedule().getOrigin().getName())
                .time(reservation.getSchedule().getArrivalTime())
                .build();
    }
}
