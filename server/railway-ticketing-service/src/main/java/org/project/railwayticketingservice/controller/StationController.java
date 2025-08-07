package org.project.railwayticketingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.NewStationRequest;
import org.project.railwayticketingservice.dto.app.response.StationResponse;
import org.project.railwayticketingservice.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rts/station")
@Tag(name = "Station Endpoints", description = "This section addresses stations and their endpoints.")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @Operation(description = "This endpoint creates a new train station.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the train station"),
            @ApiResponse(responseCode = "500", description = "Internal Error creating train station"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "Train station already exists"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PostMapping("/new")
    public ResponseEntity<StationResponse> createStation(NewStationRequest request) {
        return stationService.createStation(request);
    }
}
