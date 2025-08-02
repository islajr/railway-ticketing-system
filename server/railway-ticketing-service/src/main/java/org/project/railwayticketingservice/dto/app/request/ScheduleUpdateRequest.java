package org.project.railwayticketingservice.dto.app.request;

import org.project.railwayticketingservice.entity.Time;

public record ScheduleUpdateRequest(
        String origin,
        String destination,
        Time departureTime,
        Time arrivalTime
) {
}
