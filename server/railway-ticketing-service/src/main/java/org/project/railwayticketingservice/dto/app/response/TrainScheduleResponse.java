package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;
import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.ScheduleSeat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record TrainScheduleResponse(
    String scheduleId,
    String train,
    List<String> availableSeats,
    Long currentCapacity,
    boolean isFull,
    String origin,
    String destination,
    String status,
    LocalDateTime departureTime,
    LocalDateTime arrivalTime
) {
    public static TrainScheduleResponse fromSchedule(Schedule schedule) {
        return TrainScheduleResponse.builder()
                .scheduleId(schedule.getId())
                .train(schedule.getTrain().getName())
                .availableSeats(schedule.getEmptySeats().stream()
                        .map(
                                ScheduleSeat::getLabel
                        ).collect(Collectors.toList()))
                .currentCapacity(schedule.getCurrentCapacity())
                .isFull(schedule.isFull())
                .origin(schedule.getOrigin().getName())
                .destination(schedule.getDestination().getName())
                .departureTime(schedule.getDepartureTime())
                .arrivalTime(schedule.getArrivalTime())
                .status(schedule.getStatus())
                .build();
    }
}
