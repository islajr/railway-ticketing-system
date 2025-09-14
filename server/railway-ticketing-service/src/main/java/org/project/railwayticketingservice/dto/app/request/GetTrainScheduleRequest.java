package org.project.railwayticketingservice.dto.app.request;

import java.time.LocalDateTime;

public record GetTrainScheduleRequest(
        String origin,
        String destination,
        LocalDateTime time
) {
}
