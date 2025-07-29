package org.project.railwayticketingservice.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.project.railwayticketingservice.entity.Passenger;

public record RegisterPassengerRequest(

    @NotNull(message = "please provide a valid firstName")
    @NotBlank(message = "firstName field cannot be blank")
    @Size(max = 50, message = "firstName cannot be more than 50 characters")
    String firstName,

    @NotNull(message = "please provide a valid lastName")
    @NotBlank(message = "lastName field cannot be blank")
    @Size(max = 50, message = "lastName cannot be more than 50 characters")
    String lastName,

    @NotNull(message = "please provide an e-mail address")
    @NotBlank(message = "e-mail address field cannot be blank")
    @Email(message = "please provide a valid e-mail address")
    @Size(max = 80, message = "e-mail address cannot be more than 80 characters")
    String email,

    @NotNull(message = "please input a proper password")
    @NotBlank(message = "password field cannot be blank")
    @Size(max = 60, message = "password cannot be more than 60 characters")
    String password,

    @NotNull(message = "please provide a valid country")
    @NotBlank(message = "'country' field cannot be blank")
    String country,

    @NotNull(message = "please provide a phone number")
    @NotBlank(message = "phone number field cannot be blank")
    @Size(max = 11, message = "please input your phone number in the format: (08023456789)")
    String phone
) {

    public  Passenger toPassenger() {
       return Passenger.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .country(country)
                .phone(phone)
                .build();
    }
}
