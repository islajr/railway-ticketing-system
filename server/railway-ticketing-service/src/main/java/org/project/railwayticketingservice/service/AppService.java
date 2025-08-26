package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.response.HomePageResponse;
import org.project.railwayticketingservice.dto.app.response.ReservationResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.project.railwayticketingservice.entity.*;
import org.project.railwayticketingservice.repository.PassengerRepository;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppService {

    private final PassengerRepository passengerRepository;
    private final ScheduleRepository scheduleRepository;

    public ResponseEntity<HomePageResponse> generateHomepage() {
        String email = ((PassengerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);

        List<Reservation> reservations = passenger.getReservations();
        List<Schedule> upcomingSchedules = scheduleRepository.findSchedulesByDepartureTimeBetween(LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        return ResponseEntity.ok(HomePageResponse.builder()
                        .reservations(reservations.stream()
                                .map(reservation -> ReservationResponse.builder()
                                        .reservationId(reservation.getId())
                                        .train(reservation.getSchedule().getTrain().getName())
                                        .seatNumber(reservation.getScheduleSeat().getLabel())
                                        .time(Time.fromLocalDateTime(reservation.getSchedule().getDepartureTime()))
                                        .origin(reservation.getSchedule().getOrigin().getName())
                                        .build())
                                .toList())
                        .upcomingSchedules(upcomingSchedules.stream()
                                .map(
                                        schedule -> TrainScheduleResponse.builder()
                                                .scheduleId(schedule.getId())
                                                .train(schedule.getTrain().getName())
                                                .availableSeats(schedule.getEmptySeats().stream()
                                                        .map(ScheduleSeat::getLabel)
                                                        .collect(Collectors.toList()))
                                                .currentCapacity(schedule.getCurrentCapacity())
                                                .isFull(schedule.isFull())
                                                .origin(schedule.getOrigin().toString())
                                                .destination(schedule.getDestination().toString())
                                                .departureTime(Time.fromLocalDateTime(schedule.getDepartureTime()))
                                                .arrivalTime(Time.fromLocalDateTime(schedule.getArrivalTime()))
                                                .build()
                                )
                                .toList())
                .build());

    }

    /* should be its own job, running for all clients later on. may even include location specificity */
    public ResponseEntity<List<TrainScheduleResponse>> getUpcomingTrainSchedules() {
        List<Schedule> upcomingSchedules = scheduleRepository.findSchedulesByDepartureTimeBetween(LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        return ResponseEntity.ok(
                upcomingSchedules.stream()
                        .map(
                                schedule -> TrainScheduleResponse.builder()
                                        .scheduleId(schedule.getId())
                                        .train(schedule.getTrain().getName())
                                        .availableSeats(schedule.getEmptySeats().stream()
                                                .map(ScheduleSeat::getLabel)
                                                .collect(Collectors.toList()))
                                        .currentCapacity(schedule.getCurrentCapacity())
                                        .isFull(schedule.isFull())
                                        .origin(schedule.getOrigin().toString())
                                        .destination(schedule.getDestination().toString())
                                        .departureTime(Time.fromLocalDateTime(schedule.getDepartureTime()))
                                        .arrivalTime(Time.fromLocalDateTime(schedule.getArrivalTime()))
                                        .build()
                        )
                        .toList()
        );
    }
}
