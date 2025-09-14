package org.project.railwayticketingservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.project.railwayticketingservice.dto.app.response.HomePageResponse;
import org.project.railwayticketingservice.dto.app.response.ReservationResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.project.railwayticketingservice.dto.app.response.UserDetailsResponse;
import org.project.railwayticketingservice.entity.Passenger;
import org.project.railwayticketingservice.entity.PassengerPrincipal;
import org.project.railwayticketingservice.entity.Reservation;
import org.project.railwayticketingservice.entity.Schedule;
import org.project.railwayticketingservice.repository.PassengerRepository;
import org.project.railwayticketingservice.repository.ScheduleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppService {

    private final PassengerRepository passengerRepository;
    private final ScheduleRepository scheduleRepository;

    public ResponseEntity<HomePageResponse> generateHomepage() {
        String email = ((PassengerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        Passenger passenger = passengerRepository.findPassengerByEmail(email);

        List<Reservation> reservations = passenger.getReservations();
        List<Schedule> upcomingSchedules = scheduleRepository.findSchedulesByDepartureTimeBetween(LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        /* response prepping */
        UserDetailsResponse userDetailsResponse = UserDetailsResponse.from(passenger);
        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(ReservationResponse::from)
                .toList();
        List<TrainScheduleResponse> upcomingSchedulesResponse = upcomingSchedules.stream()
                .map(
                        TrainScheduleResponse::fromSchedule
                )
                .toList();
        return ResponseEntity.ok(HomePageResponse.of(userDetailsResponse, reservationResponses, upcomingSchedulesResponse));

    }

    /* may later include location specificity */
    public ResponseEntity<List<TrainScheduleResponse>> getUpcomingTrainSchedules() {
        List<Schedule> upcomingSchedules = scheduleRepository.findSchedulesByDepartureTimeBetween(LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        return ResponseEntity.ok(upcomingSchedules.stream()
                .map(TrainScheduleResponse::fromSchedule)
                .toList());
    }
}
