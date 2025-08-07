package org.project.railwayticketingservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.NewStationRequest;
import org.project.railwayticketingservice.dto.app.response.StationResponse;
import org.project.railwayticketingservice.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rts/station")
@Tag(name = "Station Endpoints", description = "This section addresses stations and their endpoints.")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @PostMapping("/new")
    public ResponseEntity<StationResponse> createStation(NewStationRequest request) {
        return stationService.createStation(request);
    }
}
