package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;
import org.project.railwayticketingservice.entity.Train;

@Builder
public record NewTrainResponse(
        String trainId,
        String trainName,
        String isActive,
        String capacity
) {
    public static NewTrainResponse from(Train train) {
        return NewTrainResponse.builder()
                .trainId(train.getId().toString())
                .trainName(train.getName())
                .isActive(String.valueOf(train.isActive()))
                .capacity(String.valueOf(train.getCapacity()))
                .build();
    }
}
