package org.project.railwayticketingservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.railwayticketingservice.entity.Reservation;
import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.ScheduleSeat;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.project.railwayticketingservice.repository.ScheduleSeatRepository;
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
