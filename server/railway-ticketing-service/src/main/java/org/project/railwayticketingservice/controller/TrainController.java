package org.project.railwayticketingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.NewTrainRequest;
import org.project.railwayticketingservice.dto.app.response.NewTrainResponse;
import org.project.railwayticketingservice.service.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
