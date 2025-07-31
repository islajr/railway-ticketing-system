package org.project.railwayticketingservice.controller;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.GetTrainScheduleRequest;
import org.project.railwayticketingservice.dto.app.request.NewReservationRequest;
import org.project.railwayticketingservice.dto.app.response.ReservationResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.project.railwayticketingservice.service.AppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rts/app")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @PostMapping("/reservation/new")
    public ResponseEntity<ReservationResponse> createReservation(NewReservationRequest request) {
        return appService.createReservation(request);
    }

    @GetMapping("/reservation/{id}")
    public ResponseEntity<ReservationResponse> viewReservation(@PathVariable String id) {
        return appService.getReservation(id);
    }

    @GetMapping("/reservation/all")
    public ResponseEntity<List<ReservationResponse>> viewAllReservations() {
        return appService.getAllReservations();
    }

    @DeleteMapping("/reservation/{id}")
    public ResponseStatus cancelReservation(@PathVariable String id) {
        return appService.deleteReservation(id);
    }

    @DeleteMapping("/reservation/all")
    public ResponseStatus cancelAllReservations() {
        return appService.deleteAllReservations();
    }

    @GetMapping("/schedule/search")
    public ResponseEntity<List<TrainScheduleResponse>> searchTrainSchedules(
            @RequestParam String filter1,
            @RequestParam(required = false, defaultValue = "null") String filter2,
            @RequestParam(required = false, defaultValue = "null") String filter3,
            GetTrainScheduleRequest request) {
        return appService.getTrainSchedules(filter1, filter2, filter3, request);
    }
}
