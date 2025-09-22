package org.project.railwayticketingservice.dto.app.request;

public record GetTrainScheduleRequest(
        String origin,
        String destination
) {
}
