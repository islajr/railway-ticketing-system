package org.project.railwayticketingservice.task;

import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.enums.Status;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduleTasks {

    private final ScheduleRepository scheduleRepository;

    public ScheduleTasks(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /* scheduled task to mark started train tasks as 'STARTED' */
    @Scheduled(fixedRateString = "${schedule.status.scanner.interval:60000}")   // runs every minute
    private void scheduleStatusStarterTask() {

        System.out.println("***'status starter' task started ***");

        LocalDateTime now = LocalDateTime.now();

        List<Schedule> schedules = scheduleRepository.findSchedulesByDepartureTimeAfterAndStatus(now, String.valueOf(Status.NOT_STARTED));

        if (!schedules.isEmpty()) {
            for (Schedule schedule : schedules) {
                schedule.setStatus(String.valueOf(Status.STARTED));
            }
            scheduleRepository.saveAll(schedules);
            System.out.println("Marked " + schedules.size() + " schedules as 'started' at " + now);
        } else {
            System.out.println("***'status starter' task stopped ***");
        }
    }

    /* scheduled task to mark completed train schedules as 'COMPLETED'*/
    @Scheduled(fixedRateString = "${schedule.status.scanner.interval:60000}")   // runs every minute
    private void scheduleStatusCompleterTask() {

        System.out.println("***'status completer' task started ***");

        LocalDateTime now = LocalDateTime.now();

        List<Schedule> schedules = scheduleRepository.findSchedulesByDepartureTimeAfterAndStatus(now, String.valueOf(Status.STARTED));

        if (!schedules.isEmpty()) {
            for (Schedule schedule : schedules) {
                schedule.setStatus(String.valueOf(Status.COMPLETED));
            }
            scheduleRepository.saveAll(schedules);
            System.out.println("Marked " + schedules.size() + " schedules as 'completed' at " + now);
        } else {
            System.out.println("***'status completer' task stopped ***");
        }
    }
}
