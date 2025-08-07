package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.NewStationRequest;
import org.project.railwayticketingservice.dto.app.response.StationResponse;
import org.project.railwayticketingservice.entity.Station;
import org.project.railwayticketingservice.exception.RtsException;
import org.project.railwayticketingservice.repository.StationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    public ResponseEntity<StationResponse> createStation(NewStationRequest request) {

        if (!stationRepository.existsByName(request.name())) {  // continue only if station with said name does not exist
            Station station = Station.builder()
                    .name(request.name())
                    .code(request.code())
                    .LGA(request.LGA())
                    .isActive(true)
                    .build();
            System.out.println("created station: " + station.getName());
            stationRepository.save(station);
            return ResponseEntity.status(HttpStatus.CREATED).body(StationResponse.builder()
                            .id(station.getId())
                            .code(station.getCode())
                            .name(station.getName())
                            .lga(station.getLGA())
                    .build());
        } throw new RtsException(409, "Station with name " + request.name() + " already exists");
    }

    public ResponseEntity<StationResponse> deleteStation(Long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(() -> new RtsException(404, "Station with id " + stationId + " does not exist"));

        stationRepository.delete(station);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
