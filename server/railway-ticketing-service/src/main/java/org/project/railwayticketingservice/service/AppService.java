package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.*;
import org.project.railwayticketingservice.dto.app.response.AppResponse;
import org.project.railwayticketingservice.dto.app.response.NewTrainResponse;
import org.project.railwayticketingservice.dto.app.response.ReservationResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.project.railwayticketingservice.entity.*;
import org.project.railwayticketingservice.exception.RtsException;
import org.project.railwayticketingservice.repository.*;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppService {

    private final PassengerRepository passengerRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final ReservationRepository reservationRepository;
    private final TrainRepository trainRepository;
    private final Utilities utilities;

    public ResponseEntity<ReservationResponse> createReservation(NewReservationRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        Schedule schedule = scheduleRepository.findScheduleById(request.scheduleId());
        ScheduleSeat seat = scheduleSeatRepository.findByLabel(request.preferredSeat());
        Reservation possibleReservation = reservationRepository.findReservationByScheduleAndPassenger(schedule, passenger);

        // check if the schedule is full anyway
        if (!schedule.isFull()) {

            // check if passenger already has a reservation for the schedule in question
            if (possibleReservation != null) {
                throw new RtsException(409, "Reservation already exists");
            }

            // seat availability check
            if (seat != null) {
                if (!seat.isReserved()) {   // if seat is not reserved
                    seat.setReserved(true); // make the seat reserved.
                    schedule.setCurrentCapacity(schedule.getCurrentCapacity() - 1); // reduce the capacity with each reservation

                    if (schedule.getCurrentCapacity() <= 0) {   // if current capacity hits 0, tell us that schedule is full
                        schedule.setFull(true);
                    }
                    scheduleRepository.save(schedule);
                    scheduleSeatRepository.save(seat);
                } else {
                    throw new RtsException(409, "Seat is already taken!");
                }
            } else {
                throw new RtsException(404, "Seat not found!");   // seat does not exist.
            }
        } else {
            throw new RtsException(409, "Schedule is already full!");
        }

        Reservation reservation = Reservation.builder()
                .schedule(schedule)
                .passenger(passenger)
                .scheduleSeat(seat)
                .build();

        reservationRepository.save(reservation);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ReservationResponse.builder()
                    .reservationId(reservation.getId())
                    .train(reservation.getSchedule().getTrain().getName())
                    .seatNumber(seat.getLabel())
                    .time(Time.fromLocalDateTime(reservation.getSchedule().getDepartureTime()))
                    .origin(reservation.getSchedule().getOrigin())
                    .build()
        );

    }

    public ResponseEntity<ReservationResponse> getReservation(String id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        Reservation reservation = reservationRepository.findByIdAndPassenger(id, passenger);

        if (reservation != null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ReservationResponse.builder()
                            .reservationId(reservation.getId())
                            .train(reservation.getSchedule().getTrain().getName())
                            .seatNumber(reservation.getScheduleSeat().getLabel())
                            .time(Time.fromLocalDateTime(reservation.getSchedule().getDepartureTime()))
                            .origin(reservation.getSchedule().getOrigin())
                            .build()
            );

        } throw new RtsException(404, "Reservation not found!");
    }

    public ResponseEntity<List<ReservationResponse>> getAllReservations() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        List<Reservation> reservations = reservationRepository.findAllByPassenger(passenger);

        if (!reservations.isEmpty()) {

            return ResponseEntity.status(HttpStatus.OK).body(
                    reservations.stream()
                            .map(reservation -> ReservationResponse.builder()
                                    .reservationId(reservation.getId())
                                    .train(reservation.getSchedule().getTrain().getName())
                                    .seatNumber(reservation.getScheduleSeat().getLabel())
                                    .time(Time.fromLocalDateTime(reservation.getSchedule().getDepartureTime()))
                                    .origin(reservation.getSchedule().getOrigin())
                                    .build())
                            .toList()
            );
        } throw new RtsException(404, "Reservations not found!") ;
    }

    public ResponseEntity<AppResponse> deleteReservation(String id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        Reservation reservation = reservationRepository.findByIdAndPassenger(id, passenger);

        if (reservation != null) {
            reservationRepository.delete(reservation);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(AppResponse.builder()
                            .message("reservation successfully deleted")
                    .build());
        } throw new RtsException(404, "Reservation not found!");
    }

    public ResponseEntity<AppResponse> deleteAllReservations() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        List<Reservation> reservations = reservationRepository.findAllByPassenger(passenger);

        if (!reservations.isEmpty()) {
            reservationRepository.deleteAll(reservations);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(AppResponse.builder()
                            .message("reservations successfully deleted!")
                    .build());
        } throw new RtsException(404, "Reservations not found!");
    }

    public ResponseEntity<List<TrainScheduleResponse>> getTrainSchedules(String filter1, String filter2, String filter3, GetTrainScheduleRequest request) {
        List<Schedule> schedules;

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
            schedules = scheduleRepository.findSchedulesByOriginAndDestinationAndDepartureTime(request.origin(), request.destination(), request.time().getLocalDateTime());
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
                                        .origin(schedule.getOrigin())
                                        .destination(schedule.getDestination())
                                        .departureTime(Time.fromLocalDateTime(schedule.getDepartureTime()))
                                        .arrivalTime(Time.fromLocalDateTime(schedule.getArrivalTime()))
                                        .build()
                        )
                        .toList()
        );
    }

    // admin-specific method
    public ResponseEntity<AppResponse> createSchedule(ScheduleCreationRequest request) {
        Train train = trainRepository.findTrainByName(request.train());

        if (train != null) {
            // check if the schedule actually exists: name -> origin -> departure time
            if (train.getName().equals(request.train().strip())) {

                for (Schedule schedule : train.getSchedules()) {
                    // check for station of origin
                    if (request.origin().equals(schedule.getOrigin())) {
                        // check for departure times
                        if (request.departure().getLocalDateTime().equals(schedule.getDepartureTime())) {
                            throw new RtsException(409, "there is already a schedule fixed for this period.");  // try another train or time?
                        }
                    }
                }
            }
            Schedule schedule = Schedule.builder()
                    .train(train)
                    .currentCapacity(train.getCapacity())
                    .isFull(false)
                    .origin(request.origin())
                    .destination(request.destination())
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


        } else {
            throw new RtsException(400, "Schedule creation failed!\nNo such train!");
        }
    }

    // admin-specific method
    public ResponseEntity<NewTrainResponse> createNewTrain(NewTrainRequest newTrainRequest) {
        if (!trainRepository.existsByName(newTrainRequest.name())) {
            Train train = Train.builder()
                    .name(newTrainRequest.name())
                    .capacity(Long.valueOf(newTrainRequest.capacity().strip()))
                    .schedules(null)    // no schedules for now for new train
                    .build();
            trainRepository.save(train);
            System.out.println("train successfully created");
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    NewTrainResponse.builder()
                            .trainId(train.getId().toString())
                            .trainName(train.getName())
                            .capacity(train.getCapacity().toString())
                            .build()
            );

        } throw new RtsException(409, "train name already exists!");
    }

    public ResponseEntity<NewTrainResponse> getTrain(String id) {
        Train train = trainRepository.findTrainById(Long.getLong(id));

        if (train != null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    NewTrainResponse.builder()
                            .trainId(train.getId().toString())
                            .trainName(train.getName())
                            .capacity(train.getCapacity().toString())
                            .build()
            );
        } throw new RtsException(404, "Train not found!");
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
                            .origin(schedule.getOrigin())
                            .destination(schedule.getDestination())
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

        if (schedule != null) {

            // origin
            if (request.origin() != null && !Objects.equals(schedule.getOrigin(), request.origin())) {
                schedule.setOrigin(request.origin());
                System.out.println("updated origin for train " + id);
                // destination
            } if (request.destination() != null && !Objects.equals(schedule.getDestination(), request.destination())) {
                schedule.setDestination(request.destination());
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
}
