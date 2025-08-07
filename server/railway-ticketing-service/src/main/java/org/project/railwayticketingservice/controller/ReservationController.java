package org.project.railwayticketingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.NewReservationRequest;
import org.project.railwayticketingservice.dto.app.response.AppResponse;
import org.project.railwayticketingservice.dto.app.response.ReservationResponse;
import org.project.railwayticketingservice.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rts/app/reservation")
@RequiredArgsConstructor
@Tag(name = "Reservation Endpoints", description = "This sections addresses everything concerning reservations.")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(description = "This endpoint creates a new reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the reservation"),
            @ApiResponse(responseCode = "500", description = "Internal Error creating reservation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "Reservation already exists for selected schedule"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PostMapping("/new")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody NewReservationRequest request) {
        return reservationService.createReservation(request);
    }

    @Operation(description = "This endpoint returns a requested reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the reservation"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting reservation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Reservation does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> viewReservation(@PathVariable String id) {
        return reservationService.getReservation(id);
    }

    @Operation(description = "This endpoint returns all current reservations belonging to the passenger.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the reservations"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting reservations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Reservations do not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @GetMapping("/all")
    public ResponseEntity<List<ReservationResponse>> viewAllReservations() {
        return reservationService.getAllReservations();
    }

    @Operation(description = "This endpoint deletes a reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the reservation"),
            @ApiResponse(responseCode = "500", description = "Internal Error deleting reservation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Reservation does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse> cancelReservation(@PathVariable String id) {
        return reservationService.deleteReservation(id);
    }

    @Operation(description = "This endpoint deletes all current reservations belonging to the passenger.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the reservations"),
            @ApiResponse(responseCode = "500", description = "Internal Error getting reservations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Reservations do not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @DeleteMapping("/all")
    public ResponseEntity<AppResponse> cancelAllReservations() {
        return reservationService.deleteAllReservations();
    }


}
