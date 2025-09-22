package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    Schedule findScheduleById(String id);

    Page<Schedule> findSchedulesByOrigin(Station origin, Pageable pageable);

    Page<Schedule> findSchedulesByDestination(Station destination, Pageable pageable);

    List<Schedule> findSchedulesByDepartureTime(LocalDateTime departureTime);

    Page<Schedule> findSchedulesByOriginAndDestination(Station origin, Station destination, Pageable pageable);

    List<Schedule> findSchedulesByOriginAndDepartureTime(Station origin, LocalDateTime localDateTime);

    List<Schedule> findSchedulesByDestinationAndDepartureTime(Station origin, LocalDateTime localDateTime);

    List<Schedule> findSchedulesByOriginAndDestinationAndDepartureTime(Station origin, Station destination, LocalDateTime departureTime);

    List<Schedule> findSchedulesByDepartureTimeBetween(LocalDateTime upper, LocalDateTime lower);

    List<Schedule> findSchedulesByDepartureTimeAfterAndStatus(LocalDateTime now, String status);
}
