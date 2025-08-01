package org.project.railwayticketingservice.dto.app.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewTrainRequest(

        @NotNull(message = "train name cannot be null")
        @NotBlank(message = "please provide a valid train name")
        String name,

        @NotNull(message = "train capacity cannot be null")
        @NotBlank(message = "please provide a train capacity")
        String capacity    // 1040-seater
) {
}
