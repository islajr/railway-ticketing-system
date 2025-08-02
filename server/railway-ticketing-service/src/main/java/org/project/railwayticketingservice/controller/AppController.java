package org.project.railwayticketingservice.controller;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.*;
import org.project.railwayticketingservice.dto.app.response.AppResponse;
import org.project.railwayticketingservice.dto.app.response.NewTrainResponse;
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

    /* reservations */
    @PostMapping("/reservation/new")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody NewReservationRequest request) {
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
    public ResponseEntity<AppResponse> cancelReservation(@PathVariable String id) {
        return appService.deleteReservation(id);
    }

    @DeleteMapping("/reservation/all")
    public ResponseEntity<AppResponse> cancelAllReservations() {
        return appService.deleteAllReservations();
    }

    /* schedules */
    @GetMapping("/schedule/search")
    public ResponseEntity<List<TrainScheduleResponse>> searchTrainSchedules(
            @RequestParam String filter1,
            @RequestParam(required = false, defaultValue = "null") String filter2,
            @RequestParam(required = false, defaultValue = "null") String filter3,
            @RequestBody GetTrainScheduleRequest request) {
        return appService.getTrainSchedules(filter1, filter2, filter3, request);
    }

    @PostMapping("/schedule/new")
    public ResponseEntity<AppResponse> createSchedule(@RequestBody ScheduleCreationRequest scheduleCreationRequest) {
        return appService.createSchedule(scheduleCreationRequest);
    }

    @GetMapping("/schedule/{id}")
    public ResponseEntity<TrainScheduleResponse> viewTrainSchedule(@PathVariable String id) {
        return appService.getTrainSchedule(id);
    }

    @PatchMapping("/schedule/edit/{id}")
    public ResponseEntity<TrainScheduleResponse> editTrainSchedule(@PathVariable String id, @RequestBody ScheduleUpdateRequest request) {
        return appService.editTrainSchedule(id, request);
    }

    /* trains */
    @PostMapping("/train/new")
    public ResponseEntity<NewTrainResponse> createTrain(@RequestBody NewTrainRequest newTrainRequest) {
        return appService.createNewTrain(newTrainRequest);
    }

    @GetMapping("/train/{id}")
    public ResponseEntity<NewTrainResponse> getTrain(@PathVariable String id) {
        return appService.getTrain(id);
    }
}
