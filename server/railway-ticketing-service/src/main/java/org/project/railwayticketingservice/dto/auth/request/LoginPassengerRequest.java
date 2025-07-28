package org.project.railwayticketingservice.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginPassengerRequest(
        @NotNull
        @NotBlank
        @Email(message = "please provide a valid e-mail address")
        @Size(max = 80, message = "e-mail cannot be more than 80 characters")
        String email,

        @NotNull(message = "password field cannot be null")
        @NotBlank(message = "please provide a password")
        @Size(max = 60, message = "password cannot be more than 60 characters")
        String password
) {
}
