package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;

import java.util.List;

@Builder
public record HomePageResponse(
        List<ReservationResponse> reservations,
        List<TrainScheduleResponse> upcomingSchedules
) {
}
