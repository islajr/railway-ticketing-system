package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.railwayticketingservice.dto.app.request.NewReservationRequest;
import org.project.railwayticketingservice.dto.app.request.ReservationUpdateRequest;
import org.project.railwayticketingservice.dto.app.response.AppResponse;
import org.project.railwayticketingservice.dto.app.response.ReservationResponse;
import org.project.railwayticketingservice.entity.*;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.project.railwayticketingservice.repository.PassengerRepository;
import org.project.railwayticketingservice.repository.ReservationRepository;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.project.railwayticketingservice.repository.ScheduleSeatRepository;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
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

            // check if the schedule is already active
            if (schedule.getStatus().equals("COMPLETED") || schedule.getStatus().equals("STARTED"))
                throw new RtsException(HttpStatus.CONFLICT, "Schedule already completed");

            // check if passenger already has a reservation for the schedule in question
            if (possibleReservation != null) {
                throw new RtsException(HttpStatus.CONFLICT, "Reservation already exists");
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
                    throw new RtsException(HttpStatus.CONFLICT, "Seat is already taken!");
                }
            } else {
                throw new RtsException(HttpStatus.NOT_FOUND, "Seat not found!");   // seat does not exist.
            }
        } else {
            throw new RtsException(HttpStatus.CONFLICT, "Schedule is already full!");
        }

        Reservation reservation = Reservation.builder()
                .schedule(schedule)
                .passenger(passenger)
                .scheduleSeat(seat)
                .build();

        reservationRepository.save(reservation);
        seat.setReservation(reservation);
        scheduleSeatRepository.save(seat);

        log.info("Reservation with id: {} created for passenger: {}", reservation.getId(), passenger.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ReservationResponse.from(reservation, null)
        );

    }

    @Cacheable(value = "reservations", key = "#id")
    public ResponseEntity<ReservationResponse> getReservation(String id) {

        String email = ((PassengerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        Reservation reservation = reservationRepository.findByIdAndPassenger(id, passenger);

        if (reservation == null) {
            throw new RtsException(HttpStatus.NOT_FOUND, "Reservation not found!");
        } else {
            log.info("Reservation with id: {} found", id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ReservationResponse.from(reservation, null)
            );

        }
    }

    @Cacheable(value = "reservations")
    public ResponseEntity<List<ReservationResponse>> getAllReservations(int page, int size, String sortBy, String direction) {

        String email = ((PassengerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        Sort sort = direction.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Reservation> reservations = reservationRepository.findAllReservationsByPassenger(passenger, pageable);

        log.info("Successfully retrieved all reservations for passenger: {}", passenger.getEmail());
        log.info("Reservation count: {}", reservations.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK).body(
                reservations.stream()
                        .map(reservation -> ReservationResponse.from(reservation, reservations))
                        .toList()
        );
    }

    @CacheEvict(value = "reservations", key = "#id")
    public ResponseEntity<AppResponse> deleteReservation(String id) {
        String email = ((PassengerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        Reservation reservation = reservationRepository.findByIdAndPassenger(id, passenger);

        if (reservation == null) {
            throw new RtsException(HttpStatus.NOT_FOUND, "Reservation not found!");
        } else {
            utilities.freeUpSeat(reservation);
            reservationRepository.delete(reservation);
            log.info("Reservation with id: {} deleted", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(AppResponse.builder()
                    .message("reservation successfully deleted")
                    .build());
        }
    }

    @CacheEvict(value = "reservations", allEntries = true)
    public ResponseEntity<AppResponse> deleteAllReservations() {
        String email = ((PassengerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        List<Reservation> reservations = reservationRepository.findAllByPassenger(passenger);
        // make seats available again

        if (reservations.isEmpty()) {
            throw new RtsException(HttpStatus.NOT_FOUND, "Reservations not found!");
        } else {
            for (Reservation reservation : reservations) {
                utilities.freeUpSeat(reservation);
                log.info("Reservation with id: {} deleted", reservation.getId());
            }
            reservationRepository.deleteAll(reservations);
            log.info("Successfully deleted all reservations for passenger: {}", passenger.getEmail());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(AppResponse.builder()
                    .message("reservations successfully deleted!")
                    .build());
        }
    }

    @CachePut(value = "reservations", key = "#id")
    public ResponseEntity<ReservationResponse> updateReservation(String id, ReservationUpdateRequest request) {

        /* allow passengers to change their selected seats for a start. */
        Reservation reservation = reservationRepository.findReservationById(id)
                .orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "Reservation does not exist"));

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

                    log.info("assigned new seat: {} to reservation: {}.", newSeat.getLabel(), reservation.getId());
                    return ResponseEntity.ok(ReservationResponse.from(reservation, null));
                } throw new RtsException(HttpStatus.CONFLICT, "Seat is already taken!");
            } throw new RtsException(HttpStatus.NOT_FOUND, "No such seat");
        } throw new RtsException(HttpStatus.BAD_REQUEST, "nothing to update");
    }
}
