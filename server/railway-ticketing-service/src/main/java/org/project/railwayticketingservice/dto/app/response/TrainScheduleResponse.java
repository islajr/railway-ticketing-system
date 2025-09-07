package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;
import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.ScheduleSeat;
import org.project.railwayticketingservice.entity.Time;

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
    Time departureTime,
    Time arrivalTime,
    boolean isCompleted
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
                .departureTime(Time.fromLocalDateTime(schedule.getDepartureTime()))
                .arrivalTime(Time.fromLocalDateTime(schedule.getArrivalTime()))
                .isCompleted(schedule.isCompleted())
                .build();
    }
}
