package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.entity.Admin;
import org.project.railwayticketingservice.entity.AdminPrincipal;
import org.project.railwayticketingservice.entity.Passenger;
import org.project.railwayticketingservice.entity.PassengerPrincipal;
import org.project.railwayticketingservice.repository.AdminRepository;
import org.project.railwayticketingservice.repository.PassengerRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final PassengerRepository passengerRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Passenger passenger = passengerRepository.findPassengerByEmail(username);
        Admin admin = adminRepository.findAdminByEmail(username);

        if (passenger == null && admin != null) {
            return new AdminPrincipal(admin);
        } else if (passenger != null && admin == null) {
            return new PassengerPrincipal(passenger);
        } else if (passenger == null) {
            throw new BadCredentialsException("Invalid e-mail or password");
        }

        return new PassengerPrincipal(passenger);

    }
}
