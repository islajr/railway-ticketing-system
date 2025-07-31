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
import org.project.railwayticketingservice.entity.Admin;
import org.project.railwayticketingservice.entity.Passenger;
import org.project.railwayticketingservice.exception.RtsException;
import org.project.railwayticketingservice.repository.AdminRepository;
import org.project.railwayticketingservice.repository.PassengerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PassengerRepository passengerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public ResponseEntity<RegisterPassengerResponse> registerPassenger(RegisterPassengerRequest request) {

        if (adminRepository.existsByEmail(request.email()) || passengerRepository.existsByEmail(request.email())) {
            throw new RtsException(409, "Email already in use");
        }

        Passenger passenger = request.toPassenger();
        passenger.setPassword(passwordEncoder.encode(passenger.getPassword()));
        passengerRepository.save(passenger);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    public ResponseEntity<LoginPassengerResponse> loginPassenger(LoginPassengerRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Passenger passenger = passengerRepository.findPassengerByEmail(request.email());

        if (passenger != null) {

            if (authentication.isAuthenticated()) {
                String email = passenger.getEmail();
                return ResponseEntity.ok(new LoginPassengerResponse(
                        jwtService.generateToken(email),
                        jwtService.generateRefreshToken(email)
                        ));
            }
        }

        throw new RtsException(400, "Invalid email or password");
    }

    public ResponseEntity<RegisterAdminResponse> registerAdmin(RegisterAdminRequest request) {

        if (adminRepository.existsByEmail(request.email()) || passengerRepository.existsByEmail(request.email())) {
            throw new RtsException(409, "Email already in use");
        }

        Admin admin = request.toAdmin();
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<LoginAdminResponse> loginAdmin(LoginAdminRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Admin admin = adminRepository.findAdminByEmail(request.email());

        if (admin != null) {

            if (authentication.isAuthenticated()) {
                return ResponseEntity.ok(new LoginAdminResponse());
            }
        }

        throw new RtsException(400, "Invalid email or password");
    }
}
