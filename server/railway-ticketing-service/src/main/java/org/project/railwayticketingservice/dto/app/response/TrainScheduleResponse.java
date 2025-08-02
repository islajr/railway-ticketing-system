package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;
import org.project.railwayticketingservice.entity.ScheduleSeat;
import org.project.railwayticketingservice.entity.Time;

import java.util.List;

@Builder
public record TrainScheduleResponse(
    String scheduleId,
    String train,
    List<String> availableSeats,
    Long currentCapacity,
    boolean isFull,
    String origin,
    String destination,
    Time departureTime,
    Time arrivalTime
) {
}
