package org.project.railwayticketingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.response.HomePageResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.project.railwayticketingservice.service.AppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rts/app")
@RequiredArgsConstructor
@Tag(name = "App", description = "This section documents specific application endpoints")
public class AppController {

    private final AppService appService;

    @Operation(description = "This endpoint returns necessary information for populating the homepage.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned homepage data"),
            @ApiResponse(responseCode = "500", description = "Error returning homepage data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/home")
    public ResponseEntity<HomePageResponse> home() {
        return appService.generateHomepage();
    }

    @Operation(description = "This endpoint returns upcoming schedules within the next hour.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned upcoming schedules"),
            @ApiResponse(responseCode = "500", description = "Error returning data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/schedules/upcoming")
    public ResponseEntity<List<TrainScheduleResponse>> getUpcomingTrainSchedules() {
        return appService.getUpcomingTrainSchedules();
    }
}
