package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    Schedule findScheduleById(String id);

    List<Schedule> findSchedulesByOrigin(String origin);

    List<Schedule> findSchedulesByDestination(String destination);

    List<Schedule> findSchedulesByDepartureTime(LocalDateTime departureTime);

    List<Schedule> findSchedulesByOriginAndDestination(String origin, String destination);

    List<Schedule> findSchedulesByOriginAndDepartureTime(String origin, LocalDateTime localDateTime);

    List<Schedule> findSchedulesByDestinationAndDepartureTime(String origin, LocalDateTime localDateTime);

    List<Schedule> findSchedulesByOriginAndDestinationAndDepartureTime(String origin, String destination, LocalDateTime departureTime);
}
