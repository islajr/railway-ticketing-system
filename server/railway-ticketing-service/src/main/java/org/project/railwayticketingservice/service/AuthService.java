package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.auth.request.LoginAdminRequest;
import org.project.railwayticketingservice.dto.auth.request.LoginPassengerRequest;
import org.project.railwayticketingservice.dto.auth.request.RegisterAdminRequest;
import org.project.railwayticketingservice.dto.auth.request.RegisterPassengerRequest;
import org.project.railwayticketingservice.dto.auth.response.LoginAdminResponse;
import org.project.railwayticketingservice.dto.auth.response.LoginPassengerResponse;
import org.project.railwayticketingservice.dto.auth.response.RegisterAdminResponse;
import org.project.railwayticketingservice.dto.auth.response.RegisterPassengerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    public ResponseEntity<RegisterPassengerResponse> registerPassenger(RegisterPassengerRequest request) {
    }

    public ResponseEntity<LoginPassengerResponse> loginPassenger(LoginPassengerRequest request) {
    }

    public ResponseEntity<RegisterAdminResponse> registerAdmin(RegisterAdminRequest request) {
    }

    public ResponseEntity<LoginAdminResponse> loginAdmin(LoginAdminRequest request) {
    }
}
