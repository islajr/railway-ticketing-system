package org.project.railwayticketingservice.dto.app.request;

import org.project.railwayticketingservice.entity.enums.Status;

import java.time.LocalDateTime;

public record ScheduleUpdateRequest(
        String origin,
        String destination,
        Status status,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime
) {
}
