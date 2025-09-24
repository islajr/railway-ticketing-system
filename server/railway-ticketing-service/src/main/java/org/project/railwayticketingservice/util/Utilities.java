package org.project.railwayticketingservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.railwayticketingservice.entity.*;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.project.railwayticketingservice.repository.ScheduleSeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Component
public class Utilities {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;

    public void generateSeatsForSchedule(Schedule schedule) {
        List<ScheduleSeat> seats = new ArrayList<>();

        for (char row = 'A'; row <= 'Z'; row++) {
            for (int i = 1; i <= 40; i++) {
                if (seats.size() >= 1044)
                    break;
                /* ... */
                String label = row + String.valueOf(i);
                ScheduleSeat seat = new ScheduleSeat();
                seat.setLabel(label);
                seat.setReserved(false);
                seat.setSchedule(schedule);
                seat.setReservation(null);  // since seat is unoccupied for now.
                seats.add(seat);
            }
        }

        schedule.setSeats(seats);
        log.info("Successfully generated seats for schedule: {}", schedule);
        scheduleSeatRepository.saveAll(seats);
    }

    public void freeUpSeat(Reservation reservation) {
        Schedule schedule = reservation.getSchedule();
        ScheduleSeat seat = reservation.getScheduleSeat();

        schedule.setCurrentCapacity(schedule.getCurrentCapacity() + 1);
        seat.setReserved(false);
        seat.setReservation(null);

        log.info("Successfully freed up seat: {}from schedule: {}", seat.getLabel(), schedule.getId());

        scheduleRepository.save(schedule);
        scheduleSeatRepository.save(seat);
    }

    public void handleException(HttpServletResponse response, HttpServletRequest request, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("status", String.valueOf(status));
        errorDetails.put("message", message);
        errorDetails.put("timestamp", String.valueOf(Instant.now()));
        errorDetails.put("error", HttpStatus.valueOf(status).getReasonPhrase());
        errorDetails.put("path", request.getRequestURI());

        log.error(errorDetails.toString());
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorDetails));
        response.getWriter().flush();
    }

    public List<String> fileReader(String path, String subject) {

        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            List<String> dataList = new ArrayList<>();
            if (subject.equals("station")) {
                log.info("Reading data for stations");
                while ((line = reader.readLine()) != null) {
                    log.info("Station: Read data: {}", line);
                    dataList.add(line);
                }
            } else if (subject.equals("train")) {
                log.info("Reading data for trains");
                String code;
                while ((line = reader.readLine()) != null) {
                    log.info("Train: Read data: {}", line);
                    code = line.split(",")[1];  // code should be the second csv in the file
                    log.info("Train: Extracted code: {}", code);
                    dataList.add(code);
                }

            } else {
                log.error("Failed to read data: Unknown subject");
                throw new IOException("Failed to read data: Unknown subject");
            }

            log.info("Successfully read {} lines of data", dataList.size());
            return dataList;
        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Map<String, Train> updateFreeTrainCache(Map<String, Train> trainCache, LocalDateTime now) {
        Map<String, Train> freeTrainsCache = new ConcurrentHashMap<>();
        

        for (Train train : trainCache.values()) {
            List<Schedule> trainSchedules = train.getSchedules();
            
            if (!trainSchedules.isEmpty()) {
                for (Schedule schedule : trainSchedules) {
                    if (schedule.getDepartureTime().equals(now)) {
                        log.info("Schedule Refresher: IGNORE - Schedule Conflict on train:  {} for departure time: {}", train.getName(), schedule.getDepartureTime());
                    } else {
                        log.info("Schedule Refresher: Adding train: {} to the Free Trains Cache for departure time: {}", train.getName(), schedule.getDepartureTime());
                        freeTrainsCache.put(train.getName(), train);
                    }
                }
            } else {
                log.info("Schedule Refresher: Adding free train: {} with no schedules to the Free Trains Cache", train.getName());
                freeTrainsCache.put(train.getName(), train);
            }
         }

         return freeTrainsCache;
    }

    /* *
     * return two unique stations: origin and destination
     */
    public List<Station> randomStationPick(Collection<Station> stations) {
        // generate two random numbers within the range of 'stations' size

        int one = 0;
        int two = 0;

        while (one == two) {
            one = ThreadLocalRandom.current().nextInt(stations.size() - 1);
            two = ThreadLocalRandom.current().nextInt(stations.size() - 1);
        }

        List<Station> stationsList = stations.stream()
                .toList();

        Station origin = stationsList.get(one);
        Station destination = stationsList.get(two);

        log.info("Schedule Refresher: Randomly selected origin: {} and destination: {}", origin.getName(), destination.getName());

        stationsList.clear();
        stationsList.add(origin);
        stationsList.add(destination);
        return stationsList;

    }


}
