package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.GetTrainScheduleRequest;
import org.project.railwayticketingservice.dto.app.request.ScheduleCreationRequest;
import org.project.railwayticketingservice.dto.app.request.ScheduleUpdateRequest;
import org.project.railwayticketingservice.dto.app.response.AppResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.project.railwayticketingservice.entity.*;
import org.project.railwayticketingservice.exception.RtsException;
import org.project.railwayticketingservice.repository.*;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final ReservationRepository reservationRepository;
    private final Utilities utilities;

    // admin-specific method
    public ResponseEntity<AppResponse> createSchedule(ScheduleCreationRequest request) {
        Train train = trainRepository.findTrainByName(request.train());

        if (train != null) {
            // check if the schedule actually exists: name -> origin -> departure time
            if (train.getName().equals(request.train().strip())) {

                for (Schedule schedule : train.getSchedules()) {
                    // check for station of origin
                    if (request.origin().equals(schedule.getOrigin().toString())) {
                        // check for departure times
                        if (request.departure().getLocalDateTime().equals(schedule.getDepartureTime())) {
                            throw new RtsException(409, "there is already a schedule fixed for this period.");  // try another train or time?
                        }
                    }
                }
            }

            // convert to station
            Station origin = stationRepository.getStationByName(request.origin());
            Station destination = stationRepository.getStationByName(request.destination());

            if (origin != null && destination != null) {

                Schedule schedule = Schedule.builder()
                        .train(train)
                        .currentCapacity(train.getCapacity())
                        .isFull(false)
                        .origin(origin)
                        .destination(destination)
                        .departureTime(request.departure().getLocalDateTime())
                        .arrivalTime(request.arrival().getLocalDateTime())
                        .build();

                scheduleRepository.save(schedule);
                System.out.println("schedule successfully created");

                // generating seats for schedule
                utilities.generateSeatsForSchedule(schedule);
                System.out.println("seats generated");

                return ResponseEntity.status(HttpStatus.CREATED).body(AppResponse.builder()
                        .message("schedule successfully created.")
                        .build());
            } throw new RtsException(400, "Please input a valid station");

        } else {
            throw new RtsException(400, "Schedule creation failed! No such train!");
        }
    }

    public ResponseEntity<TrainScheduleResponse> getTrainSchedule(String id) {
        Schedule schedule = scheduleRepository.findScheduleById(id);

        if (schedule != null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    TrainScheduleResponse.builder()
                            .scheduleId(schedule.getId())
                            .train(schedule.getTrain().getName())
                            .currentCapacity(schedule.getCurrentCapacity())
                            .isFull(schedule.isFull())
                            .origin(schedule.getOrigin().toString())
                            .destination(schedule.getDestination().toString())
                            .departureTime(Time.fromLocalDateTime(schedule.getDepartureTime()))
                            .arrivalTime(Time.fromLocalDateTime(schedule.getArrivalTime()))
                            .availableSeats(schedule.getEmptySeats().stream()
                                    .map(ScheduleSeat::getLabel)
                                    .collect(Collectors.toList()))
                            .build()
            );
        } throw new RtsException(404, "Schedule not found!");
    }

    public ResponseEntity<TrainScheduleResponse> editTrainSchedule(String id, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findScheduleById(id);
        Station origin = stationRepository.getStationByName(request.origin());
        Station destination = stationRepository.getStationByName(request.destination());

        if (schedule != null) {

            // origin
            if (!Objects.equals(request.origin(), "null") && !Objects.equals(schedule.getOrigin(), origin)) {
                schedule.setOrigin(origin);
                System.out.println("updated origin for train " + id);
                // destination
            } if (!Objects.equals(request.destination(), "null") && !Objects.equals(schedule.getDestination(), destination)) {
                schedule.setDestination(destination);
                System.out.println("updated destination for train " + id);
            }   // departure
            if (request.departureTime() != null && !Objects.equals(request.departureTime(), Time.fromLocalDateTime(schedule.getDepartureTime()))) {
                schedule.setDepartureTime(request.departureTime().getLocalDateTime());
                System.out.println("updated departure time for train " + id);
            }   // arrival
            if (request.arrivalTime() != null && !Objects.equals(request.arrivalTime(), Time.fromLocalDateTime(schedule.getArrivalTime()))) {
                schedule.setArrivalTime(request.arrivalTime().getLocalDateTime());
                System.out.println("updated arrival time for train " + id);
            }

            scheduleRepository.save(schedule);

        } throw new RtsException(404, "Schedule not found!");

    }

    public ResponseEntity<List<TrainScheduleResponse>> getTrainSchedules(String filter1, String filter2, String filter3, GetTrainScheduleRequest request) {
        List<Schedule> schedules;
        Station origin = stationRepository.getStationByName(request.origin());
        Station destination = stationRepository.getStationByName(request.destination());

        if (filter3.equals("null")) {   // if filter3 is null

            // check filter2
            if (filter2.equals("null")) {   // if filter2 is null

                if (!filter1.equals("null")) {
                    schedules = utilities.getSchedules(filter1, request);
                } else {
                    throw new RtsException(400, "Filters cannot be null!");
                }

            } else {
                schedules = utilities.getSchedules(filter1, filter2, request);
            }
        } else {
            schedules = scheduleRepository.findSchedulesByOriginAndDestinationAndDepartureTime(origin, destination, request.time().getLocalDateTime());
        }

        // convert schedules to proper response DTOs
        return ResponseEntity.status(HttpStatus.OK).body(
                schedules.stream()
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

    public ResponseEntity<AppResponse> deleteTrainSchedule(String id) {
        Schedule schedule = scheduleRepository.findScheduleById(id);

        if (schedule != null) {

            // delete all reservations
            for (ScheduleSeat seat : schedule.getSeats()) {
                if (seat.isReserved() && seat.getReservation() != null) {
                    reservationRepository.delete(seat.getReservation());
                }
            }

            // delete schedule
            scheduleRepository.delete(schedule);
            System.out.println("deleted schedule: " + schedule.getId());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } throw new RtsException(404, "Schedule not found!");

    }
}
