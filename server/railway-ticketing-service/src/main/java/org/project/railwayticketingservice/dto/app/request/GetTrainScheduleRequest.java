package org.project.railwayticketingservice.dto.app.request;

import org.project.railwayticketingservice.entity.Time;

public record GetTrainScheduleRequest(
        String origin,
        String destination,
        Time time
) {
}
