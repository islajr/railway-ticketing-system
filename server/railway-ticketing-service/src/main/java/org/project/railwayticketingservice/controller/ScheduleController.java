package org.project.railwayticketingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.GetTrainScheduleRequest;
import org.project.railwayticketingservice.dto.app.request.ScheduleCreationRequest;
import org.project.railwayticketingservice.dto.app.request.ScheduleUpdateRequest;
import org.project.railwayticketingservice.dto.app.response.AppResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.project.railwayticketingservice.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rts/app/schedule")
@Tag(name = "Schedule Endpoints", description = "This section provides clarity concerning Schedules and their endpoints.")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(description = "This endpoint returns all current schedules that fit the search bounds.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the schedules"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting schedules"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Schedules do not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/search")
    public ResponseEntity<List<TrainScheduleResponse>> searchTrainSchedules(
            @RequestParam(required=false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "1") int size,
            @RequestParam(required = false, defaultValue = "departureTime") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestBody GetTrainScheduleRequest request
    ) {
        return scheduleService.getTrainSchedules(page, size, sortBy, order, request);
    }

    @Operation(description = "This endpoint creates a new schedule.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the schedule"),
            @ApiResponse(responseCode = "500", description = "Internal Error creating schedule"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "Schedule already exists for selected train, time and origin"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PostMapping("/new")
    public ResponseEntity<AppResponse> createSchedule(@RequestBody ScheduleCreationRequest scheduleCreationRequest) {
        return scheduleService.createSchedule(scheduleCreationRequest);
    }

    @Operation(description = "This endpoint returns a requested schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the schedule"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting schedule"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Schedule does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/{id}")
    public ResponseEntity<TrainScheduleResponse> viewTrainSchedule(@PathVariable String id) {
        return scheduleService.getTrainSchedule(id);
    }

    @Operation(description = "This endpoint edits a specified schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully edited the schedule"),
            @ApiResponse(responseCode = "500", description = "Internal Error editing schedule"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Schedule does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PatchMapping("/{id}")
    public ResponseEntity<TrainScheduleResponse> editTrainSchedule(@PathVariable String id, @RequestBody ScheduleUpdateRequest request) {
        return scheduleService.editTrainSchedule(id, request);
    }

    @Operation(description = "This endpoint removes a train schedule.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the train schedule"),
            @ApiResponse(responseCode = "500", description = "Internal Error deleting the train schedule"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Train schedule does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse> deleteTrainSchedule(@PathVariable String id) {
        return scheduleService.deleteTrainSchedule(id);
    }
}
