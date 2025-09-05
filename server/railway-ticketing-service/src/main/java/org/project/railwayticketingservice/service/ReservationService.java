package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.NewReservationRequest;
import org.project.railwayticketingservice.dto.app.request.ReservationUpdateRequest;
import org.project.railwayticketingservice.dto.app.response.AppResponse;
import org.project.railwayticketingservice.dto.app.response.ReservationResponse;
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
import java.util.Objects;

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
        ScheduleSeat seat = scheduleSeatRepository.findByScheduleAndLabel(schedule, request.preferredSeat());
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
                        .origin(reservation.getSchedule().getOrigin().getName())
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
                            .origin(reservation.getSchedule().getOrigin().getName())
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
                                    .origin(reservation.getSchedule().getOrigin().getName())
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
            utilities.freeUpSeat(reservation);
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
            for (Reservation reservation : reservations) {
                utilities.freeUpSeat(reservation);
            }
            reservationRepository.deleteAll(reservations);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(AppResponse.builder()
                    .message("reservations successfully deleted!")
                    .build());
        } throw new RtsException(404, "Reservations not found!");
    }

    public ResponseEntity<ReservationResponse> updateReservation(String id, ReservationUpdateRequest request) {

        /* allow passengers to change their selected seats for a start. */
        Reservation reservation = reservationRepository.findReservationById(id)
                .orElseThrow(() -> new RtsException(404, "Reservation does not exist"));

        if (!Objects.equals(reservation.getScheduleSeat().getLabel(), request.preferredSeat())) {   // check for seat mismatch

            // check for availability
            ScheduleSeat newSeat = scheduleSeatRepository.findByScheduleAndLabel(reservation.getSchedule(), request.preferredSeat().toUpperCase().strip());

            if (newSeat != null) {
                if ((!newSeat.isReserved()) && newSeat.getReservation() == null) {    // if seat is free

                    // de-allocate previous seat
                    utilities.freeUpSeat(reservation);

                    Schedule schedule = reservation.getSchedule();

                    schedule.setCurrentCapacity(schedule.getCurrentCapacity() - 1);
                    newSeat.setReserved(true);
                    newSeat.setReservation(reservation);
                    scheduleRepository.save(schedule);
                    scheduleSeatRepository.save(newSeat);

                    reservation.setScheduleSeat(newSeat);
                    reservationRepository.save(reservation);
                    System.out.println("assigned new seat: " + newSeat.getLabel() + " to reservation: " + reservation.getId() + ".");

                    return ResponseEntity.ok(ReservationResponse.builder()
                                    .reservationId(reservation.getId())
                                    .time(Time.fromLocalDateTime(reservation.getSchedule().getDepartureTime()))
                                    .train(reservation.getSchedule().getTrain().getName())
                                    .origin(reservation.getSchedule().getOrigin().getName())
                                    .seatNumber(reservation.getScheduleSeat().getLabel())
                            .build());
                } throw new RtsException(409, "Seat is already taken!");
            } throw new RtsException(404, "No such seat");
        } throw new RtsException(400, "nothing to update");
    }
}
