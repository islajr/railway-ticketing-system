package org.project.railwayticketingservice.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rts/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // passenger endpoints

    @PostMapping("/passenger/register")
    public ResponseEntity<RegisterPassengerResponse> registerPassenger(RegisterPassengerRequest request) {
        return authService.registerPassenger(request);
    }

    @PostMapping("/passenger/login")
    public ResponseEntity<LoginPassengerResponse> loginPassenger(LoginPassengerRequest request) {
        return authService.loginPassenger(request);
    }

    // admin endpoints

    @PostMapping("/admin/register")
    public ResponseEntity<RegisterAdminResponse> registerAdmin(RegisterAdminRequest request) {
        return authService.registerAdmin(request);
    }

    @PostMapping("/admin/login")
    public ResponseEntity<LoginAdminResponse> loginAdmin(LoginAdminRequest request) {
        return authService.loginAdmin(request);
    }
}
