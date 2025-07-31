package org.project.railwayticketingservice.util;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.GetTrainScheduleRequest;
import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Component
public class Utilities {

    private final ScheduleRepository scheduleRepository;

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

}
