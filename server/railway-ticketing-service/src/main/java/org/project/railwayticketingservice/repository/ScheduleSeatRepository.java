package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.Reservation;
import org.project.railwayticketingservice.entity.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Long> {

    Optional<ScheduleSeat> findById(Long id);
    ScheduleSeat findByLabel(String label);

    ScheduleSeat findByReservation(Reservation reservation);

    List<ScheduleSeat> findScheduleSeatsByReservation(Reservation reservation);
}
