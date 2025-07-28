package org.project.railwayticketingservice.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterAdminRequest(
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

        @NotNull(message = "please provide a valid password")
        @NotBlank(message = "password field cannot be blank")
        @Size(max = 60, message = "password cannot be more than 60 characters")
        String password
) {
}
