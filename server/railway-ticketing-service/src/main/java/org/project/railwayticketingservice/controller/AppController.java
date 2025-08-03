package org.project.railwayticketingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Main Features", description = "This documentation explains how the main features within this API work.")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    /* reservations */
    @Operation(description = "This endpoint creates a new reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the reservation"),
            @ApiResponse(responseCode = "500", description = "Internal Error creating reservation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "Reservation already exists for selected schedule"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PostMapping("/reservation/new")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody NewReservationRequest request) {
        return appService.createReservation(request);
    }

    @Operation(description = "This endpoint returns a requested reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the reservation"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting reservation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Reservation does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/reservation/{id}")
    public ResponseEntity<ReservationResponse> viewReservation(@PathVariable String id) {
        return appService.getReservation(id);
    }

    @Operation(description = "This endpoint returns all current reservations belonging to the passenger.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the reservations"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting reservations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Reservations do not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/reservation/all")
    public ResponseEntity<List<ReservationResponse>> viewAllReservations() {
        return appService.getAllReservations();
    }

    @Operation(description = "This endpoint deletes a reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the reservation"),
            @ApiResponse(responseCode = "500", description = "Internal Error deleting reservation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Reservation does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @DeleteMapping("/reservation/{id}")
    public ResponseEntity<AppResponse> cancelReservation(@PathVariable String id) {
        return appService.deleteReservation(id);
    }

    @Operation(description = "This endpoint deletes all current reservations belonging to the passenger.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the reservations"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting reservations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Reservations do not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @DeleteMapping("/reservation/all")
    public ResponseEntity<AppResponse> cancelAllReservations() {
        return appService.deleteAllReservations();
    }

    /* schedules */

    @Operation(description = "This endpoint returns all current schedules that fit the search bounds.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the schedules"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting schedules"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Schedules do not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/schedule/search")
    public ResponseEntity<List<TrainScheduleResponse>> searchTrainSchedules(
            @RequestParam String filter1,
            @RequestParam(required = false, defaultValue = "null") String filter2,
            @RequestParam(required = false, defaultValue = "null") String filter3,
            @RequestBody GetTrainScheduleRequest request) {
        return appService.getTrainSchedules(filter1, filter2, filter3, request);
    }

    @Operation(description = "This endpoint creates a new schedule.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the schedule"),
            @ApiResponse(responseCode = "500", description = "Internal Error creating schedule"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "Schedule already exists for selected train, time and origin"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PostMapping("/schedule/new")
    public ResponseEntity<AppResponse> createSchedule(@RequestBody ScheduleCreationRequest scheduleCreationRequest) {
        return appService.createSchedule(scheduleCreationRequest);
    }

    @Operation(description = "This endpoint returns a requested schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the schedule"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting schedule"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Schedule does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/schedule/{id}")
    public ResponseEntity<TrainScheduleResponse> viewTrainSchedule(@PathVariable String id) {
        return appService.getTrainSchedule(id);
    }

    @Operation(description = "This endpoint edits a specified schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully edited the schedule"),
            @ApiResponse(responseCode = "500", description = "Internal Error editing schedule"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Schedule does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PatchMapping("/schedule/edit/{id}")
    public ResponseEntity<TrainScheduleResponse> editTrainSchedule(@PathVariable String id, @RequestBody ScheduleUpdateRequest request) {
        return appService.editTrainSchedule(id, request);
    }

    /* trains */

    @Operation(description = "This endpoint creates a new train.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the train"),
            @ApiResponse(responseCode = "500", description = "Internal Error creating the train"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "train name conflict"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PostMapping("/train/new")
    public ResponseEntity<NewTrainResponse> createTrain(@RequestBody NewTrainRequest newTrainRequest) {
        return appService.createNewTrain(newTrainRequest);
    }

    @Operation(description = "This endpoint returns a requested train")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the train"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting train"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Train does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/train/{id}")
    public ResponseEntity<NewTrainResponse> getTrain(@PathVariable String id) {
        return appService.getTrain(id);
    }
}
