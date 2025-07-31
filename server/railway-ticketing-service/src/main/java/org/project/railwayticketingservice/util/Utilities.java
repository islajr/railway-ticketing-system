package org.project.railwayticketingservice.util;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.GetTrainScheduleRequest;
import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.entity.ScheduleSeat;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.project.railwayticketingservice.repository.ScheduleSeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class Utilities {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;

    public List<Schedule> getSchedules(String filter, GetTrainScheduleRequest request){

        switch (filter) {
            case "origin" -> {
                return scheduleRepository.findSchedulesByOrigin(request.origin());
            }
            case "destination" -> {
                return scheduleRepository.findSchedulesByDestination(request.destination());
            }

            case "time" -> {
                return scheduleRepository.findSchedulesByDepartureTime(request.time().getLocalDateTime());
            }
            default -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filter!");
            }
        }
    }

    public List<Schedule> getSchedules(String filter1, String filter2, GetTrainScheduleRequest request){
        switch (filter1) {
            case "origin" -> {     // second filter can either be destination or time
                switch (filter2) {
                    case "destination" -> {
                        return scheduleRepository.findSchedulesByOriginAndDestination(request.origin(), request.destination());
                    }
                    case "time" -> {
                        return scheduleRepository.findSchedulesByOriginAndDepartureTime(request.origin(), request.time().getLocalDateTime());
                    }
                    default -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid second filter!");
                    }
                }

            }
            case "destination" -> {     // second filter can either be origin or time
                switch (filter2) {
                    case "origin" -> {
                        return scheduleRepository.findSchedulesByOriginAndDestination(request.origin(), request.destination());
                    }
                    case "time" -> {
                        return scheduleRepository.findSchedulesByOriginAndDepartureTime(request.origin(), request.time().getLocalDateTime());
                    }
                    default -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid second filter!");
                    }
                }
            }
            case "time" -> {    // second filter can either be origin or destination
                switch (filter2) {
                    case "origin" -> {
                        return scheduleRepository.findSchedulesByOriginAndDepartureTime(request.origin(), request.time().getLocalDateTime());
                    }
                    case "destination" -> {
                        return scheduleRepository.findSchedulesByDestinationAndDepartureTime(request.origin(), request.time().getLocalDateTime());
                    }
                    default -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid second filter!");
                    }
                }
            }
            default -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filter!");
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


}
