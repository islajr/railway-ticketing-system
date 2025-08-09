package org.project.railwayticketingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.NewTrainRequest;
import org.project.railwayticketingservice.dto.app.request.TrainUpdateRequest;
import org.project.railwayticketingservice.dto.app.response.NewTrainResponse;
import org.project.railwayticketingservice.service.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rts/app/train")
@Tag(name = "Train Endpoints", description = "This section addresses everything about trains and their endpoints.")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @Operation(description = "This endpoint creates a new train.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the train"),
            @ApiResponse(responseCode = "500", description = "Internal Error creating the train"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "train name conflict"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PostMapping("/new")
    public ResponseEntity<NewTrainResponse> createTrain(@RequestBody NewTrainRequest newTrainRequest) {
        return trainService.createNewTrain(newTrainRequest);
    }

    @Operation(description = "This endpoint returns a requested train")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the train"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting train"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Train does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/{id}")
    public ResponseEntity<NewTrainResponse> getTrain(@PathVariable String id) {
        return trainService.getTrain(id);
    }

    @Operation(description = "This endpoint returns all registered trains")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned trains"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting trains"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Trains do not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/all")
    public ResponseEntity<List<NewTrainResponse>> getAllTrains() {
        return trainService.getAllTrains();
    }

    @Operation(description = "This endpoint edits a given train.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the train"),
            @ApiResponse(responseCode = "500", description = "Internal Error updating train"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Train does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PatchMapping("/{id}")
    public ResponseEntity<NewTrainResponse> updateTrain(@PathVariable String id, @RequestBody TrainUpdateRequest request) {
        return trainService.updateTrain(id, request);
    }

    @Operation(description = "This endpoint removes a train.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully removed the train"),
            @ApiResponse(responseCode = "500", description = "Internal Error removing train"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Train does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @DeleteMapping("/{id}")
    public ResponseEntity<NewTrainResponse> removeTrain(@PathVariable String id) {
        return trainService.removeTrain(id);
    }
}
