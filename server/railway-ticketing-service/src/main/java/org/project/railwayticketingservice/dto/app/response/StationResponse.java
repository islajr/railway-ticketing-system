package org.project.railwayticketingservice.dto.app.response;

import lombok.Builder;
import org.project.railwayticketingservice.entity.Station;

@Builder
public record StationResponse(
        Long id,
        String name,
        String code,
        String lga
) {
    public static StationResponse from(Station station) {
        return StationResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .code(station.getCode())
                .lga(station.getLGA())
                .build();
    }
}
