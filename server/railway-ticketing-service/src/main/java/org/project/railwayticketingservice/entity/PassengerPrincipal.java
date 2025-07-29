package org.project.railwayticketingservice.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class PassengerPrincipal implements UserDetails {

    private final Passenger passenger;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_PASSENGER"));
    }

    public String getEmail() {
        return passenger.getEmail();
    }

    @Override
    public String getPassword() {
        return passenger.getPassword();
    }

    @Override
    public String getUsername() {
        return (passenger.getFirstName() + " " + passenger.getLastName());
    }

    public String getFirstName() {
        return passenger.getFirstName();
    }

    public String getLastName() {
        return passenger.getLastName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
