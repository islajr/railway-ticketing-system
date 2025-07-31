package org.project.railwayticketingservice.dto.app.response;

public record TrainScheduleResponse(
    String train,
    String origin,
    String destination,
    String departureTime,
    String arrivalTime
) {
}
