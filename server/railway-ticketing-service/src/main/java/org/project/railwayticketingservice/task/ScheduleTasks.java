package org.project.railwayticketingservice.task;

import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.enums.Status;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ScheduleTasks {

    private final ScheduleRepository scheduleRepository;

    public ScheduleTasks(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /* scheduled task to mark started train tasks as 'STARTED' */
    @Scheduled(fixedRateString = "${schedule.status.scanner.interval:60000}")   // runs every minute
    private void scheduleStatusStarterTask() {

        log.info("TASK: *** 'status starter' task started ***");

        LocalDateTime now = LocalDateTime.now();

        List<Schedule> schedules = scheduleRepository.findSchedulesByDepartureTimeBeforeAndStatus(now, String.valueOf(Status.NOT_STARTED));

        if (!schedules.isEmpty()) {
            for (Schedule schedule : schedules) {
                schedule.setStatus(String.valueOf(Status.STARTED));
            }
            scheduleRepository.saveAll(schedules);
            log.info("TASK: Marked " + schedules.size() + " schedules as 'started' at " + now);
        } else {
            log.info("TASK: *** 'status starter' task stopped ***");
        }
    }

    /* scheduled task to mark completed train schedules as 'COMPLETED'*/
    @Scheduled(fixedRateString = "${schedule.status.scanner.interval:60000}")   // runs every minute
    private void scheduleStatusCompleterTask() {

        log.info("TASK: *** 'status completer' task started ***");

        LocalDateTime now = LocalDateTime.now();

        List<Schedule> schedules = scheduleRepository.findSchedulesByArrivalTimeBeforeAndStatus(now, String.valueOf(Status.STARTED));

        if (!schedules.isEmpty()) {
            for (Schedule schedule : schedules) {
                schedule.setStatus(String.valueOf(Status.COMPLETED));
            }
            scheduleRepository.saveAll(schedules);
            log.info("TASK: *** Marked " + schedules.size() + " schedules as 'completed' at " + now + " ***");
        } else {
            log.info("TASK: *** 'status completer' task stopped ***");
        }
    }
}
