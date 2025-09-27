package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.railwayticketingservice.dto.app.request.GetTrainScheduleRequest;
import org.project.railwayticketingservice.dto.app.request.ScheduleCreationRequest;
import org.project.railwayticketingservice.dto.app.request.ScheduleUpdateRequest;
import org.project.railwayticketingservice.dto.app.response.AppResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.ScheduleSeat;
import org.project.railwayticketingservice.entity.Station;
import org.project.railwayticketingservice.entity.Train;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.project.railwayticketingservice.repository.ReservationRepository;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.project.railwayticketingservice.repository.StationRepository;
import org.project.railwayticketingservice.repository.TrainRepository;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final ReservationRepository reservationRepository;
    private final Utilities utilities;

    // admin-specific method
    @Cacheable(value = "schedules")
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

        // checking that arrival > departure
        if (request.arrival().isBefore(request.departure())) {
            throw new RtsException(HttpStatus.CONFLICT, "arrival time cannot be before departure time.");
        }

        // convert to station
        Station origin = stationRepository.findStationByName(request.origin()).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "Provided origin does not exist"));
        Station destination = stationRepository.findStationByName(request.destination()).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "Provided destination does not exist"));

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

            // generating seats for schedule
            utilities.generateSeatsForSchedule(schedule);

            log.info("Schedule with id: {} successfully created", schedule.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(AppResponse.builder()
                    .message("schedule successfully created.")
                    .build());
        }


    }

    @Cacheable(value = "schedules", key = "#id")
    public ResponseEntity<TrainScheduleResponse> getTrainSchedule(String id) {
        Schedule schedule = scheduleRepository.findScheduleById(id);

        if (schedule == null) {
            throw new RtsException(HttpStatus.NOT_FOUND, "Schedule not found!");
        } else {
            log.info("Schedule with id: {} successfully retrieved", id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    TrainScheduleResponse.fromSchedule(schedule, null)
            );
        }
    }

    @Cacheable(value = "schedules", key = "#id")
    public ResponseEntity<TrainScheduleResponse> editTrainSchedule(String id, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findScheduleById(id);
        Station origin;
        Station destination;

        if (schedule == null) {
            throw new RtsException(HttpStatus.NOT_FOUND, "Schedule not found!");
        } else {
            /* TODO:
             *  find a more efficient way to only write to DB once
             *  handle method more efficiently generally
            */

            boolean changed = false;

            // origin
            if (!Objects.equals(request.origin(), "null")) {
                origin = stationRepository.findStationByName(request.origin()).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "Provided origin does not exist"));
                if (!Objects.equals(schedule.getOrigin(), origin)) {
                    schedule.setOrigin(origin);
                    scheduleRepository.save(schedule);
                    changed = true;
                    log.info("Updated origin for train {}", id);
                }
                
            }   // destination 
            if (!Objects.equals(request.destination(), "null")) {
                destination = stationRepository.findStationByName(request.destination()).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "Provided destination does not exist"));
                if (!Objects.equals(schedule.getDestination(), destination)) {
                    schedule.setDestination(destination);
                    scheduleRepository.save(schedule);
                    changed = true;
                    log.info("Updated destination for train {}", id);
                }
            }   // departure
            /* if (request.departureTime() != null && !Objects.equals(request.departureTime(), schedule.getDepartureTime())) {
                schedule.setDepartureTime(request.departureTime());
                scheduleRepository.save(schedule);
                changed = true;
                log.info("Updated departure time for train {}", id);
            }   // arrival
            if (request.arrivalTime() != null && !Objects.equals(request.arrivalTime(), schedule.getArrivalTime())) {
                schedule.setArrivalTime(request.arrivalTime());
                scheduleRepository.save(schedule);
                changed = true;
                log.info("Updated arrival time for train {}", id);
            }   // status */
            if (request.status() != null && !Objects.equals(String.valueOf(request.status()), schedule.getStatus())) {
                schedule.setStatus(String.valueOf(request.status()));
                scheduleRepository.save(schedule);
                changed = true;
                log.info("Updated status for train {}", id);
            }

            if (changed) {
                log.info("Successfully edited schedule for train {}", id);
                return ResponseEntity.status(HttpStatus.OK).body(
                        TrainScheduleResponse.fromSchedule(schedule, null)
                );
            } else {
                throw new RtsException(HttpStatus.NOT_MODIFIED, "Nothing to update!");
            }

        }

    }
    @Cacheable(value = "schedules")
    public ResponseEntity<List<TrainScheduleResponse>> getTrainSchedules(int page, int size, String sortBy, String order, GetTrainScheduleRequest request) {

        Sort sort = order.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Schedule> schedules;
        Station origin;
        Station destination;

        if (request.origin() != null && request.destination() != null) {  // search by origin and destination
            origin = stationRepository.findStationByName(request.destination()).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "requested origin does not exist"));
            destination = stationRepository.findStationByName(request.destination()).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "requested destination does not exist"));
            schedules = scheduleRepository.findSchedulesByOriginAndDestination(origin, destination, pageable);
        } else if (request.origin() == null && request.destination() != null) {   // search only by destination
            destination = stationRepository.findStationByName(request.destination()).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "requested destination does not exist"));
            schedules = scheduleRepository.findSchedulesByDestination(destination, pageable);

        } else if (request.origin() != null) {   // search only by origin
            origin = stationRepository.findStationByName(request.origin()).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "requested origin does not exist"));
            schedules = scheduleRepository.findSchedulesByOrigin(origin, pageable);

        } else {    // search by nothing?
            throw new RtsException(HttpStatus.BAD_REQUEST, "no filters specified");
        }

        // convert schedules to proper response DTOs

        log.info("Successfully retrieved {} train schedules", schedules.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK).body(
                schedules.stream()
                        .map(
                                schedule -> TrainScheduleResponse.fromSchedule(schedule, schedules)
                        )
                        .toList()
        );
    }

    @CacheEvict(value = "schedules", key = "#id")
    public ResponseEntity<AppResponse> deleteTrainSchedule(String id) {
        Schedule schedule = scheduleRepository.findScheduleById(id);

        if (schedule == null) {
            throw new RtsException(HttpStatus.NOT_FOUND, "Schedule not found!");
        } else {

            // delete all reservations
            for (ScheduleSeat seat : schedule.getSeats()) {
                if (seat.isReserved() && seat.getReservation() != null) {
                    reservationRepository.delete(seat.getReservation());
                    log.info("Reservation with id: {} successfully deleted", seat.getReservation().getId());
                }
            }

            // delete schedule
            scheduleRepository.delete(schedule);

            log.info("Successfully deleted schedule: {}", schedule.getId());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }

    }


}
