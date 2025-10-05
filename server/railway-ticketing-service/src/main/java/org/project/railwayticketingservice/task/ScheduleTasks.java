package org.project.railwayticketingservice.task;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.Station;
import org.project.railwayticketingservice.entity.Train;
import org.project.railwayticketingservice.entity.enums.Status;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.project.railwayticketingservice.repository.StationRepository;
import org.project.railwayticketingservice.repository.TrainRepository;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ScheduleTasks {

    private final ScheduleRepository scheduleRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final Utilities utilities;

    public ScheduleTasks(ScheduleRepository scheduleRepository, TrainRepository trainRepository, StationRepository stationRepository, Utilities utilities) {
        this.scheduleRepository = scheduleRepository;
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.utilities = utilities;
    }

    /* train and station cache */
    Map<String, Station> stationCache = new ConcurrentHashMap<>();
    Map<String, Train> trainCache = new ConcurrentHashMap<>();
    Map<String, Train> freeTrainsCache = new ConcurrentHashMap<>();
    Map<String, Schedule> dailySchedules = new ConcurrentHashMap<>();
    Map<String, Schedule> ongoingSchedulesCache = new ConcurrentHashMap<>();

    /* scheduled task to mark started train tasks as 'STARTED' */
    @Scheduled(fixedRateString = "${schedule.status.scanner.interval:60000}")   // runs every minute
    protected void scheduleStatusStarterTask() {

        log.info("TASK: *** 'status starter' task started ***");

        LocalDateTime now = LocalDateTime.now();

        List<Schedule> schedules = utilities.getOngoingSchedules(dailySchedules, now);

        if (!schedules.isEmpty()) {
            for (Schedule schedule : schedules) {
                schedule.setStatus(String.valueOf(Status.STARTED));
                ongoingSchedulesCache.put(schedule.getId(), schedule);
                dailySchedules.replace(schedule.getId(), schedule);     // update the original cache
            }

            scheduleRepository.saveAll(schedules);  // write the changes to db to ensure idempotency.
            log.info("TASK: Marked {} schedules 'started' as at {} ", schedules.size(), now);
        } else {
            log.info("TASK: *** 'status starter' task stopped ***");
        }
    }

    /* scheduled task to mark completed train schedules as 'COMPLETED'*/
    @Scheduled(fixedRateString = "${schedule.status.scanner.interval:60000}")   // runs every minute
    protected void scheduleStatusCompleterTask() {

        log.info("TASK: *** 'status completer' task started ***");

        LocalDateTime now = LocalDateTime.now();

        ongoingSchedulesCache.forEach((id, schedule) -> {
            if (schedule.getArrivalTime().isBefore(now)) {
                schedule.setStatus("COMPLETED");
                dailySchedules.replace(schedule.getId(), schedule);     // update original cache
                scheduleRepository.save(schedule);  // persist to db for consistency
                log.info("Status Completer: Marked schedule {} as completed", schedule.getId());
            }
        });

        log.info("TASK: *** 'status completer' task stopped ***");
    }

    /* task to periodically refresh schedule, train and station caches for the day - runs every day at midnight */
    @Scheduled(cron = "0 0 0 * * *") // everyday at midnight
    public void cacheUpdater() {
        log.info("TASK: *** 'Cache Updater' task started ***");
        LocalDateTime now = LocalDateTime.now();

        log.info("Cache Updater: Querying DB for train stations");
        List<Station> stations = stationRepository.findAll();

        log.info("Cache Updater: Querying DB for trains");
        List<Train> trains = trainRepository.findAll();

        log.info("Cache Updater: Querying DB for schedules for the day");
        List<Schedule> schedules = scheduleRepository.findSchedulesByDepartureTimeBetween(now, now.plusHours(24));

        if (!schedules.isEmpty()) {
            log.info("Cache Updater: Updating schedules cache");
            dailySchedules.clear();     // empty cache initially
            for (Schedule schedule : schedules) {
                dailySchedules.put(schedule.getId(), schedule);
            }

            log.info("Cache Updater: Successfully updated schedules cache");
        } else {
            log.warn("Cache Updater: Nothing to update - No schedules for the day");
        }

        if (!stations.isEmpty()) {
            log.info("Cache Updater: Updating station cache");
            for (Station station : stations) {
                stationCache.put(station.getCode(), station);
            }

            log.info("Cache Updater: Successfully updated station cache");
        } else {
            log.warn("Cache Updater: Nothing to update - No stations found");
        }

        if (!trains.isEmpty()) {
            log.info("Cache Updater: Updating train cache");
            for (Train train : trains) {
                trainCache.put(train.getName(), train);
            }

            log.info("Cache Updater: Successfully updated train cache");
        } else {
            log.warn("Cache Updater: Nothing to update - No trains found");
        }

        log.info("TASK: *** 'Cache Updater' task completed");
    }

    /* task to intermittently create new train schedules */
    @Transactional
    @Scheduled(cron="0 0/15 * * * *") // every 15 minutes
    protected void scheduleRefresher() {
        LocalDateTime now = LocalDateTime.now();
        log.info("TASK: *** 'Schedule Refresher' task started");

        if (trainCache.isEmpty() || stationCache.isEmpty()) {
            log.info("Schedule Refresher: No stored trains and stations. Triggering 'Cache Updater'");
            cacheUpdater();
        }

        /* *
         * need to know the free trains at a specific time - update free trains cache from trainsCache before use
         * randomize origin and destination selection from cache
         * 
         */

         log.info("Schedule Refresher: Clearing Free Trains Cache.");
         freeTrainsCache.clear();   // clear freeTrainsCache
         
         log.info("Schedule Refresher: Updating Free Trains Cache.");
         freeTrainsCache = utilities.updateFreeTrainCache(trainCache, now); // update free train cache

         List<Station> randomStations;
         now = now.plusDays(1);
         for (Train train : freeTrainsCache.values()) {
            log.info("Schedule Refresher: Selecting random stations for schedule origin and destination");
            randomStations = utilities.randomStationPick(stationCache.values());

             Schedule schedule = Schedule.builder()
             .train(train)
             .currentCapacity(1040L)
             .isFull(false)
             .origin(randomStations.getFirst())
             .destination(randomStations.getLast())
             .departureTime(now)
             .arrivalTime(randomStations.getFirst().getLGA().equals(randomStations.getLast().getLGA()) ? now.plusMinutes(15) : now.plusMinutes(30))
             .status(String.valueOf(Status.NOT_STARTED))
             .build();

             scheduleRepository.save(schedule);
             log.info("Schedule Refresher: Generated new schedule for train {} from {} to {} at {}", 
             train.getName(), schedule.getOrigin().getName(), schedule.getDestination().getName(), schedule.getDepartureTime().toString());

             utilities.generateSeatsForSchedule(schedule);

         }


        log.info("TASK: *** 'Schedule Refresher' task completed");

    }
}
