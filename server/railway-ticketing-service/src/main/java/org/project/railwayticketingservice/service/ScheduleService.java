package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.GetTrainScheduleRequest;
import org.project.railwayticketingservice.dto.app.request.ScheduleCreationRequest;
import org.project.railwayticketingservice.dto.app.request.ScheduleUpdateRequest;
import org.project.railwayticketingservice.dto.app.response.AppResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.project.railwayticketingservice.entity.*;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.project.railwayticketingservice.repository.ReservationRepository;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.project.railwayticketingservice.repository.StationRepository;
import org.project.railwayticketingservice.repository.TrainRepository;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
        Train train = trainRepository.findTrainByName(request.train()).orElseThrow(
                () -> new RtsException(HttpStatus.BAD_REQUEST, "Schedule creation failed! No such train"));

        if (train.getName().equals(request.train().strip())) {

            for (Schedule schedule : train.getSchedules()) {
                // check for station of origin
                if (request.origin().equals(schedule.getOrigin().toString())) {
                    // check for departure times
                    if (request.departure().equals(schedule.getDepartureTime())) {
                        throw new RtsException(HttpStatus.CONFLICT, "there is already a schedule fixed for this period.");  // try another train or time?
                    }
                }
            }
        } else {
            throw new RtsException(HttpStatus.NOT_FOUND, "Train not found");
        }

        // convert to station
        Station origin = stationRepository.findStationByName(request.origin());
        Station destination = stationRepository.findStationByName(request.destination());

        if (origin == null || destination == null) {
            throw new RtsException(HttpStatus.BAD_REQUEST, "Please input a valid station");
        } else {

            Schedule schedule = Schedule.builder()
                    .train(train)
                    .currentCapacity(train.getCapacity())
                    .isFull(false)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(request.departure())
                    .arrivalTime(request.arrival())
                    .status(String.valueOf(request.status()))
                    .build();

            scheduleRepository.save(schedule);
            System.out.println("schedule successfully created");

            // generating seats for schedule
            utilities.generateSeatsForSchedule(schedule);
            System.out.println("seats generated");

            return ResponseEntity.status(HttpStatus.CREATED).body(AppResponse.builder()
                    .message("schedule successfully created.")
                    .build());
        }


    }

    public ResponseEntity<TrainScheduleResponse> getTrainSchedule(String id) {
        Schedule schedule = scheduleRepository.findScheduleById(id);

        if (schedule == null) {
            throw new RtsException(HttpStatus.NOT_FOUND, "Schedule not found!");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    TrainScheduleResponse.fromSchedule(schedule)
            );
        }
    }

    public ResponseEntity<TrainScheduleResponse> editTrainSchedule(String id, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findScheduleById(id);
        Station origin = stationRepository.findStationByName(request.origin());
        Station destination = stationRepository.findStationByName(request.destination());

        if (schedule == null) {
            throw new RtsException(HttpStatus.NOT_FOUND, "Schedule not found!");
        } else {
            /* TODO:
             *  find a more efficient way to only write to DB once
             *  handle method more efficiently generally
            */

            boolean changed = false;

            // origin
            if (!Objects.equals(request.origin(), "null") && !Objects.equals(schedule.getOrigin(), origin)) {
                schedule.setOrigin(origin);
                scheduleRepository.save(schedule);
                changed = true;
                System.out.println("updated origin for train " + id);
                // destination
            } if (!Objects.equals(request.destination(), "null") && !Objects.equals(schedule.getDestination(), destination)) {
                schedule.setDestination(destination);
                scheduleRepository.save(schedule);
                changed = true;
                System.out.println("updated destination for train " + id);
            }   // departure
            if (request.departureTime() != null && !Objects.equals(request.departureTime(), Time.fromLocalDateTime(schedule.getDepartureTime()))) {
                schedule.setDepartureTime(request.departureTime());
                scheduleRepository.save(schedule);
                changed = true;
                System.out.println("updated departure time for train " + id);
            }   // arrival
            if (request.arrivalTime() != null && !Objects.equals(request.arrivalTime(), Time.fromLocalDateTime(schedule.getArrivalTime()))) {
                schedule.setArrivalTime(request.arrivalTime());
                scheduleRepository.save(schedule);
                changed = true;
                System.out.println("updated arrival time for train " + id);
            }   // status
            if (request.status() != null && !Objects.equals(String.valueOf(request.status()), schedule.getStatus())) {
                schedule.setStatus(String.valueOf(request.status()));
                scheduleRepository.save(schedule);
                changed = true;
                System.out.println("updated status for train " + id);
            }

            if (changed) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        TrainScheduleResponse.fromSchedule(schedule)
                );
            } else {
                throw new RtsException(HttpStatus.NOT_MODIFIED, "Nothing to update!");
            }

        }

    }

    public ResponseEntity<List<TrainScheduleResponse>> getTrainSchedules(String filter1, String filter2, String filter3, GetTrainScheduleRequest request) {
        List<Schedule> schedules;
        Station origin = stationRepository.findStationByName(request.origin());
        Station destination = stationRepository.findStationByName(request.destination());

        if (filter3.equals("null")) {   // if filter3 is null

            // check filter2
            if (filter2.equals("null")) {   // if filter2 is null

                if (!filter1.equals("null")) {
                    schedules = utilities.getSchedules(filter1, request);
                } else {
                    throw new RtsException(HttpStatus.BAD_REQUEST, "Filters cannot be null!");
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
                                TrainScheduleResponse::fromSchedule
                        )
                        .toList()
        );
    }

    public ResponseEntity<AppResponse> deleteTrainSchedule(String id) {
        Schedule schedule = scheduleRepository.findScheduleById(id);

        if (schedule == null) {
            throw new RtsException(HttpStatus.NOT_FOUND, "Schedule not found!");
        } else {

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

        }

    }
}
