package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.GetTrainScheduleRequest;
import org.project.railwayticketingservice.dto.app.request.NewReservationRequest;
import org.project.railwayticketingservice.dto.app.response.AppResponse;
import org.project.railwayticketingservice.dto.app.response.ReservationResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.project.railwayticketingservice.entity.*;
import org.project.railwayticketingservice.exception.RtsException;
import org.project.railwayticketingservice.repository.PassengerRepository;
import org.project.railwayticketingservice.repository.ReservationRepository;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.project.railwayticketingservice.repository.ScheduleSeatRepository;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PassengerRepository passengerRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final Utilities utilities;

    public ResponseEntity<ReservationResponse> createReservation(NewReservationRequest request) {

        String email = ((PassengerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
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
        seat.setReservation(reservation);
        scheduleSeatRepository.save(seat);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ReservationResponse.builder()
                        .reservationId(reservation.getId())
                        .train(reservation.getSchedule().getTrain().getName())
                        .seatNumber(seat.getLabel())
                        .time(Time.fromLocalDateTime(reservation.getSchedule().getDepartureTime()))
                        .origin(reservation.getSchedule().getOrigin().toString())
                        .build()
        );

    }

    public ResponseEntity<ReservationResponse> getReservation(String id) {

        String email = ((PassengerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        Reservation reservation = reservationRepository.findByIdAndPassenger(id, passenger);

        if (reservation != null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ReservationResponse.builder()
                            .reservationId(reservation.getId())
                            .train(reservation.getSchedule().getTrain().getName())
                            .seatNumber(reservation.getScheduleSeat().getLabel())
                            .time(Time.fromLocalDateTime(reservation.getSchedule().getDepartureTime()))
                            .origin(reservation.getSchedule().getOrigin().toString())
                            .build()
            );

        } throw new RtsException(404, "Reservation not found!");
    }

    public ResponseEntity<List<ReservationResponse>> getAllReservations() {

        String email = ((PassengerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
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
                                    .origin(reservation.getSchedule().getOrigin().toString())
                                    .build())
                            .toList()
            );
        } throw new RtsException(404, "Reservations not found!") ;
    }

    public ResponseEntity<AppResponse> deleteReservation(String id) {
        String email = ((PassengerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        Reservation reservation = reservationRepository.findByIdAndPassenger(id, passenger);

        if (reservation != null) {
            /*reservation.getSchedule().setCurrentCapacity(reservation.getSchedule().getCurrentCapacity() + 1);
            reservation.getScheduleSeat().setReserved(false);
            reservation.getScheduleSeat().setReservation(null);
            reservationRepository.save(reservation);*/  // attempt at freeing up reserved seats
            reservationRepository.delete(reservation);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(AppResponse.builder()
                    .message("reservation successfully deleted")
                    .build());
        } throw new RtsException(404, "Reservation not found!");
    }

    public ResponseEntity<AppResponse> deleteAllReservations() {
        String email = ((PassengerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        List<Reservation> reservations = reservationRepository.findAllByPassenger(passenger);
        // make seats available again

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
