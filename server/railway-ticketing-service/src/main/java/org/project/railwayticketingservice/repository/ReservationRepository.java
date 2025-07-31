package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.Passenger;
import org.project.railwayticketingservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    Reservation findByIdAndPassenger(String id, Passenger passenger);

    List<Reservation> findAllByIdAndPassenger(String id, Passenger passenger);
    List<Reservation> findAllByPassenger(Passenger passenger);
}
