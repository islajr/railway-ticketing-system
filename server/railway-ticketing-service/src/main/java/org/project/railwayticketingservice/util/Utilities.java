package org.project.railwayticketingservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.GetTrainScheduleRequest;
import org.project.railwayticketingservice.entity.Reservation;
import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.ScheduleSeat;
import org.project.railwayticketingservice.entity.Station;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.project.railwayticketingservice.repository.ScheduleSeatRepository;
import org.project.railwayticketingservice.repository.StationRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                return scheduleRepository.findSchedulesByOrigin(origin);
            }
            case "destination" -> {
                return scheduleRepository.findSchedulesByDestination(destination);
            }

            case "time" -> {
                return scheduleRepository.findSchedulesByDepartureTime(request.time().getLocalDateTime());
            }
            default -> {
                throw new RtsException(400, "Invalid filter!", Instant.now().toString());
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
                        return scheduleRepository.findSchedulesByOriginAndDestination(origin, destination);
                    }
                    case "time" -> {
                        return scheduleRepository.findSchedulesByOriginAndDepartureTime(origin, request.time().getLocalDateTime());
                    }
                    default -> {
                        throw new RtsException(400, "Invalid second filter!", Instant.now().toString());
                    }
                }

            }
            case "destination" -> {     // second filter can either be origin or time
                switch (filter2) {
                    case "origin" -> {
                        return scheduleRepository.findSchedulesByOriginAndDestination(origin, destination);
                    }
                    case "time" -> {
                        return scheduleRepository.findSchedulesByOriginAndDepartureTime(origin, request.time().getLocalDateTime());
                    }
                    default -> {
                        throw new RtsException(400, "Invalid second filter!", Instant.now().toString());
                    }
                }
            }
            case "time" -> {    // second filter can either be origin or destination
                switch (filter2) {
                    case "origin" -> {
                        return scheduleRepository.findSchedulesByOriginAndDepartureTime(origin, request.time().getLocalDateTime());
                    }
                    case "destination" -> {
                        return scheduleRepository.findSchedulesByDestinationAndDepartureTime(origin, request.time().getLocalDateTime());
                    }
                    default -> {
                        throw new RtsException(400, "Invalid second filter!", Instant.now().toString());
                    }
                }
            }
            default -> {
                throw new RtsException(400, "Invalid filter!", Instant.now().toString());
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
        scheduleSeatRepository.saveAll(seats);
    }

    public void freeUpSeat(Reservation reservation) {
        Schedule schedule = reservation.getSchedule();
        ScheduleSeat seat = reservation.getScheduleSeat();

        schedule.setCurrentCapacity(schedule.getCurrentCapacity() + 1);
        seat.setReserved(false);
        seat.setReservation(null);

        System.out.println("freed up seat: " + seat.getLabel() + "from schedule: " + schedule.getId());

        scheduleRepository.save(schedule);
        scheduleSeatRepository.save(seat);
    }

    public void handleException(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("status", String.valueOf(status));
        errorDetails.put("message", message);
        errorDetails.put("timestamp", String.valueOf(Instant.now()));

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorDetails));
        response.getWriter().flush();
    }


}
