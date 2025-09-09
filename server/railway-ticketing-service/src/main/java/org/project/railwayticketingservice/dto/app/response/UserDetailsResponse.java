package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;
import org.project.railwayticketingservice.entity.Passenger;

@Builder
public record UserDetailsResponse(
        String firstName,
        String lastName
) {
    public static UserDetailsResponse from(Passenger passenger) {
        return UserDetailsResponse.builder()
                .firstName(passenger.getFirstName())
                .lastName(passenger.getLastName())
                .build();
    }
}
