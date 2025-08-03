package org.project.railwayticketingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.auth.request.LoginAdminRequest;
import org.project.railwayticketingservice.dto.auth.request.LoginPassengerRequest;
import org.project.railwayticketingservice.dto.auth.request.RegisterAdminRequest;
import org.project.railwayticketingservice.dto.auth.request.RegisterPassengerRequest;
import org.project.railwayticketingservice.dto.auth.response.LoginAdminResponse;
import org.project.railwayticketingservice.dto.auth.response.LoginPassengerResponse;
import org.project.railwayticketingservice.dto.auth.response.RegisterAdminResponse;
import org.project.railwayticketingservice.dto.auth.response.RegisterPassengerResponse;
import org.project.railwayticketingservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rts/auth")
@Tag(name = "Authentication", description = "This documentation explains how authentication has been implemented within this API.")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // passenger endpoints

    @Operation(description = "This endpoint registers new passengers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the passenger"),
            @ApiResponse(responseCode = "500", description = "Error creating passenger"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "passenger or admin with e-mail already exists"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PostMapping("/passenger/register")
    public ResponseEntity<RegisterPassengerResponse> registerPassenger(@RequestBody RegisterPassengerRequest request) {
        return authService.registerPassenger(request);
    }

    @Operation(description = "This endpoint logs passengers into the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PostMapping("/passenger/login")
    public ResponseEntity<LoginPassengerResponse> loginPassenger(@RequestBody LoginPassengerRequest request) {
        return authService.loginPassenger(request);
    }

    // admin endpoints

    @Operation(description = "This endpoint registers new admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the admin"),
            @ApiResponse(responseCode = "500", description = "Error creating admin"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "passenger or admin with e-mail already exists"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PostMapping("/admin/register")
    public ResponseEntity<RegisterAdminResponse> registerAdmin(@RequestBody RegisterAdminRequest request) {
        return authService.registerAdmin(request);
    }

    @Operation(description = "This endpoint logs admins into the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @PostMapping("/admin/login")
    public ResponseEntity<LoginAdminResponse> loginAdmin(@RequestBody LoginAdminRequest request) {
        return authService.loginAdmin(request);
    }
}
