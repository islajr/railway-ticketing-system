package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    Schedule findScheduleById(String id);

    List<Schedule> findSchedulesByOrigin(Station origin);

    List<Schedule> findSchedulesByDestination(Station destination);

    List<Schedule> findSchedulesByDepartureTime(LocalDateTime departureTime);

    List<Schedule> findSchedulesByOriginAndDestination(Station origin, Station destination);

    List<Schedule> findSchedulesByOriginAndDepartureTime(Station origin, LocalDateTime localDateTime);

    List<Schedule> findSchedulesByDestinationAndDepartureTime(Station origin, LocalDateTime localDateTime);

    List<Schedule> findSchedulesByOriginAndDestinationAndDepartureTime(Station origin, Station destination, LocalDateTime departureTime);
}
