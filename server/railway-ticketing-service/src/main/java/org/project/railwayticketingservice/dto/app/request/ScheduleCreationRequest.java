package org.project.railwayticketingservice.dto.app.request;

import org.project.railwayticketingservice.entity.enums.Status;

import java.time.LocalDateTime;

public record ScheduleCreationRequest(
        String train,
        String origin,
        String destination,
        Status status,
        LocalDateTime departure,
        LocalDateTime arrival

) {
}
