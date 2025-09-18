package org.project.railwayticketingservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.railwayticketingservice.dto.app.request.GetTrainScheduleRequest;
import org.project.railwayticketingservice.entity.Reservation;
import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.ScheduleSeat;
import org.project.railwayticketingservice.entity.Station;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.project.railwayticketingservice.repository.ScheduleSeatRepository;
import org.project.railwayticketingservice.repository.StationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class Utilities {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final StationRepository stationRepository;

    public List<Schedule> getSchedules(String filter, GetTrainScheduleRequest request){

        Station origin = stationRepository.findStationByName(request.origin());
        Station destination = stationRepository.findStationByName(request.destination());

        switch (filter) {
            case "origin" -> {
                log.info("Successfully filtered schedules by origin");
                return scheduleRepository.findSchedulesByOrigin(origin);
            }
            case "destination" -> {
                log.info("Successfully filtered schedules by destination");
                return scheduleRepository.findSchedulesByDestination(destination);
            }

            case "time" -> {
                log.info("Successfully filtered schedules by departure time");
                return scheduleRepository.findSchedulesByDepartureTime(request.time());
            }
            default -> {
                throw new RtsException(HttpStatus.BAD_REQUEST, "Invalid filter!");
            }
        }
    }

    public List<Schedule> getSchedules(String filter1, String filter2, GetTrainScheduleRequest request){

        Station origin = stationRepository.findStationByName(request.origin());
        Station destination = stationRepository.findStationByName(request.destination());

        switch (filter1) {
            case "origin" -> {     // second filter can either be destination or time
                switch (filter2) {
                    case "destination" -> {
                        log.info("Successfully filtered schedules by origin and destination");
                        return scheduleRepository.findSchedulesByOriginAndDestination(origin, destination);
                    }
                    case "time" -> {
                        log.info("Successfully filtered schedules by origin and departure time");
                        return scheduleRepository.findSchedulesByOriginAndDepartureTime(origin, request.time());
                    }
                    default -> {
                        throw new RtsException(HttpStatus.BAD_REQUEST, "Invalid second filter!");
                    }
                }

            }
            case "destination" -> {     // second filter can either be origin or time
                switch (filter2) {
                    case "origin" -> {
                        log.info("Successfully filtered schedules by destination and origin");
                        return scheduleRepository.findSchedulesByOriginAndDestination(origin, destination);
                    }
                    case "time" -> {
                        log.info("Successfully filtered schedules by destination and departure time");
                        return scheduleRepository.findSchedulesByOriginAndDepartureTime(origin, request.time());
                    }
                    default -> {
                        throw new RtsException(HttpStatus.BAD_REQUEST, "Invalid second filter!");
                    }
                }
            }
            case "time" -> {    // second filter can either be origin or destination
                switch (filter2) {
                    case "origin" -> {
                        log.info("Successfully filtered schedules by departure time and origin");
                        return scheduleRepository.findSchedulesByOriginAndDepartureTime(origin, request.time());
                    }
                    case "destination" -> {
                        log.info("Successfully filtered schedules by departure time and destination");
                        return scheduleRepository.findSchedulesByDestinationAndDepartureTime(origin, request.time());
                    }
                    default -> {
                        throw new RtsException(HttpStatus.BAD_REQUEST, "Invalid second filter!");
                    }
                }
            }
            default -> {
                throw new RtsException(HttpStatus.BAD_REQUEST, "Invalid filter!");
            }
        }
    }

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


}
