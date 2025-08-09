package org.project.railwayticketingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.NewStationRequest;
import org.project.railwayticketingservice.dto.app.request.StationUpdateRequest;
import org.project.railwayticketingservice.dto.app.response.StationResponse;
import org.project.railwayticketingservice.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(description = "This endpoint removes a new train station.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the train station"),
            @ApiResponse(responseCode = "500", description = "Internal Error deleting train station"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Train station does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @DeleteMapping("/{id}")
    public ResponseEntity<StationResponse> deleteStation(@PathVariable Long id) {
        return stationService.deleteStation(id);
    }

    @Operation(description = "This endpoint updates a given train station.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the train station"),
            @ApiResponse(responseCode = "500", description = "Internal Error updating train station"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Train station does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PatchMapping("/{id}")
    public ResponseEntity<StationResponse> updateStation(@PathVariable Long id, @RequestBody StationUpdateRequest request) {
        return stationService.updateStation(id, request);
    }

    @Operation(description = "This endpoint returns a train station.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the train station"),
            @ApiResponse(responseCode = "500", description = "Internal Error retrieving train station"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Train station does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/{id}")
    public ResponseEntity<StationResponse> getStation(@PathVariable Long id) {
        return stationService.getStation(id);
    }

    @Operation(description = "This endpoint returns all registered train stations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all train stations"),
            @ApiResponse(responseCode = "500", description = "Internal Error retrieving train stations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Train stations does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/all")
    public ResponseEntity<List<StationResponse>> getAllStations() {
        return stationService.getAllStations();
    }
}
