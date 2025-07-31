package org.project.railwayticketingservice.dto.app.request;

import org.project.railwayticketingservice.entity.Time;

public record ScheduleCreationRequest(
        String train,
        String origin,
        String destination,
        Time departure,
        Time arrival

) {
}
