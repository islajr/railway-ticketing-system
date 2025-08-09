package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.Passenger;
import org.project.railwayticketingservice.entity.Reservation;
import org.project.railwayticketingservice.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    Reservation findByIdAndPassenger(String id, Passenger passenger);

    List<Reservation> findAllByIdAndPassenger(String id, Passenger passenger);
    List<Reservation> findAllByPassenger(Passenger passenger);

    Reservation findReservationByScheduleAndPassenger(Schedule schedule, Passenger passenger);

    Optional<Reservation> findReservationById(String s);
}
