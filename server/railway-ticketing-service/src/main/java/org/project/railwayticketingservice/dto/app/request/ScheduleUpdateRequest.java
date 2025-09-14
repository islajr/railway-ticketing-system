package org.project.railwayticketingservice.dto.app.request;

import org.project.railwayticketingservice.entity.Time;
import org.project.railwayticketingservice.entity.enums.Status;

public record ScheduleUpdateRequest(
        String origin,
        String destination,
        Status status,
        Time departureTime,
        Time arrivalTime
) {
}
