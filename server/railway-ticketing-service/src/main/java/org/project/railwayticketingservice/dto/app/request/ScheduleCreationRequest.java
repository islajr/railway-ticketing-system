package org.project.railwayticketingservice.dto.app.request;

import org.project.railwayticketingservice.entity.Time;
import org.project.railwayticketingservice.entity.enums.Status;

public record ScheduleCreationRequest(
        String train,
        String origin,
        String destination,
        Status status,
        Time departure,
        Time arrival

) {
}
